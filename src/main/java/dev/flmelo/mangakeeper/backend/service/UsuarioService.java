package dev.flmelo.mangakeeper.backend.service;

import dev.flmelo.mangakeeper.backend.dto.UsuarioResponseDTO;
import dev.flmelo.mangakeeper.backend.entity.Usuario;
import dev.flmelo.mangakeeper.backend.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
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
            throw new EntityNotFoundException("Usuario não encontrado: ID " + id);
        }

        Usuario u = byId.get();
        return new UsuarioResponseDTO(u.getId(), u.getUsername(), u.getAvatarUrl());

    }
}
