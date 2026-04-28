package dev.flmelo.mangakeeper.backend.controller;

import dev.flmelo.mangakeeper.backend.dto.colecao.ColecaoResponseDTO;
import dev.flmelo.mangakeeper.backend.dto.colecao.CreateColecaoDTO;
import dev.flmelo.mangakeeper.backend.dto.colecao.UpdateColecaoDTO;
import dev.flmelo.mangakeeper.backend.service.ColecaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "colecoes")
@Tag(name = "Coleções", description = "Endpoints de Coleções")
public class ColecaoController {

    private final ColecaoService colecaoService;

    public ColecaoController(ColecaoService colecaoService) {
        this.colecaoService = colecaoService;
    }

    @Operation(summary = "Listar coleções", description = "Retorna todas coleções cadastradas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    @GetMapping
    public ResponseEntity<List<ColecaoResponseDTO>> getAll(){
        List<ColecaoResponseDTO> list = colecaoService.getAll();
        return ResponseEntity.ok().body(list);
    }

    @Operation(summary = "Buscar coleção por ID", description = "Retorna uma coleção específica. ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coleção encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Coleção não encontrada.")
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<ColecaoResponseDTO> getById(@PathVariable Long id){
        ColecaoResponseDTO colecao = colecaoService.getById(id);
        return ResponseEntity.ok().body(colecao);
    }

    @Operation(summary = "Cria uma coleção", description = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Coleção criada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuario informado não foi encontrado."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<ColecaoResponseDTO> create(@Valid @RequestBody CreateColecaoDTO request){
        ColecaoResponseDTO colecaoCriada = colecaoService.create(request);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}").buildAndExpand(colecaoCriada.id()).toUri();
        return ResponseEntity.created(uri).body(colecaoCriada);

    }
    @Operation(summary = "Atualiza coleção", description = "Atualiza o nome da coleção")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nome da coleção atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Coleção não encontrada"),
            @ApiResponse(responseCode = "400", description = "Nome da coleção inválido")
    })
    @PatchMapping(value = "/{id}")
    public ResponseEntity<ColecaoResponseDTO> updateColecao(@PathVariable Long id, @Valid  @RequestBody UpdateColecaoDTO colecaoUpdate){
        ColecaoResponseDTO colecao = colecaoService.updateColecao(id, colecaoUpdate);
        return ResponseEntity.ok().body(colecao);
    }

    @Operation(summary = "Deleta uma coleção", description = "Remove coleção do sistema por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Coleção deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Coleção não encontrada")
    })
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        colecaoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
