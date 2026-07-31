package dev.flmelo.mangakeeper.backend.service;

import dev.flmelo.mangakeeper.backend.dto.security.AuthenticationDTO;
import dev.flmelo.mangakeeper.backend.dto.security.LoginResponseDTO;
import dev.flmelo.mangakeeper.backend.dto.security.RegisterDTO;
import dev.flmelo.mangakeeper.backend.dto.usuario.UsuarioResponseDTO;
import dev.flmelo.mangakeeper.backend.entity.RoleModel;
import dev.flmelo.mangakeeper.backend.entity.Usuario;
import dev.flmelo.mangakeeper.backend.entity.enuns.RoleName;
import dev.flmelo.mangakeeper.backend.exceptions.UserAlreadyExistsException;
import dev.flmelo.mangakeeper.backend.repository.RoleRepository;
import dev.flmelo.mangakeeper.backend.repository.UsuarioRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class AuthenticationService {
    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthenticationService(UsuarioRepository usuarioRepository, RoleRepository roleRepository,
                                AuthenticationManager authenticationManager, TokenService tokenService) {
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    public UsuarioResponseDTO register(RegisterDTO data){
        if (usuarioRepository.findByUsername(data.login()).isPresent()){
            throw new UserAlreadyExistsException();
        }
        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        Usuario newUser = new Usuario(
                data.login().toLowerCase(Locale.ROOT),
                data.email(),
                encryptedPassword,
                data.avatarUrl());
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

    public LoginResponseDTO login(AuthenticationDTO authData){
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(authData.login(), authData.password());
            var auth = this.authenticationManager.authenticate(usernamePassword);
            var token = tokenService.gerarToken((Usuario) auth.getPrincipal());
            return new LoginResponseDTO(token);
        } catch (BadCredentialsException e){
            throw new BadCredentialsException("Usuário ou senha inválidos");
        } catch (Exception e){
            throw new RuntimeException("Erro ao autenticar usuário", e);
        }
    }

}
