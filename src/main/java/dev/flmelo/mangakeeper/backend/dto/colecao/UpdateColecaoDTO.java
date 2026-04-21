package dev.flmelo.mangakeeper.backend.dto.colecao;

import jakarta.validation.constraints.NotBlank;

public record UpdateColecaoDTO(@NotBlank String nome) {
}
