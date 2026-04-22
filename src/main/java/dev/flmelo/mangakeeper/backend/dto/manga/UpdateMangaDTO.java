package dev.flmelo.mangakeeper.backend.dto.manga;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.flmelo.mangakeeper.backend.entity.enuns.Idioma;
import jakarta.validation.constraints.Positive;

public record UpdateMangaDTO(
        String titulo,
        String autor,
        String editora,
        String genero,
        String sinopse,
        String issn,
        @Positive
        @JsonProperty(value = "total_volumes")
        Integer totalVolumes,
        Idioma idioma
) {
}
