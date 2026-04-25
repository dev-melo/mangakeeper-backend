package dev.flmelo.mangakeeper.backend.service;

import dev.flmelo.mangakeeper.backend.dto.volume.VolumeResponseDTO;
import dev.flmelo.mangakeeper.backend.entity.Volume;
import dev.flmelo.mangakeeper.backend.repository.MangaRepository;
import dev.flmelo.mangakeeper.backend.repository.VolumeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

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

    public VolumeResponseDTO getById(Long id){
        Optional<Volume> volumeById = volumeRepository.findById(id);
        if (volumeById.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Volume não encontrado.");
        }
        Volume v = volumeById.get();
        return new VolumeResponseDTO(
                v.getId(),
                v.getNumero(),
                v.getCodigoDeBarras(),
                v.getIsbn(),
                v.getManga().getId(),
                v.getImagemUrl()
        );
    }

    public void delete(Long id){
        Optional<Volume> volumeById = volumeRepository.findById(id);
        if (volumeById.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Volume não encontrado.");
        }
        volumeRepository.deleteById(id);
    }
}
