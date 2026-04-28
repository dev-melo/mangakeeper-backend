package dev.flmelo.mangakeeper.backend.controller;

import dev.flmelo.mangakeeper.backend.dto.manga.CreateMangaDTO;
import dev.flmelo.mangakeeper.backend.dto.manga.MangaResponseDTO;
import dev.flmelo.mangakeeper.backend.dto.manga.UpdateMangaDTO;
import dev.flmelo.mangakeeper.backend.entity.Manga;
import dev.flmelo.mangakeeper.backend.service.MangaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "mangas")
@Tag(name = "Mangas", description = "Endpoints de Mangas")
public class MangaController {

    private final MangaService mangaService;

    public MangaController(MangaService mangaService) {
        this.mangaService = mangaService;
    }

    @Operation(summary = "Listar mangás", description = "Retorna todos mangás cadastrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    @GetMapping
    public ResponseEntity<List<MangaResponseDTO>> getAll(){
        List<MangaResponseDTO> list = mangaService.getAll();
        return ResponseEntity.ok().body(list);
    }

    @Operation(summary = "Buscar mangá por ID", description = "Retorna um mangá específico. ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mangá encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Mangá não encontrado.")
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<MangaResponseDTO> getById(@PathVariable Long id){
        MangaResponseDTO manga = mangaService.getById(id);
        return ResponseEntity.ok().body(manga);
    }

    @Operation(summary = "Cria um mangá", description = "Criar um mangá no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Mangá criado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Coleção informada não foi encontrada."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
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

    @Operation(summary = "Atualiza mangá", description = "Atualiza dados de um mangá")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados atualizados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Mangá não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PatchMapping(value = "/{id}")
    public ResponseEntity<MangaResponseDTO> updateManga(@Valid @PathVariable Long id, @RequestBody UpdateMangaDTO request){
        MangaResponseDTO manga = mangaService.update(id, request);
        return ResponseEntity.ok().body(manga);
    }

    @Operation(summary = "Deleta um mangá", description = "Remove mangá do sistema por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Mangá deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Mangá não encontrado")
    })
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        mangaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
