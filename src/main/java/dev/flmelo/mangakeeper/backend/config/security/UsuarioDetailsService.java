package dev.flmelo.mangakeeper.backend.config.security;

import dev.flmelo.mangakeeper.backend.entity.Usuario;
import dev.flmelo.mangakeeper.backend.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Usuário não encontrato")
        );
        return new User(usuario.getUsername(), usuario.getPassword(), true, true, true, true,
                usuario.getAuthorities());
    }
}
