package dev.flmelo.mangakeeper.backend.service;

import dev.flmelo.mangakeeper.backend.dto.volume.VolumeResponseDTO;
import dev.flmelo.mangakeeper.backend.repository.MangaRepository;
import dev.flmelo.mangakeeper.backend.repository.VolumeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VolumeService {
    private final VolumeRepository volumeRepository;
    private final MangaRepository mangaRepository;

    public VolumeService(VolumeRepository volumeRepository, MangaRepository mangaRepository) {
        this.volumeRepository = volumeRepository;
        this.mangaRepository = mangaRepository;
    }

    public List<VolumeResponseDTO> getAll(){
        return volumeRepository.findAll()
                .stream()
                .map(volume -> new VolumeResponseDTO(
                        volume.getId(),
                        volume.getNumero(),
                        volume.getCodigoDeBarras(),
                        volume.getIsbn(),
                        volume.getManga().getId(),
                        volume.getImagemUrl()
                )).toList();
    }

}
