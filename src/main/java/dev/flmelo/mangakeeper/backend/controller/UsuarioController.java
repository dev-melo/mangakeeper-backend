package dev.flmelo.mangakeeper.backend.controller;

import dev.flmelo.mangakeeper.backend.dto.usuario.UpdateUsuarioDTO;
import dev.flmelo.mangakeeper.backend.dto.usuario.UsuarioResponseDTO;
import dev.flmelo.mangakeeper.backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("usuarios")
@Tag(name = "Usuários", description = "Endpoints de Usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Listar usuários", description = "Retorna todos usuários cadastrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> getAll(){
        List<UsuarioResponseDTO> list = usuarioService.getAll();
        return ResponseEntity.ok().body(list);
    }

    @Operation(summary = "Buscar usuário por ID", description = "Retorna um usuário específica. ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<UsuarioResponseDTO> getUsuarioById(@PathVariable Long id){
        UsuarioResponseDTO usuario = usuarioService.getById(id);
        return ResponseEntity.ok().body(usuario);
    }


    @Operation(summary = "Atualizar usuário", description = "Atualiza dados do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados do usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PatchMapping(value = "/{id}")
    public ResponseEntity<UsuarioResponseDTO> updateUsuario(@Valid @PathVariable Long id, @RequestBody UpdateUsuarioDTO usuarioUpdate){
        UsuarioResponseDTO usuarioAtualizado = usuarioService.updateUsuario(id, usuarioUpdate);

        return ResponseEntity.ok().body(usuarioAtualizado);
    }

    @Operation(summary = "Deletar usuário", description = "Remove usuário do sistema por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
