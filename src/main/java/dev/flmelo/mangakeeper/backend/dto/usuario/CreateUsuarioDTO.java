package dev.flmelo.mangakeeper.backend.dto.usuario;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUsuarioDTO(
        @NotBlank String username,

        @NotBlank String email,

        @NotBlank
        @Size(min = 8, max = 20, message = "A senha precisa ter entre 8 a 20 caracteres.")
        String password,


        @JsonProperty("avatar_url")
        String avatarUrl) {
}
