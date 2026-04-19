package dev.flmelo.mangakeeper.backend.controller;

import dev.flmelo.mangakeeper.backend.dto.colecao.ColecaoResponseDTO;
import dev.flmelo.mangakeeper.backend.service.ColecaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "colecoes")
public class ColecaoController {
    private final ColecaoService colecaoService;

    public ColecaoController(ColecaoService colecaoService) {
        this.colecaoService = colecaoService;
    }

    @GetMapping
    public ResponseEntity<List<ColecaoResponseDTO>> getAll(){
        List<ColecaoResponseDTO> list = colecaoService.getAll();
        return ResponseEntity.ok().body(list);
    }
}
