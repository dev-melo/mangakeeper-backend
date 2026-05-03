package dev.flmelo.mangakeeper.backend.service;

import dev.flmelo.mangakeeper.backend.config.security.AuthService;
import dev.flmelo.mangakeeper.backend.dto.usuario.CreateUsuarioDTO;
import dev.flmelo.mangakeeper.backend.dto.usuario.UpdateUsuarioDTO;
import dev.flmelo.mangakeeper.backend.dto.usuario.UsuarioResponseDTO;
import dev.flmelo.mangakeeper.backend.entity.Usuario;
import dev.flmelo.mangakeeper.backend.exceptions.UserAlreadyExistsException;
import dev.flmelo.mangakeeper.backend.exceptions.UserNotFoundException;
import dev.flmelo.mangakeeper.backend.repository.CurtidaRepository;
import dev.flmelo.mangakeeper.backend.repository.UsuarioRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {


    private final AuthService authService;

    private final UsuarioRepository usuarioRepository;
    private final CurtidaRepository curtidaRepository;

    public final PasswordEncoder passwordEncoder;

    public UsuarioService(AuthService authService, UsuarioRepository usuarioRepository, CurtidaRepository curtidaRepository, PasswordEncoder passwordEncoder) {
        this.authService = authService;
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

    public UsuarioResponseDTO create(CreateUsuarioDTO request) {
        String senhaCriptografada = passwordEncoder.encode(request.password());
        Usuario u = new Usuario(request.username(), request.email(), senhaCriptografada, request.avatarUrl());
        try {
            Usuario usuarioSalvo = usuarioRepository.save(u);
            return new UsuarioResponseDTO(usuarioSalvo.getId(), usuarioSalvo.getUsername(), usuarioSalvo.getAvatarUrl());

        } catch (DataIntegrityViolationException e){
            throw new UserAlreadyExistsException();
        }

    }

    public UsuarioResponseDTO updateUsuario(Long id, UpdateUsuarioDTO usuarioUpdate) {
        Optional<Usuario> byId = usuarioRepository.findById(id);
        if (byId.isEmpty()){
            throw new UserNotFoundException();
        }
        Usuario u = byId.get();

        authService.validaDonoOuAdmin(u);

        if (usuarioUpdate.email() != null){
            u.setEmail(usuarioUpdate.email());
        }

        if (usuarioUpdate.avatarUrl() != null){
            u.setAvatarUrl(usuarioUpdate.avatarUrl());
        }

        if (usuarioUpdate.password() != null){
            u.setPassword(passwordEncoder.encode(usuarioUpdate.password()));
        }

        usuarioRepository.save(u);
        return new UsuarioResponseDTO(u.getId(), u.getUsername(), u.getAvatarUrl());
    }

    @Transactional
    public void delete(Long id){
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isEmpty()){
            throw new UserNotFoundException();
        }

        authService.validaDonoOuAdmin(usuario.get());
        curtidaRepository.deleteByUsuarioId(id);
        usuarioRepository.deleteById(id);
    }
}
