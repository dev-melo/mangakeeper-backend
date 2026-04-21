package dev.flmelo.mangakeeper.backend.service;

import dev.flmelo.mangakeeper.backend.repository.MangaRepository;
import org.springframework.stereotype.Service;

@Service
public class MangaService {

    private final MangaRepository mangaRepository;

    public MangaService(MangaRepository mangaRepository) {
        this.mangaRepository = mangaRepository;
    }
}
