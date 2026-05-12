package dev.flmelo.mangakeeper.backend.service;

import dev.flmelo.mangakeeper.backend.config.security.SecurityContextService;
import dev.flmelo.mangakeeper.backend.dto.usuario.UpdateUsuarioDTO;
import dev.flmelo.mangakeeper.backend.dto.usuario.UsuarioResponseDTO;
import dev.flmelo.mangakeeper.backend.entity.Usuario;
import dev.flmelo.mangakeeper.backend.exceptions.UserNotFoundException;
import dev.flmelo.mangakeeper.backend.repository.CurtidaRepository;
import dev.flmelo.mangakeeper.backend.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {


    private final SecurityContextService securityContextService;

    private final UsuarioRepository usuarioRepository;
    private final CurtidaRepository curtidaRepository;

    public final PasswordEncoder passwordEncoder;

    public UsuarioService(SecurityContextService securityContextService, UsuarioRepository usuarioRepository, CurtidaRepository curtidaRepository, PasswordEncoder passwordEncoder) {
        this.securityContextService = securityContextService;
        this.usuarioRepository = usuarioRepository;
        this.curtidaRepository = curtidaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UsuarioResponseDTO> getAll() {
        return usuarioRepository.findAll()
                .stream()
                .map(usuario -> new UsuarioResponseDTO(
                        usuario.getId(),
                        usuario.getUsername(),
                        usuario.getAvatarUrl()
                )).toList();

    }

    public UsuarioResponseDTO getById(Long id) {
        Optional<Usuario> byId = usuarioRepository.findById(id);
        if (byId.isEmpty())
        {
            throw new UserNotFoundException();
        }

        Usuario u = byId.get();
        return new UsuarioResponseDTO(u.getId(), u.getUsername(), u.getAvatarUrl());

    }


    public UsuarioResponseDTO updateUsuario(Long id, UpdateUsuarioDTO usuarioUpdate) {
        Usuario alvo = usuarioRepository.findById(id).orElseThrow(UserNotFoundException::new);
        

        securityContextService.validaDonoOuAdmin(alvo);

        if (usuarioUpdate.email() != null){
            alvo.setEmail(usuarioUpdate.email());
        }

        if (usuarioUpdate.avatarUrl() != null){
            alvo.setAvatarUrl(usuarioUpdate.avatarUrl());
        }

        if (usuarioUpdate.password() != null){
            alvo.setPassword(passwordEncoder.encode(usuarioUpdate.password()));
        }

        usuarioRepository.save(alvo);
        return new UsuarioResponseDTO(alvo.getId(), alvo.getUsername(), alvo.getAvatarUrl());
    }

    @Transactional
    public void delete(Long id){
        Usuario alvo = usuarioRepository.findById(id).orElseThrow(UserNotFoundException::new);

        securityContextService.validaDonoOuAdmin(alvo);
        curtidaRepository.deleteByUsuarioId(id);
        usuarioRepository.deleteById(id);
    }
}
