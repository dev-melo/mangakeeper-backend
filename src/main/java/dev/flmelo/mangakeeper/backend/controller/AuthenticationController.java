package dev.flmelo.mangakeeper.backend.controller;

import dev.flmelo.mangakeeper.backend.dto.security.AuthenticationDTO;
import dev.flmelo.mangakeeper.backend.dto.security.LoginResponseDTO;
import dev.flmelo.mangakeeper.backend.dto.security.RegisterDTO;
import dev.flmelo.mangakeeper.backend.dto.usuario.UsuarioResponseDTO;
import dev.flmelo.mangakeeper.backend.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "auth")
@Tag(name = "Autenticação", description = "Endpoints para registro, login e geração de tokens JWT")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Operation(
            summary = "Realiza autenticação do usuário",
            description = """
                Endpoint responsável por autenticar um usuário utilizando username e senha.
                
                Se as credenciais forem válidas, um token JWT será gerado e retornado.
                
                O token deve ser enviado nas próximas requisições protegidas no header:
                
                Authorization: Bearer <token>
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login realizado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Usuário ou senha inválidos"
            )
    })
    @PostMapping(value = "/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO data){
        LoginResponseDTO loginResponse = authenticationService.login(data);
        return ResponseEntity.ok(loginResponse);
    }

    @Operation(summary = "Criar usuário", description = "Criar um usuário no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping(value = "/register")
    public ResponseEntity<UsuarioResponseDTO> register(@RequestBody @Valid RegisterDTO data){

        UsuarioResponseDTO usuarioSalvo = authenticationService.register(data);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(usuarioSalvo.id())
                .toUri();

        return ResponseEntity.created(uri).body(usuarioSalvo);

    }
}
