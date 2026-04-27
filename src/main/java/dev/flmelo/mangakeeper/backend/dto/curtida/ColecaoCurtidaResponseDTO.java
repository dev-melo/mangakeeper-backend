package dev.flmelo.mangakeeper.backend.dto.curtida;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.flmelo.mangakeeper.backend.dto.usuario.UsuarioResumeDTO;

import java.util.List;

public record ColecaoCurtidaResponseDTO(
        @JsonProperty("colecao_id")
        Long colecaoId,
        @JsonProperty("total_curtidas")
        Integer totalCurtidas,
        List<UsuarioResumeDTO> usuariosQueCurtiram
) {
}
