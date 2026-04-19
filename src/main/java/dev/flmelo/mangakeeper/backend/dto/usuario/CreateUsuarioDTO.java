package dev.flmelo.mangakeeper.backend.dto.usuario;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record CreateUsuarioDTO(@NotBlank String username,
                               @NotBlank String email,
                               @NotBlank String password,

                               @JsonProperty("avatar_url")
                               String avatarUrl) {
}
