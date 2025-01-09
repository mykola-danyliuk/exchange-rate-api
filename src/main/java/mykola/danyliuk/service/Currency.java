package mykola.danyliuk.service;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Currency information")
public record Currency(
    @Schema(description = "Currency code", example = "USD") String code,
    @Schema(description = "Currency name", example = "United States Dollar") String name) {

    public Currency {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Currency code cannot be null or empty");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Currency name cannot be null or empty");
        }
    }
}
