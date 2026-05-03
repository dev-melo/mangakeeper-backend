package dev.flmelo.mangakeeper.backend.service;

import dev.flmelo.mangakeeper.backend.config.security.AuthService;
import dev.flmelo.mangakeeper.backend.dto.colecao.ColecaoResponseDTO;
import dev.flmelo.mangakeeper.backend.dto.colecao.CreateColecaoDTO;
import dev.flmelo.mangakeeper.backend.dto.colecao.UpdateColecaoDTO;
import dev.flmelo.mangakeeper.backend.entity.Colecao;
import dev.flmelo.mangakeeper.backend.entity.Usuario;
import dev.flmelo.mangakeeper.backend.exceptions.CollectionNotFoundException;
import dev.flmelo.mangakeeper.backend.exceptions.InvalidCollectionNameException;
import dev.flmelo.mangakeeper.backend.exceptions.UserNotFoundException;
import dev.flmelo.mangakeeper.backend.repository.ColecaoRepository;
import dev.flmelo.mangakeeper.backend.repository.CurtidaRepository;
import dev.flmelo.mangakeeper.backend.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class ColecaoService {
    private final AuthService authService;
    private final ColecaoRepository colecaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CurtidaRepository curtidaRepository;

    public ColecaoService(AuthService authService, ColecaoRepository colecaoRepository, UsuarioRepository usuarioRepository, CurtidaRepository curtidaRepository) {
        this.authService = authService;
        this.colecaoRepository = colecaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.curtidaRepository = curtidaRepository;
    }


    public List<ColecaoResponseDTO> getAll() {
        return colecaoRepository.findAll()
                .stream()
                .map(colecao -> new ColecaoResponseDTO(
                        colecao.getId(),
                        colecao.getNome(),
                        colecao.getPublico(),
                        colecao.getUsuario().getId()
                )).toList();
    }

    public ColecaoResponseDTO getById(Long id){
        Optional<Colecao> colecaoById = colecaoRepository.findById(id);
        if (colecaoById.isEmpty()){
            throw new CollectionNotFoundException();
        }

        Colecao colecao = colecaoById.get();

        return new ColecaoResponseDTO(
                colecao.getId(),
                colecao.getNome(),
                colecao.getPublico(),
                colecao.getUsuario().getId());

    }

    public ColecaoResponseDTO create(CreateColecaoDTO request){
        Colecao novaColecao = new Colecao();

        Optional<Usuario> usuario = usuarioRepository.findById(request.usuarioId());

        if (usuario.isEmpty()){
            throw new UserNotFoundException();
        }

        Usuario u = usuario.get();

        //Set Nome da Coleção
        if (request.nome() == null || request.nome().trim().isEmpty() ){
            novaColecao.setNome("Coleção de " + u.getUsername().trim());

        } else {
            novaColecao.setNome(request.nome().trim());
        }

        //Set Pulico True
        novaColecao.setPublico(true);

        // Set Usuario ID
        novaColecao.setUsuario(u);

        colecaoRepository.save(novaColecao);

        return new ColecaoResponseDTO(
                novaColecao.getId(),
                novaColecao.getNome(),
                novaColecao.getPublico(),
                novaColecao.getUsuario().getId()
        );

    }

    public ColecaoResponseDTO updateColecao(Long id, UpdateColecaoDTO updateColecaoDTO){
        Optional<Colecao> colecaoAntiga = colecaoRepository.findById(id);
        if (colecaoAntiga.isEmpty()){
            throw new CollectionNotFoundException();
        }
        String nomeColecao = updateColecaoDTO.nome();

        Colecao c = colecaoAntiga.get();
        authService.validaDonoOuAdmin(c.getUsuario());
        if (nomeColecao != null && !nomeColecao.trim().isEmpty()){
            nomeColecao = nomeColecao.trim();
            if (nomeColecao.matches("[0-9]+")){
                throw new InvalidCollectionNameException();
            }
            c.setNome(nomeColecao);
        } else {
            throw new InvalidCollectionNameException();
        }

        colecaoRepository.save(c);
        return new ColecaoResponseDTO(
                c.getId(),
                c.getNome(),
                c.getPublico(),
                c.getUsuario().getId()
        );
    }

    public void delete(Long id){
        Optional<Colecao> colecao = colecaoRepository.findById(id);
        if (colecao.isEmpty()){
            throw new CollectionNotFoundException();
        }

        authService.validaDonoOuAdmin(colecao.get().getUsuario());
        curtidaRepository.deleteByColecaoId(id);

        colecaoRepository.deleteById(id);
    }
}
