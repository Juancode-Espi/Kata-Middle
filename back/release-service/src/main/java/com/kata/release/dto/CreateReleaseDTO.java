package com.kata.release.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateReleaseDTO(
        @NotBlank String equipo,
        @NotNull String tipo,
        @NotBlank String descripcion,
        String prId,
        Double cobertura,
        String stack,
        String notificationEmail
) {}
