package dev.flmelo.mangakeeper.backend.service;

import dev.flmelo.mangakeeper.backend.dto.colecao.ColecaoResponseDTO;
import dev.flmelo.mangakeeper.backend.repository.ColecaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColecaoService {
    private final ColecaoRepository colecaoRepository;

    public ColecaoService(ColecaoRepository colecaoRepository) {
        this.colecaoRepository = colecaoRepository;
    }


    public List<ColecaoResponseDTO> getAll() {
        return colecaoRepository.findAll()
                .stream()
                .map(colecao -> new ColecaoResponseDTO(
                        colecao.getId(),
                        colecao.getNome(),
                        colecao.getPublico(),
                        colecao.getUsuario().getId()
                )).toList();
    }
}
