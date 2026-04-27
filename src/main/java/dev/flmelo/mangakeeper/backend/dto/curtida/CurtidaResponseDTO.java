package dev.flmelo.mangakeeper.backend.dto.curtida;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CurtidaResponseDTO(
        @JsonProperty("colecao_id")
        Long colecaoId,
        @JsonProperty("total_curtidas")
        Integer totalCurtidas
) {
}
