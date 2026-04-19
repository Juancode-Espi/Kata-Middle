package com.kata.rules.rule;

import com.kata.rules.dto.ReleaseRequestDTO;
import org.springframework.stereotype.Component;

/**
 * Rule: a PR or JIRA story must be present and verified.
 */
@Component
public class PrOrJiraRule implements ReleaseRule {

    @Override
    public String evaluate(ReleaseRequestDTO request) {
        boolean hasPrId = request.prId() != null && !request.prId().isBlank();
        if (!hasPrId) {
            return "Se requiere un PR o historia de JIRA asociada al release.";
        }
        if (!request.prExists()) {
            return "El PR indicado (" + request.prId() + ") no existe en el repositorio.";
        }
        return null;
    }
}
