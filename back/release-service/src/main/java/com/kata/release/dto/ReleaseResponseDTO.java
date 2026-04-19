package com.kata.release.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ReleaseResponseDTO(
        UUID id,
        LocalDateTime fecha,
        String equipo,
        String tipo,
        String descripcion,
        String prId,
        Double cobertura,
        String stack,
        String estado,
        String tipoAprobacion,
        List<String> razonesRechazo
) {}
