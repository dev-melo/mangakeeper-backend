package dev.flmelo.mangakeeper.backend.controller;

import dev.flmelo.mangakeeper.backend.dto.volume.CreateVolumeDTO;
import dev.flmelo.mangakeeper.backend.dto.volume.UpdateVolumeDTO;
import dev.flmelo.mangakeeper.backend.dto.volume.VolumeResponseDTO;
import dev.flmelo.mangakeeper.backend.service.VolumeService;
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
@RequestMapping(value = "volumes")
@Tag(name = "Volumes", description = "Endpoints de Volumes")
public class VolumeController {

    public VolumeController(VolumeService volumeService) {
        this.volumeService = volumeService;
    }

    private final VolumeService volumeService;

    @Operation(summary = "Listar volumes", description = "Retorna todos volumes cadastrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    @GetMapping
    public ResponseEntity<List<VolumeResponseDTO>> getAll(){
        List<VolumeResponseDTO> list = volumeService.getAll();
        return ResponseEntity.ok().body(list);
    }

    @Operation(summary = "Buscar volume por ID", description = "Retorna um volume específico ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Volume encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Volume não encontrado")
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<VolumeResponseDTO> getById(@PathVariable Long id){
        VolumeResponseDTO volumeById = volumeService.getById(id);
        return ResponseEntity.ok().body(volumeById);
    }

    @Operation(summary = "Criar volume", description = "Criar um volume no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Volume criado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Manga informado não foi encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<VolumeResponseDTO> create(@Valid @RequestBody CreateVolumeDTO request){
        VolumeResponseDTO volumeCriado = volumeService.create(request);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(volumeCriado.id())
                .toUri();
        return ResponseEntity.created(uri).body(volumeCriado);
    }

    @Operation(summary = "Atualizar volume", description = "Atualiza dados do volume")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nome do volume atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Volume não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PatchMapping(value = "/{id}")
    public ResponseEntity<VolumeResponseDTO> update(@Valid @PathVariable Long id, @RequestBody UpdateVolumeDTO request){
        VolumeResponseDTO volume = volumeService.update(id, request);
        return ResponseEntity.ok().body(volume);
    }

    @Operation(summary = "Deletar volume", description = "Remove volume do sistema por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Volume deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Volume não encontrado")
    })
    @DeleteMapping (value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        volumeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
