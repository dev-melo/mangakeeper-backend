package dev.flmelo.mangakeeper.backend.dto.curtida;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.flmelo.mangakeeper.backend.dto.colecao.ColecaoResumeDTO;

import java.util.List;

public record UsuarioCurtidaResponseDTO(
        @JsonProperty("usuario_id")
        Long usuarioId,
        @JsonProperty("total_curtidas")
        Integer totalCurtidas,
        @JsonProperty("colecoes_curtidas")
        List<ColecaoResumeDTO> colecoesCurtidas
) {
}
