package com.kata.rules.rule;

import com.kata.rules.dto.ReleaseRequestDTO;

/**
 * Strategy interface for release rules.
 * Each implementation represents a single business rule.
 */
public interface ReleaseRule {

    /**
     * Evaluates the rule against the given release request.
     *
     * @param request the release request to evaluate
     * @return null if the rule passes, or a descriptive message if it fails
     */
    String evaluate(ReleaseRequestDTO request);
}
