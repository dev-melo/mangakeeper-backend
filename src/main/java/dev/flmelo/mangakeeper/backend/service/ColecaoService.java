package dev.flmelo.mangakeeper.backend.service;

import dev.flmelo.mangakeeper.backend.repository.ColecaoRepository;
import org.springframework.stereotype.Service;

@Service
public class ColecaoService {
    private final ColecaoRepository colecaoRepository;

    public ColecaoService(ColecaoRepository colecaoRepository) {
        this.colecaoRepository = colecaoRepository;
    }


}
