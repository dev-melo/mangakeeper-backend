package dev.flmelo.mangakeeper.backend.dto.volume;

import com.fasterxml.jackson.annotation.JsonProperty;

public record VolumeResponseDTO(
        Long id,
        Integer numero,
        String codigoDeBarras,
        String isbn,
        @JsonProperty("manga_id")
        Long mangaId,
        @JsonProperty("imagem_url")
        String imagemUrl
) {
}
