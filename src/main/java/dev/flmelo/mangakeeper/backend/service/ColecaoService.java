package dev.flmelo.mangakeeper.backend.service;

import dev.flmelo.mangakeeper.backend.dto.colecao.ColecaoResponseDTO;
import dev.flmelo.mangakeeper.backend.entity.Colecao;
import dev.flmelo.mangakeeper.backend.repository.ColecaoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

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

    public ColecaoResponseDTO getById(Long id){
        Optional<Colecao> colecaoById = colecaoRepository.findById(id);
        if (colecaoById.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Coleção não encontrada.");
        }

        Colecao colecao = colecaoById.get();

        return new ColecaoResponseDTO(
                colecao.getId(),
                colecao.getNome(),
                colecao.getPublico(),
                colecao.getUsuario().getId());

    }
}
