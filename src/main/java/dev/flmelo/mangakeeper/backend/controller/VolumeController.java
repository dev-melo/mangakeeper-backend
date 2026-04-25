package dev.flmelo.mangakeeper.backend.controller;

import dev.flmelo.mangakeeper.backend.dto.volume.VolumeResponseDTO;
import dev.flmelo.mangakeeper.backend.service.VolumeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
