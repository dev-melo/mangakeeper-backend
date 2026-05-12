package dev.flmelo.mangakeeper.backend.service;

import dev.flmelo.mangakeeper.backend.config.security.SecurityContextService;
import dev.flmelo.mangakeeper.backend.dto.colecao.ColecaoResponseDTO;
import dev.flmelo.mangakeeper.backend.dto.colecao.CreateColecaoDTO;
import dev.flmelo.mangakeeper.backend.dto.colecao.UpdateColecaoDTO;
import dev.flmelo.mangakeeper.backend.entity.Colecao;
import dev.flmelo.mangakeeper.backend.entity.Usuario;
import dev.flmelo.mangakeeper.backend.exceptions.CollectionNotFoundException;
import dev.flmelo.mangakeeper.backend.exceptions.InvalidCollectionNameException;
import dev.flmelo.mangakeeper.backend.repository.ColecaoRepository;
import dev.flmelo.mangakeeper.backend.repository.CurtidaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ColecaoService {
    private final SecurityContextService securityContextService;
    private final ColecaoRepository colecaoRepository;
    private final CurtidaRepository curtidaRepository;

    public ColecaoService(SecurityContextService securityContextService, ColecaoRepository colecaoRepository, CurtidaRepository curtidaRepository) {
        this.securityContextService = securityContextService;
        this.colecaoRepository = colecaoRepository;
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
        Colecao colecao = colecaoRepository.findById(id).orElseThrow(CollectionNotFoundException::new);

        return new ColecaoResponseDTO(
                colecao.getId(),
                colecao.getNome(),
                colecao.getPublico(),
                colecao.getUsuario().getId());

    }

    public ColecaoResponseDTO create(CreateColecaoDTO request){
        Colecao novaColecao = new Colecao();

        Usuario u = securityContextService.getUsuarioLogado();

//        Optional<Usuario> usuario = usuarioRepository.findById(request.usuarioId());
//
//        if (usuario.isEmpty()){
//            throw new UserNotFoundException();
//        }
//
//        Usuario u = usuario.get();

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

        Colecao c = colecaoRepository.findById(id).orElseThrow(CollectionNotFoundException::new);
        String nomeColecao = updateColecaoDTO.nome();

        securityContextService.validaDonoOuAdmin(c.getUsuario());
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
        Colecao colecao = colecaoRepository.findById(id).orElseThrow(CollectionNotFoundException::new);

        securityContextService.validaDonoOuAdmin(colecao.getUsuario());
        curtidaRepository.deleteByColecaoId(id);

        colecaoRepository.deleteById(id);
    }
}
