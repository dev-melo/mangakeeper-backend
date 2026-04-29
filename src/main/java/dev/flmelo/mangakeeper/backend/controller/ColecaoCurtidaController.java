package dev.flmelo.mangakeeper.backend.controller;

import dev.flmelo.mangakeeper.backend.dto.curtida.ColecaoCurtidaResponseDTO;
import dev.flmelo.mangakeeper.backend.service.CurtidaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "colecoes")
@Tag(name = "Curtidas", description = "Endpoint para gerenciar e listar curtidas em coleções")
public class ColecaoCurtidaController {

    private final CurtidaService curtidaService;

    public ColecaoCurtidaController(CurtidaService curtidaService) {
        this.curtidaService = curtidaService;
    }

    @Operation(summary = "Curtir coleção", description = "Registra a curtida de um usuário em uma coleção específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Curtida registrada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Coleção ou usuário não encontrado."),
            @ApiResponse(responseCode = "409", description = "Usuário já curtiu essa coleção")
    })
    @PostMapping("/{colecaoId}/likes")
    public ResponseEntity<?> curtirColecao(@Valid @PathVariable Long colecaoId, @RequestParam Long usuarioId){
        curtidaService.curtirColecao(usuarioId, colecaoId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Remover curtida", description = "Remove a curtida de uma coleção")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Curtida removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Coleção ou usuário não encontrado.")
    })
    @DeleteMapping("/{colecaoId}/likes")
    public ResponseEntity<?> removeCurtida(@PathVariable Long colecaoId, @RequestParam Long usuarioId){
        curtidaService.removeCurtida(colecaoId, usuarioId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Resumo de curtidas de uma coleção", description = "Retorna a quantidade total de curtidas de uma coleção e os usuários que curtiram")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resumo retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Coleção não encontrada")
    })
    @GetMapping("/{colecaoId}/likes")
    public ResponseEntity<ColecaoCurtidaResponseDTO> curtidasColecao(@PathVariable Long colecaoId){
        ColecaoCurtidaResponseDTO curtidaDTO = curtidaService.totalCurtidasColecao(colecaoId);
        return ResponseEntity.ok().body(curtidaDTO);
    }

}
