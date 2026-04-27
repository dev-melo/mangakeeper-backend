package dev.flmelo.mangakeeper.backend.controller;

import dev.flmelo.mangakeeper.backend.dto.curtida.UsuarioCurtidaResponseDTO;
import dev.flmelo.mangakeeper.backend.service.CurtidaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("usuarios")
public class UsuarioCurtidaController {

    private final CurtidaService curtidaService;

    public UsuarioCurtidaController(CurtidaService curtidaService) {
        this.curtidaService = curtidaService;
    }

    @GetMapping("/{usuarioId}/likes")
    public ResponseEntity<UsuarioCurtidaResponseDTO> curtidasUsuario(@PathVariable Long usuarioId){
        UsuarioCurtidaResponseDTO curtidaDTO = curtidaService.totalCurtidasUsuario(usuarioId);
        return ResponseEntity.ok().body(curtidaDTO);
    }
}
