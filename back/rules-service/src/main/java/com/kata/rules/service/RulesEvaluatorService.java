package com.kata.rules.service;

import com.kata.rules.dto.EvaluationResultDTO;
import com.kata.rules.dto.ReleaseRequestDTO;
import com.kata.rules.rule.ReleaseRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Orchestrates all ReleaseRule implementations using the Strategy pattern.
 * Rules only apply when tipo = "rs".
 */
@Service
public class RulesEvaluatorService {

    private static final Logger log = LoggerFactory.getLogger(RulesEvaluatorService.class);

    private final List<ReleaseRule> rules;

    public RulesEvaluatorService(List<ReleaseRule> rules) {
        this.rules = rules;
    }

    public EvaluationResultDTO evaluate(ReleaseRequestDTO request) {
        log.info("[RulesService] Evaluating release for team: {}, type: {}", request.equipo(), request.tipo());

        if (!"rs".equalsIgnoreCase(request.tipo())) {
            log.info("[RulesService] Type is '{}' — auto-approved without rule evaluation.", request.tipo());
            return new EvaluationResultDTO(true, List.of());
        }

        List<String> failures = new ArrayList<>();
        for (ReleaseRule rule : rules) {
            String result = rule.evaluate(request);
            if (result != null) {
                log.warn("[RulesService] Rule failed: {}", result);
                failures.add(result);
            }
        }

        boolean approved = failures.isEmpty();
        log.info("[RulesService] Evaluation complete. Approved: {}. Failures: {}", approved, failures.size());
        return new EvaluationResultDTO(approved, failures);
    }
}
