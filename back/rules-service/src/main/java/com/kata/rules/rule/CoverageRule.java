package com.kata.rules.rule;

import com.kata.rules.dto.ReleaseRequestDTO;
import org.springframework.stereotype.Component;

/**
 * Rule: test coverage must be >= 80%.
 */
@Component
public class CoverageRule implements ReleaseRule {

    private static final double MIN_COVERAGE = 80.0;

    @Override
    public String evaluate(ReleaseRequestDTO request) {
        if (request.cobertura() < MIN_COVERAGE) {
            return String.format("Cobertura insuficiente: %.1f%% (mínimo requerido: %.1f%%)",
                    request.cobertura(), MIN_COVERAGE);
        }
        return null;
    }
}
