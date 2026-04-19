package com.kata.rules.rule;

import com.kata.rules.dto.ReleaseRequestDTO;
import org.springframework.stereotype.Component;

/**
 * Rule: description must not be empty.
 */
@Component
public class DescriptionRule implements ReleaseRule {

    @Override
    public String evaluate(ReleaseRequestDTO request) {
        if (request.descripcion() == null || request.descripcion().isBlank()) {
            return "La descripción del release no puede estar vacía.";
        }
        return null;
    }
}
