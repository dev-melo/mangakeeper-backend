package dev.flmelo.mangakeeper.backend.service;

import dev.flmelo.mangakeeper.backend.dto.security.RegisterDTO;
import dev.flmelo.mangakeeper.backend.dto.usuario.UsuarioResponseDTO;
import dev.flmelo.mangakeeper.backend.entity.RoleModel;
import dev.flmelo.mangakeeper.backend.entity.Usuario;
import dev.flmelo.mangakeeper.backend.entity.enuns.RoleName;
import dev.flmelo.mangakeeper.backend.exceptions.UserAlreadyExistsException;
import dev.flmelo.mangakeeper.backend.repository.RoleRepository;
import dev.flmelo.mangakeeper.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class AuthenticationService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RoleRepository roleRepository;



    public UsuarioResponseDTO register(RegisterDTO data){
        if (usuarioRepository.findByUsername(data.login()).isPresent()){
            throw new UserAlreadyExistsException();
        }
        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        Usuario newUser = new Usuario(data.login().toLowerCase(Locale.ROOT), data.email(), encryptedPassword,  data.avatarUrl());
        RoleModel role = roleRepository.findByRoleName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Role não Encontrada"));
        newUser.setRoles(List.of(role));
        try{
            usuarioRepository.save(newUser);
        } catch (DataIntegrityViolationException e){
            throw new DataIntegrityViolationException("Usuario já existe");
        }
        return new UsuarioResponseDTO(newUser.getId(), newUser.getUsername(), newUser.getAvatarUrl());
    }
}
