package dev.flmelo.mangakeeper.backend.controller;

import dev.flmelo.mangakeeper.backend.dto.manga.CreateMangaDTO;
import dev.flmelo.mangakeeper.backend.dto.manga.MangaResponseDTO;
import dev.flmelo.mangakeeper.backend.service.MangaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "mangas")
public class MangaController {

    private final MangaService mangaService;

    public MangaController(MangaService mangaService) {
        this.mangaService = mangaService;
    }

    @GetMapping
    public ResponseEntity<List<MangaResponseDTO>> getAll(){
        List<MangaResponseDTO> list = mangaService.getAll();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<MangaResponseDTO> getById(@PathVariable Long id){
        MangaResponseDTO manga = mangaService.getById(id);
        return ResponseEntity.ok().body(manga);
    }

    @PostMapping
    public ResponseEntity<MangaResponseDTO> create(@Valid @RequestBody CreateMangaDTO request){
        MangaResponseDTO mangaCriado = mangaService.create(request);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(mangaCriado.id())
                .toUri();

        return ResponseEntity.created(uri).body(mangaCriado);
    }
}
