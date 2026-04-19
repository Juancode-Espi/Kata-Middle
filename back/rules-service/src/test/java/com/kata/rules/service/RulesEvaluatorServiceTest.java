package com.kata.rules.service;

import com.kata.rules.dto.EvaluationResultDTO;
import com.kata.rules.dto.ReleaseRequestDTO;
import com.kata.rules.rule.CoverageRule;
import com.kata.rules.rule.DescriptionRule;
import com.kata.rules.rule.PrOrJiraRule;
import com.kata.rules.rule.StackObsolescenceRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class RulesEvaluatorServiceTest {

    private RulesEvaluatorService service;
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        service = new RulesEvaluatorService(List.of(
                new CoverageRule(),
                new DescriptionRule(),
                new PrOrJiraRule(),
                new StackObsolescenceRule(restTemplate)
        ));
    }

    private ReleaseRequestDTO validRsRequest() {
        return new ReleaseRequestDTO(
                "team-alpha", "rs", "Release de nueva funcionalidad",
                "PR-123", true, 85.0, "Java 17 + Spring Boot 3"
        );
    }

    @Test
    @DisplayName("Tipo != rs debe aprobarse automáticamente sin evaluar reglas")
    void nonRsType_shouldAutoApprove() {
        ReleaseRequestDTO fx = new ReleaseRequestDTO(
                "team-alpha", "fx", "", null, false, 0.0, null
        );
        EvaluationResultDTO result = service.evaluate(fx);
        assertThat(result.approved()).isTrue();
        assertThat(result.reasons()).isEmpty();
    }

    @Test
    @DisplayName("RS con todos los criterios cumplidos debe aprobarse")
    void rsType_allRulesPassing_shouldApprove() {
        EvaluationResultDTO result = service.evaluate(validRsRequest());
        assertThat(result.approved()).isTrue();
        assertThat(result.reasons()).isEmpty();
    }

    @Test
    @DisplayName("RS con cobertura < 80 debe rechazarse")
    void rsType_lowCoverage_shouldReject() {
        ReleaseRequestDTO request = new ReleaseRequestDTO(
                "team-alpha", "rs", "Descripción válida",
                "PR-123", true, 75.0, "Java 17"
        );
        EvaluationResultDTO result = service.evaluate(request);
        assertThat(result.approved()).isFalse();
        assertThat(result.reasons()).anyMatch(r -> r.contains("Cobertura insuficiente"));
    }

    @Test
    @DisplayName("RS con descripción vacía debe rechazarse")
    void rsType_emptyDescription_shouldReject() {
        ReleaseRequestDTO request = new ReleaseRequestDTO(
                "team-alpha", "rs", "",
                "PR-123", true, 90.0, "Java 17"
        );
        EvaluationResultDTO result = service.evaluate(request);
        assertThat(result.approved()).isFalse();
        assertThat(result.reasons()).anyMatch(r -> r.contains("descripción"));
    }

    @Test
    @DisplayName("RS sin PR/JIRA debe rechazarse")
    void rsType_noPrId_shouldReject() {
        ReleaseRequestDTO request = new ReleaseRequestDTO(
                "team-alpha", "rs", "Descripción válida",
                null, false, 85.0, "Java 17"
        );
        EvaluationResultDTO result = service.evaluate(request);
        assertThat(result.approved()).isFalse();
        assertThat(result.reasons()).anyMatch(r -> r.contains("PR o historia"));
    }

    @Test
    @DisplayName("RS con PR no verificado debe rechazarse")
    void rsType_prNotVerified_shouldReject() {
        ReleaseRequestDTO request = new ReleaseRequestDTO(
                "team-alpha", "rs", "Descripción válida",
                "PR-999", false, 85.0, "Java 17"
        );
        EvaluationResultDTO result = service.evaluate(request);
        assertThat(result.approved()).isFalse();
        assertThat(result.reasons()).anyMatch(r -> r.contains("no existe"));
    }

    @Test
    @DisplayName("RS con stack obsoleto debe rechazarse")
    void rsType_obsoleteStack_shouldReject() {
        ReleaseRequestDTO request = new ReleaseRequestDTO(
                "team-alpha", "rs", "Descripción válida",
                "PR-123", true, 85.0, "Java 8 + Spring Boot 1"
        );
        EvaluationResultDTO result = service.evaluate(request);
        assertThat(result.approved()).isFalse();
        assertThat(result.reasons()).anyMatch(r -> r.contains("obsoleta"));
    }

    @Test
    @DisplayName("RS con múltiples fallos debe retornar todas las razones")
    void rsType_multipleFailures_shouldReturnAllReasons() {
        ReleaseRequestDTO request = new ReleaseRequestDTO(
                "team-alpha", "rs", "",
                null, false, 50.0, "Java 8"
        );
        EvaluationResultDTO result = service.evaluate(request);
        assertThat(result.approved()).isFalse();
        assertThat(result.reasons()).hasSizeGreaterThanOrEqualTo(3);
    }
}
