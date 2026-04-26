package dev.flmelo.mangakeeper.backend.dto.volume;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Positive;

public record UpdateVolumeDTO(

        @Positive
        Integer numero,

        @JsonProperty("codigo_de_barras")
        String codigoDeBarras,

        String isbn,

        @JsonProperty("imagem_url")
        String imagemUrl

        ) {
}
