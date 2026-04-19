package com.kata.rules.dto;

/**
 * DTO recibido por el Rules Service para evaluación de reglas.
 */
public record ReleaseRequestDTO(
        String equipo,
        String tipo,
        String descripcion,
        String prId,
        boolean prExists,
        double cobertura,
        String stack
) {}
