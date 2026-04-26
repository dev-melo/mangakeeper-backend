package dev.flmelo.mangakeeper.backend.service;

import dev.flmelo.mangakeeper.backend.entity.Colecao;
import dev.flmelo.mangakeeper.backend.entity.Curtida;
import dev.flmelo.mangakeeper.backend.entity.Usuario;
import dev.flmelo.mangakeeper.backend.exceptions.CollectionNotFoundException;
import dev.flmelo.mangakeeper.backend.exceptions.UserNotFoundException;
import dev.flmelo.mangakeeper.backend.repository.ColecaoRepository;
import dev.flmelo.mangakeeper.backend.repository.CurtidaRepository;
import dev.flmelo.mangakeeper.backend.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class CurtidaService {
    private final CurtidaRepository curtidaRepository;
    private final ColecaoRepository colecaoRepository;
    private final UsuarioRepository usuarioRepository;

    public CurtidaService(CurtidaRepository curtidaRepository, ColecaoRepository colecaoRepository, UsuarioRepository usuarioRepository) {
        this.curtidaRepository = curtidaRepository;
        this.colecaoRepository = colecaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public void curtirColecao(Long usuarioId, Long colecaoId){
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(
                UserNotFoundException::new
        );

        Colecao colecao = colecaoRepository.findById(colecaoId).orElseThrow(
                CollectionNotFoundException::new
        );

        Boolean curtidaExiste = curtidaRepository.existsByUsuarioIdAndColecaoId(usuarioId, colecaoId);

        if (curtidaExiste){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este usuario já curtiu essa coleção.");
        } else {
            Curtida curtida = new Curtida(usuario, colecao);
            curtidaRepository.save(curtida);
        }

    }
}
