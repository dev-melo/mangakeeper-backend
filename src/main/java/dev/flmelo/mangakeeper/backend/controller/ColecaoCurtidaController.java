package dev.flmelo.mangakeeper.backend.controller;

import dev.flmelo.mangakeeper.backend.dto.curtida.ColecaoCurtidaResponseDTO;
import dev.flmelo.mangakeeper.backend.service.CurtidaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "colecoes")
public class ColecaoCurtidaController {

    private final CurtidaService curtidaService;

    public ColecaoCurtidaController(CurtidaService curtidaService) {
        this.curtidaService = curtidaService;
    }

    @PostMapping("/{colecaoId}/likes")
    public ResponseEntity<?> curtirColecao(@Valid @PathVariable Long colecaoId, @RequestParam Long usuarioId){
        curtidaService.curtirColecao(usuarioId, colecaoId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{colecaoId}/likes")
    public ResponseEntity<?> removeCurtida(@PathVariable Long colecaoId, @RequestParam Long usuarioId){
        curtidaService.removeCurtida(colecaoId, usuarioId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{colecaoId}/likes")
    public ResponseEntity<ColecaoCurtidaResponseDTO> curtidasColecao(@PathVariable Long colecaoId){
        ColecaoCurtidaResponseDTO curtidaDTO = curtidaService.totalCurtidasColecao(colecaoId);
        return ResponseEntity.ok().body(curtidaDTO);
    }

}
