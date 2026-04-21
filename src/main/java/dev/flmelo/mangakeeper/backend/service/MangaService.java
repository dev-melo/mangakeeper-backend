package dev.flmelo.mangakeeper.backend.service;

import dev.flmelo.mangakeeper.backend.dto.manga.MangaResponseDTO;
import dev.flmelo.mangakeeper.backend.entity.Manga;
import dev.flmelo.mangakeeper.backend.repository.MangaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class MangaService {

    private final MangaRepository mangaRepository;

    public MangaService(MangaRepository mangaRepository) {
        this.mangaRepository = mangaRepository;
    }

    public List<MangaResponseDTO> getAll(){
        return mangaRepository.findAll()
                .stream()
                .map(manga ->  new MangaResponseDTO(
                        manga.getId(),
                        manga.getTitulo(),
                        manga.getAutor(),
                        manga.getEditora(),
                        manga.getGenero(),
                        manga.getSinopse(),
                        manga.getIssn(),
                        manga.getTotalVolumes(),
                        manga.getIdioma(),
                        manga.getColecao().getId()
                )).toList();
    }

    public MangaResponseDTO getById(Long id){
        Optional<Manga> mangaById = mangaRepository.findById(id);
        if (mangaById.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Mangá não encontrado.");
        }
        Manga manga = mangaById.get();
        return new MangaResponseDTO(
                manga.getId(),
                manga.getTitulo(),
                manga.getAutor(),
                manga.getEditora(),
                manga.getGenero(),
                manga.getSinopse(),
                manga.getIssn(),
                manga.getTotalVolumes(),
                manga.getIdioma(),
                manga.getColecao().getId()
        );
    }
}
