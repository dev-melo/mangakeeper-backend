package dev.flmelo.mangakeeper.backend.controller;

import dev.flmelo.mangakeeper.backend.dto.volume.CreateVolumeDTO;
import dev.flmelo.mangakeeper.backend.dto.volume.VolumeResponseDTO;
import dev.flmelo.mangakeeper.backend.service.VolumeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "volumes")
public class VolumeController {

    public VolumeController(VolumeService volumeService) {
        this.volumeService = volumeService;
    }

    private final VolumeService volumeService;

    @GetMapping
    public ResponseEntity<List<VolumeResponseDTO>> getAll(){
        List<VolumeResponseDTO> list = volumeService.getAll();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<VolumeResponseDTO> getById(@PathVariable Long id){
        VolumeResponseDTO volumeById = volumeService.getById(id);
        return ResponseEntity.ok().body(volumeById);
    }

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


    @DeleteMapping (value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        volumeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
