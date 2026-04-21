package dev.flmelo.mangakeeper.backend.dto.colecao;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.flmelo.mangakeeper.backend.entity.Usuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateColecaoDTO(
        @NotBlank
        String nome,
        Boolean publico,

        @NotNull
        @JsonProperty(value = "usuario_id")
        Long usuarioId

) {
}
