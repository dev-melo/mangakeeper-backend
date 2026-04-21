package dev.flmelo.mangakeeper.backend.service;

import dev.flmelo.mangakeeper.backend.dto.manga.MangaResponseDTO;
import dev.flmelo.mangakeeper.backend.repository.MangaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
