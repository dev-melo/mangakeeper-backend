package dev.flmelo.mangakeeper.backend.service;

import dev.flmelo.mangakeeper.backend.dto.colecao.ColecaoResumeDTO;
import dev.flmelo.mangakeeper.backend.dto.curtida.ColecaoCurtidaResponseDTO;
import dev.flmelo.mangakeeper.backend.dto.curtida.UsuarioCurtidaResponseDTO;
import dev.flmelo.mangakeeper.backend.dto.usuario.UsuarioResumeDTO;
import dev.flmelo.mangakeeper.backend.entity.Colecao;
import dev.flmelo.mangakeeper.backend.entity.Curtida;
import dev.flmelo.mangakeeper.backend.entity.Usuario;
import dev.flmelo.mangakeeper.backend.exceptions.CollectionNotFoundException;
import dev.flmelo.mangakeeper.backend.exceptions.LikeAlreadyExistsException;
import dev.flmelo.mangakeeper.backend.exceptions.UserNotFoundException;
import dev.flmelo.mangakeeper.backend.repository.ColecaoRepository;
import dev.flmelo.mangakeeper.backend.repository.CurtidaRepository;
import dev.flmelo.mangakeeper.backend.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;

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

    //Coleções
    public void curtirColecao(Long usuarioId, Long colecaoId) {
        SimpleEntry<Usuario, Colecao> res = validarUsuarioEColecaoExistentes(usuarioId, colecaoId);
        if (curtidaRepository.existsByUsuarioIdAndColecaoId(usuarioId, colecaoId)) {
            throw new LikeAlreadyExistsException();
        }
        Usuario usuario = res.getKey();
        Colecao colecao = res.getValue();
        Curtida curtida = new Curtida(usuario, colecao);
        curtidaRepository.save(curtida);
    }

    @Transactional
    public void removeCurtida(Long colecaoId, Long usuarioId){
        SimpleEntry<Usuario, Colecao> res = validarUsuarioEColecaoExistentes(usuarioId, colecaoId);
        if (curtidaRepository.existsByUsuarioIdAndColecaoId(usuarioId, colecaoId)){
            curtidaRepository.deleteByUsuarioIdAndColecaoId(usuarioId, colecaoId);
        }
    }

    public ColecaoCurtidaResponseDTO totalCurtidasColecao(Long colecaoId){
        Colecao colecao = colecaoRepository.findById(colecaoId).orElseThrow(CollectionNotFoundException::new);
        Integer qtdCurtidas = curtidaRepository.countByColecaoId(colecaoId);
        List<UsuarioResumeDTO> usuarioDTO = curtidaRepository
                .findAllByColecaoId(colecaoId)
                .stream()
                .map(curtida -> {Usuario u = curtida.getUsuario();
                return new UsuarioResumeDTO(u.getId(), u.getUsername());})
                .toList();
        return new ColecaoCurtidaResponseDTO(
                colecao.getId(),
                qtdCurtidas,
                usuarioDTO
        );

    }

    //Usuarios
    public UsuarioCurtidaResponseDTO totalCurtidasUsuario(Long usuarioId){
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(UserNotFoundException::new);
        Integer qtdCurtidas = curtidaRepository.countByUsuarioId(usuarioId);
        List<Colecao> listColecoes = colecaoRepository.findAllByUsuarioId(usuarioId);
        List<ColecaoResumeDTO> colecaoDTO = listColecoes
                .stream()
                .map(colecao -> new ColecaoResumeDTO(
                        colecao.getId(),
                        colecao.getNome()))
                .toList();
        return new UsuarioCurtidaResponseDTO(
                usuario.getId(),
                qtdCurtidas,
                colecaoDTO
        );
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