package dev.flmelo.mangakeeper.backend.controller;

import dev.flmelo.mangakeeper.backend.dto.usuario.CreateUsuarioDTO;
import dev.flmelo.mangakeeper.backend.dto.usuario.UpdateUsuarioDTO;
import dev.flmelo.mangakeeper.backend.dto.usuario.UsuarioResponseDTO;
import dev.flmelo.mangakeeper.backend.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> getAll(){
        List<UsuarioResponseDTO> list = usuarioService.getAll();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<UsuarioResponseDTO> getUsuarioById(@PathVariable Long id){
        UsuarioResponseDTO usuario = usuarioService.getById(id);
        return ResponseEntity.ok().body(usuario);
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> createUser(@Valid @RequestBody CreateUsuarioDTO request){
        UsuarioResponseDTO usuarioSalvo = usuarioService.create(request);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(usuarioSalvo.id())
                .toUri();

        return ResponseEntity.created(uri).body(usuarioSalvo);
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<UsuarioResponseDTO> updateUsuario(@Valid @PathVariable Long id, @RequestBody UpdateUsuarioDTO usuarioUpdate){
        UsuarioResponseDTO usuarioAtualizado = usuarioService.updateUsuario(id, usuarioUpdate);

        return ResponseEntity.ok().body(usuarioAtualizado);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
