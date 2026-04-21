package dev.flmelo.mangakeeper.backend.dto.colecao;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.flmelo.mangakeeper.backend.entity.Usuario;

public record ColecaoResponseDTO(
        Long id,
        String nome,
        Boolean publico,
        Long usuarioId
) {
}
