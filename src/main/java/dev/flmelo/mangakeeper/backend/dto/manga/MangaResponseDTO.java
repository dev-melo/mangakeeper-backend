package dev.flmelo.mangakeeper.backend.dto.manga;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.flmelo.mangakeeper.backend.entity.enuns.Idioma;

public record MangaResponseDTO(
        Long id,
        String titulo,
        String autor,
        String editora,
        String genero,
        String sinopse,
        String issn,
        @JsonProperty(value = "total_volumes")
        Integer totalVolumes,
        Idioma idioma,
        @JsonProperty("colecao_id")
        Long colecaoId
) {
}
