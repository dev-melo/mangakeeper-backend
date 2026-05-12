package dev.flmelo.mangakeeper.backend.service;

import dev.flmelo.mangakeeper.backend.config.security.SecurityContextService;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CurtidaService {
    private final SecurityContextService securityContextService;
    private final CurtidaRepository curtidaRepository;
    private final ColecaoRepository colecaoRepository;
    private final UsuarioRepository usuarioRepository;

    public CurtidaService(SecurityContextService securityContextService, CurtidaRepository curtidaRepository, ColecaoRepository colecaoRepository, UsuarioRepository usuarioRepository) {
        this.securityContextService = securityContextService;
        this.curtidaRepository = curtidaRepository;
        this.colecaoRepository = colecaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    //Coleções
    public void curtirColecao(Long colecaoId) {
        Usuario usuarioLogado = securityContextService.getUsuarioLogado();
        Colecao colecao = colecaoRepository.findById(colecaoId).orElseThrow(CollectionNotFoundException::new);
        if (curtidaRepository.existsByUsuarioIdAndColecaoId(usuarioLogado.getId(), colecao.getId())) {
            throw new LikeAlreadyExistsException();
        }
        Curtida curtida = new Curtida(usuarioLogado, colecao);
        curtidaRepository.save(curtida);
    }

    @Transactional
    public void removeCurtida(Long colecaoId){
        Usuario usuarioLogado = securityContextService.getUsuarioLogado();
        Colecao colecao = colecaoRepository.findById(colecaoId).orElseThrow(CollectionNotFoundException::new);
        if (curtidaRepository.existsByUsuarioIdAndColecaoId(usuarioLogado.getId(), colecao.getId())){
            curtidaRepository.deleteByUsuarioIdAndColecaoId(usuarioLogado.getId(), colecao.getId());
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
        List<Curtida> listCurtidas = curtidaRepository.findAllByUsuarioId(usuarioId);
        List<ColecaoResumeDTO> colecaoDTO = listCurtidas
                .stream()
                .map(curtida -> new ColecaoResumeDTO(
                        curtida.getColecao().getId(),
                        curtida.getColecao().getNome()
                        ))
                .toList();
        return new UsuarioCurtidaResponseDTO(
                usuario.getId(),
                qtdCurtidas,
                colecaoDTO
        );
    }
}