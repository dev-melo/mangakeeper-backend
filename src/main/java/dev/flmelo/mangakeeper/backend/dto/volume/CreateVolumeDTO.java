package dev.flmelo.mangakeeper.backend.dto.volume;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateVolumeDTO(

        @Positive @NotNull Integer numero,

        @JsonProperty("codigo_de_barras")
        @NotBlank String codigoDeBarras,

        String isbn,

        @NotNull @JsonProperty("manga_id") Long mangaId,

        @NotNull @JsonProperty("imagem_url") String imagemUrl

) {
}
