package com.kata.rules.dto;

import java.util.List;

/**
 * Resultado de la evaluación de reglas.
 */
public record EvaluationResultDTO(boolean approved, List<String> reasons) {}
