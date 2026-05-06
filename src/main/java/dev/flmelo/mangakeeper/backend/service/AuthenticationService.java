package dev.flmelo.mangakeeper.backend.service;

import dev.flmelo.mangakeeper.backend.dto.security.RegisterDTO;
import dev.flmelo.mangakeeper.backend.entity.Usuario;
import dev.flmelo.mangakeeper.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    public ResponseEntity register(RegisterDTO data){
        if (usuarioRepository.findByUsername(data.login()).isPresent()) return ResponseEntity.badRequest().build();
        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        Usuario newUser = new Usuario(data.login(), encryptedPassword, data.role());
        try{
            usuarioRepository.save(newUser);
        } catch (DataIntegrityViolationException e){
            throw new DataIntegrityViolationException("Usuario já existe");
        }
        return ResponseEntity.ok().build();
    }
}
