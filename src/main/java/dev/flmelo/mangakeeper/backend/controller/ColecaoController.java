package dev.flmelo.mangakeeper.backend.controller;

import dev.flmelo.mangakeeper.backend.dto.colecao.ColecaoResponseDTO;
import dev.flmelo.mangakeeper.backend.dto.colecao.CreateColecaoDTO;
import dev.flmelo.mangakeeper.backend.dto.colecao.UpdateColecaoDTO;
import dev.flmelo.mangakeeper.backend.service.ColecaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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

    @GetMapping(value = "/{id}")
    public ResponseEntity<ColecaoResponseDTO> getById(@PathVariable Long id){
        ColecaoResponseDTO colecao = colecaoService.getById(id);
        return ResponseEntity.ok().body(colecao);
    }

    @PostMapping
    public ResponseEntity<ColecaoResponseDTO> create(@Valid @RequestBody CreateColecaoDTO request){
        ColecaoResponseDTO colecaoCriada = colecaoService.create(request);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}").buildAndExpand(colecaoCriada.id()).toUri();
        return ResponseEntity.created(uri).body(colecaoCriada);

    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<ColecaoResponseDTO> updateColecao(@Valid @PathVariable Long id,@RequestBody UpdateColecaoDTO colecaoUpdate){
        ColecaoResponseDTO colecao = colecaoService.updateColecao(id, colecaoUpdate);
        return ResponseEntity.ok().body(colecao);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        colecaoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
