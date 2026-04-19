package com.kata.release.service;

import com.kata.release.dto.CreateReleaseDTO;
import com.kata.release.dto.ReleaseResponseDTO;
import com.kata.release.model.ReleaseRequest;
import com.kata.release.repository.ReleaseRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReleaseService {

    private static final Logger log = LoggerFactory.getLogger(ReleaseService.class);

    private final ReleaseRequestRepository repository;
    private final RestTemplate restTemplate;

    @Value("${integration.service.url}")
    private String integrationServiceUrl;

    @Value("${rules.service.url}")
    private String rulesServiceUrl;

    @Value("${notification.service.url}")
    private String notificationServiceUrl;

    public ReleaseService(ReleaseRequestRepository repository, RestTemplate restTemplate) {
        this.repository = repository;
        this.restTemplate = restTemplate;
    }

    public ReleaseResponseDTO create(CreateReleaseDTO dto) {
        log.info("[ReleaseService] Processing new release for team: {}, type: {}", dto.equipo(), dto.tipo());

        boolean prExists = false;
        if (dto.prId() != null && !dto.prId().isBlank()) {
            try {
                String url = integrationServiceUrl + "/integration/pr/" + dto.prId();
                Map<?, ?> prResponse = restTemplate.getForObject(url, Map.class);
                prExists = prResponse != null && Boolean.TRUE.equals(prResponse.get("exists"));
                log.info("[ReleaseService] PR check result for {}: exists={}", dto.prId(), prExists);
            } catch (Exception e) {
                log.error("[ReleaseService] Integration Service unreachable: {}", e.getMessage());
            }
        }

        Map<String, Object> rulesRequest = Map.of(
                "equipo", dto.equipo(),
                "tipo", dto.tipo(),
                "descripcion", dto.descripcion() != null ? dto.descripcion() : "",
                "prId", dto.prId() != null ? dto.prId() : "",
                "prExists", prExists,
                "cobertura", dto.cobertura() != null ? dto.cobertura() : 0.0,
                "stack", dto.stack() != null ? dto.stack() : ""
        );

        boolean approved = false;
        List<String> reasons = List.of();

        try {
            String rulesUrl = rulesServiceUrl + "/rules/evaluate";
            Map<?, ?> evalResult = restTemplate.postForObject(rulesUrl, rulesRequest, Map.class);
            if (evalResult != null) {
                approved = Boolean.TRUE.equals(evalResult.get("approved"));
                Object rawReasons = evalResult.get("reasons");
                if (rawReasons instanceof List<?> list) {
                    reasons = list.stream().map(Object::toString).collect(Collectors.toList());
                }
            }
            log.info("[ReleaseService] Rules evaluation: approved={}, reasons={}", approved, reasons);
        } catch (Exception e) {
            log.error("[ReleaseService] Rules Service unreachable: {}", e.getMessage());
        }

        
        String estado = approved ? "APROBADO_AUTO" : "PENDIENTE";
        String tipoAprobacion = approved ? "AUTO" : "MANUAL";

        if (!approved) {
            sendNotification(dto, reasons);
        }

        ReleaseRequest entity = new ReleaseRequest();
        entity.setFecha(LocalDateTime.now());
        entity.setEquipo(dto.equipo());
        entity.setTipo(dto.tipo());
        entity.setDescripcion(dto.descripcion());
        entity.setPrId(dto.prId());
        entity.setCobertura(dto.cobertura());
        entity.setStack(dto.stack());
        entity.setEstado(estado);
        entity.setTipoAprobacion(tipoAprobacion);
        entity.setRazonesRechazo(reasons.isEmpty() ? null : String.join("|", reasons));

        ReleaseRequest saved = repository.save(entity);
        log.info("[ReleaseService] Release persisted with id: {}, estado: {}", saved.getId(), estado);

        return toDTO(saved);
    }

    public List<ReleaseResponseDTO> findAll() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    private void sendNotification(CreateReleaseDTO dto, List<String> reasons) {
        try {
            String to = dto.notificationEmail() != null && !dto.notificationEmail().isBlank()
                    ? dto.notificationEmail()
                    : "admin@releases.local";

            String body = "El release del equipo '" + dto.equipo() + "' requiere aprobación manual.\n\n"
                    + "Razones:\n"
                    + reasons.stream().map(r -> "  - " + r).collect(Collectors.joining("\n"));

            Map<String, String> notifRequest = Map.of(
                    "to", to,
                    "subject", "[RELEASE] Aprobación manual requerida - " + dto.equipo(),
                    "body", body
            );

            String notifUrl = notificationServiceUrl + "/notifications/send";
            restTemplate.postForObject(notifUrl, notifRequest, Map.class);
            log.info("[ReleaseService] Notification sent to: {}", to);
        } catch (Exception e) {
            log.error("[ReleaseService] Failed to send notification: {}", e.getMessage());
        }
    }

    private ReleaseResponseDTO toDTO(ReleaseRequest entity) {
        List<String> reasons = List.of();
        if (entity.getRazonesRechazo() != null && !entity.getRazonesRechazo().isBlank()) {
            reasons = Arrays.asList(entity.getRazonesRechazo().split("\\|"));
        }
        return new ReleaseResponseDTO(
                entity.getId(),
                entity.getFecha(),
                entity.getEquipo(),
                entity.getTipo(),
                entity.getDescripcion(),
                entity.getPrId(),
                entity.getCobertura(),
                entity.getStack(),
                entity.getEstado(),
                entity.getTipoAprobacion(),
                reasons
        );
    }
}
