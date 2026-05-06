package dev.flmelo.mangakeeper.backend.config.security;

import dev.flmelo.mangakeeper.backend.entity.Usuario;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextService {
    public Usuario getUsuarioLogado(){
        return (Usuario) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    public boolean isAdmin(Usuario usuario){
        return usuario
                .getAuthorities()
                .stream()
                .anyMatch(auth -> auth
                        .getAuthority().equals("ROLE_ADMIN"));
    }

    public void validaDonoOuAdmin(Usuario dono) {
        Usuario logado = getUsuarioLogado();
        if (isAdmin(logado)) return;
        if (!dono.getId().equals(logado.getId())){
            throw new AccessDeniedException("Sem permissão");
        }
    }
}
