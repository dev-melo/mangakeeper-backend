package dev.flmelo.mangakeeper.backend.service;

import dev.flmelo.mangakeeper.backend.config.security.SecurityContextService;
import dev.flmelo.mangakeeper.backend.dto.volume.CreateVolumeDTO;
import dev.flmelo.mangakeeper.backend.dto.volume.UpdateVolumeDTO;
import dev.flmelo.mangakeeper.backend.dto.volume.VolumeResponseDTO;
import dev.flmelo.mangakeeper.backend.entity.Manga;
import dev.flmelo.mangakeeper.backend.entity.Volume;
import dev.flmelo.mangakeeper.backend.exceptions.MangaNotFoundException;
import dev.flmelo.mangakeeper.backend.exceptions.VolumeNotFoundException;
import dev.flmelo.mangakeeper.backend.repository.MangaRepository;
import dev.flmelo.mangakeeper.backend.repository.VolumeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VolumeService {
    private final SecurityContextService securityContextService;
    private final VolumeRepository volumeRepository;
    private final MangaRepository mangaRepository;

    public VolumeService(SecurityContextService securityContextService, VolumeRepository volumeRepository, MangaRepository mangaRepository) {
        this.securityContextService = securityContextService;
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
        Volume v = volumeRepository.findById(id).orElseThrow(VolumeNotFoundException::new);
        return new VolumeResponseDTO(
                v.getId(),
                v.getNumero(),
                v.getCodigoDeBarras(),
                v.getIsbn(),
                v.getManga().getId(),
                v.getImagemUrl()
        );
    }

    public VolumeResponseDTO create(CreateVolumeDTO request){
        Manga m = mangaRepository.findById(request.mangaId()).orElseThrow(MangaNotFoundException::new);
        securityContextService.validaDonoOuAdmin(m.getColecao().getUsuario());
        Volume volume = new Volume(
                request.numero(),
                clean(request.codigoDeBarras()),
                clean(request.isbn()),
                m,
                clean(request.imagemUrl())
        );

        Volume volumeSalvo = volumeRepository.save(volume);

        return new VolumeResponseDTO(
                volumeSalvo.getId(),
                volumeSalvo.getNumero(),
                volumeSalvo.getCodigoDeBarras(),
                volumeSalvo.getIsbn(),
                volumeSalvo.getManga().getId(),
                volumeSalvo.getImagemUrl());
    }

    public VolumeResponseDTO update(Long id, UpdateVolumeDTO request){
        Volume volumeAtualizado = volumeRepository.findById(id).orElseThrow(VolumeNotFoundException::new);
        securityContextService.validaDonoOuAdmin(volumeAtualizado.getManga().getColecao().getUsuario());
        if (request.numero() != null){
            volumeAtualizado.setNumero(request.numero());
        }
        if (request.codigoDeBarras() != null){
            volumeAtualizado.setCodigoDeBarras(clean(request.codigoDeBarras()));
        }
        if (request.isbn() != null){
            volumeAtualizado.setIsbn(clean(request.isbn()));
        }
        if (request.imagemUrl() != null) {
            volumeAtualizado.setImagemUrl(clean(request.imagemUrl()));
        }
        Volume v = volumeRepository.save(volumeAtualizado);

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
        Volume volume = volumeRepository.findById(id).orElseThrow(VolumeNotFoundException::new);
        securityContextService.validaDonoOuAdmin(volume.getManga().getColecao().getUsuario());
        volumeRepository.deleteById(id);
    }

    private String clean(String s) {
        return s == null ? null :  s.trim();

    }
}
