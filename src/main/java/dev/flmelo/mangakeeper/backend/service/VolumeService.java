package dev.flmelo.mangakeeper.backend.service;

import dev.flmelo.mangakeeper.backend.config.security.AuthService;
import dev.flmelo.mangakeeper.backend.dto.volume.CreateVolumeDTO;
import dev.flmelo.mangakeeper.backend.dto.volume.UpdateVolumeDTO;
import dev.flmelo.mangakeeper.backend.dto.volume.VolumeResponseDTO;
import dev.flmelo.mangakeeper.backend.entity.Manga;
import dev.flmelo.mangakeeper.backend.entity.Volume;
import dev.flmelo.mangakeeper.backend.exceptions.MangaNotFoundException;
import dev.flmelo.mangakeeper.backend.exceptions.VolumeNotFoundException;
import dev.flmelo.mangakeeper.backend.repository.MangaRepository;
import dev.flmelo.mangakeeper.backend.repository.VolumeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class VolumeService {
    private final AuthService authService;
    private final VolumeRepository volumeRepository;
    private final MangaRepository mangaRepository;

    public VolumeService(AuthService authService, VolumeRepository volumeRepository, MangaRepository mangaRepository) {
        this.authService = authService;
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
            throw new VolumeNotFoundException();
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

    public VolumeResponseDTO create(CreateVolumeDTO request){
        Optional<Manga> manga = mangaRepository.findById(request.mangaId());
        if (manga.isEmpty()){
            throw new MangaNotFoundException();
        }
        Manga m = manga.get();

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
                volume.getNumero(),
                volumeSalvo.getCodigoDeBarras(),
                volumeSalvo.getIsbn(),
                volumeSalvo.getManga().getId(),
                volumeSalvo.getImagemUrl());
    }

    public VolumeResponseDTO update(Long id, UpdateVolumeDTO request){
        Optional<Volume> volumeById = volumeRepository.findById(id);
        if (volumeById.isEmpty()){
            throw new VolumeNotFoundException();
        }
        Volume volumeAtualizado = volumeById.get();
        authService.validaDonoOuAdmin(volumeAtualizado.getManga().getColecao().getUsuario());
        if (request.numero() != null){
            volumeAtualizado.setNumero(request.numero());
        }
        if (request.codigoDeBarras() != null){
            String cdb = request.codigoDeBarras().trim();
            volumeAtualizado.setCodigoDeBarras(cdb);
        }
        if (request.isbn() != null){
            String isbn = request.isbn().trim();
            volumeAtualizado.setIsbn(isbn);
        }
        if (request.imagemUrl() != null) {
            String imgUrl = request.imagemUrl().trim();
            volumeAtualizado.setImagemUrl(imgUrl);
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
        Optional<Volume> volumeById = volumeRepository.findById(id);
        if (volumeById.isEmpty()){
            throw new VolumeNotFoundException();
        }
        authService.validaDonoOuAdmin(
                volumeById.get().getManga().getColecao().getUsuario()
        );
        volumeRepository.deleteById(id);
    }

    private String clean(String s) {
        return s == null ? null :  s.trim();

    }
}
