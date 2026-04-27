package dev.flmelo.mangakeeper.backend.dto.curtida;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UsuarioCurtidaResponseDTO(
        @JsonProperty("usuario_id")
        Long usuarioId,
        @JsonProperty("total_curtidas")
        Integer totalCurtidas
) {
}
