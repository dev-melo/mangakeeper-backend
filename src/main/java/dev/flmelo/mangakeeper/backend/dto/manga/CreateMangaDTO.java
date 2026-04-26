package dev.flmelo.mangakeeper.backend.dto.manga;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.flmelo.mangakeeper.backend.entity.enuns.Idioma;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateMangaDTO(
        @NotBlank String titulo,
        @NotBlank String autor,
        @NotBlank String editora,
        @NotBlank String genero,
        @NotBlank String sinopse,
        String issn,
        @JsonProperty("total_volumes")
        @NotNull Integer totalVolumes,
        @NotNull Idioma idioma,

        @JsonProperty("colecao_id") @NotNull Long colecaoId

        ) {
}
