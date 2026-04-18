package dev.flmelo.mangakeeper.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUsuarioDTO(
        @Email(message = "E-mail inválido.")
        String email,

        @JsonProperty(value = "avatar_url")
        String avatarUrl,

        @Size(min = 8, max = 20, message = "A senha precisa ter entre 8 a 20 caracteres.")
        String password
) {
}
