package dev.flmelo.mangakeeper.backend.controller;

import dev.flmelo.mangakeeper.backend.dto.curtida.UsuarioCurtidaResponseDTO;
import dev.flmelo.mangakeeper.backend.service.CurtidaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("usuarios")
@Tag(name = "Usuário - Curtidas", description = "Operações relacionadas às curtidas de usuários")
public class UsuarioCurtidaController {

    private final CurtidaService curtidaService;

    public UsuarioCurtidaController(CurtidaService curtidaService) {
        this.curtidaService = curtidaService;
    }
    @Operation(summary = "Listar curtidas do usuário", description = "Retorna o total de curtidas e as coleções curtidas por um usuário específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna resumo com sucesso."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/{usuarioId}/likes")
    public ResponseEntity<UsuarioCurtidaResponseDTO> curtidasUsuario(@PathVariable Long usuarioId){
        UsuarioCurtidaResponseDTO curtidaDTO = curtidaService.totalCurtidasUsuario(usuarioId);
        return ResponseEntity.ok().body(curtidaDTO);
    }
}
