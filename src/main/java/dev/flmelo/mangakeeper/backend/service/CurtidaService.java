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
import java.util.AbstractMap.SimpleEntry;

import java.util.AbstractMap;
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

    public void curtirColecao(Long usuarioId, Long colecaoId) {
        SimpleEntry<Usuario, Colecao> res = validarUsuarioEColecaoExistentes(usuarioId, colecaoId);
        if (curtidaRepository.existsByUsuarioIdAndColecaoId(usuarioId, colecaoId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este usuario já curtiu essa coleção.");
        }
        Usuario usuario = res.getKey();
        Colecao colecao = res.getValue();
        Curtida curtida = new Curtida(usuario, colecao);
        curtidaRepository.save(curtida);
    }

    public void removeCurtida(Long usuarioId, Long colecaoId){
        SimpleEntry<Usuario, Colecao> res = validarUsuarioEColecaoExistentes(usuarioId, colecaoId);
        if (curtidaRepository.existsByUsuarioIdAndColecaoId(usuarioId, colecaoId)){
            curtidaRepository.deleteByUsuarioIdAndColecaoId(usuarioId,usuarioId);
        }
    }

    private SimpleEntry<Usuario, Colecao> validarUsuarioEColecaoExistentes(Long usuarioId, Long colecaoId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(
                UserNotFoundException::new
        );

        Colecao colecao = colecaoRepository.findById(colecaoId).orElseThrow(
                CollectionNotFoundException::new
        );
        return new SimpleEntry<>(usuario, colecao);
    }
}