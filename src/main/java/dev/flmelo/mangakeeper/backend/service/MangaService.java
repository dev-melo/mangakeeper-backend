package dev.flmelo.mangakeeper.backend.service;

import dev.flmelo.mangakeeper.backend.dto.manga.CreateMangaDTO;
import dev.flmelo.mangakeeper.backend.dto.manga.MangaResponseDTO;
import dev.flmelo.mangakeeper.backend.dto.manga.UpdateMangaDTO;
import dev.flmelo.mangakeeper.backend.entity.Colecao;
import dev.flmelo.mangakeeper.backend.entity.Manga;
import dev.flmelo.mangakeeper.backend.repository.ColecaoRepository;
import dev.flmelo.mangakeeper.backend.repository.MangaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class MangaService {

    private final MangaRepository mangaRepository;
    private final ColecaoRepository colecaoRepository;

    public MangaService(MangaRepository mangaRepository, ColecaoRepository colecaoRepository) {
        this.mangaRepository = mangaRepository;
        this.colecaoRepository = colecaoRepository;
    }

    public List<MangaResponseDTO> getAll(){
        return mangaRepository.findAll()
                .stream()
                .map(manga ->  new MangaResponseDTO(
                        manga.getId(),
                        manga.getTitulo(),
                        manga.getAutor(),
                        manga.getEditora(),
                        manga.getGenero(),
                        manga.getSinopse(),
                        manga.getIssn(),
                        manga.getTotalVolumes(),
                        manga.getIdioma(),
                        manga.getColecao().getId()
                )).toList();
    }

    public MangaResponseDTO getById(Long id){
        Optional<Manga> mangaById = mangaRepository.findById(id);
        if (mangaById.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Mangá não encontrado.");
        }
        Manga manga = mangaById.get();
        return new MangaResponseDTO(
                manga.getId(),
                manga.getTitulo(),
                manga.getAutor(),
                manga.getEditora(),
                manga.getGenero(),
                manga.getSinopse(),
                manga.getIssn(),
                manga.getTotalVolumes(),
                manga.getIdioma(),
                manga.getColecao().getId()
        );
    }

    public MangaResponseDTO create(CreateMangaDTO request){
        Optional<Colecao> colecao = colecaoRepository.findById(request.colecaoId());
        if (colecao.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Coleção não encontrada.");
        }
        Colecao c = colecao.get();

        String issn = request.issn();
        if (issn != null && issn.trim().isEmpty()){
            issn = null;
        }

        Manga manga = new Manga(
                request.titulo(),
                issn,
                request.autor(),
                request.editora(),
                request.genero(),
                request.sinopse(),
                request.idioma(),
                request.totalVolumes(),
                c
        );



        Manga mangaSalvo = mangaRepository.save(manga);

        return new MangaResponseDTO(
                mangaSalvo.getId(),
                mangaSalvo.getTitulo().trim(),
                mangaSalvo.getAutor().trim(),
                mangaSalvo.getEditora().trim(),
                mangaSalvo.getGenero().trim(),
                mangaSalvo.getSinopse().trim(),
                mangaSalvo.getIssn(),
                mangaSalvo.getTotalVolumes(),
                mangaSalvo.getIdioma(),
                mangaSalvo.getColecao().getId()
        );

    }

    public MangaResponseDTO update(Long id, UpdateMangaDTO request){
        Optional<Manga> mangaById = mangaRepository.findById(id);
        if (mangaById.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Mangá não encontrada.");
        }
        Manga m = mangaById.get();

        if (request.titulo() != null){
            String titulo = clean(request.titulo());
            m.setTitulo(titulo);
        }
        if (request.autor() != null){
            String autor = clean(request.autor());
            m.setAutor(autor);
        }
        if (request.editora() != null){
            String editora = clean(request.editora());
            m.setEditora(editora);
        }
        if (request.genero() != null){
            String genero = clean(request.genero());
            m.setGenero(genero);
        }
        if (request.sinopse() != null){
            String sinopse = clean(request.sinopse());
            m.setSinopse(sinopse);
        }
        if (request.issn() != null){
            String issn = clean(request.issn());
            m.setIssn(issn);
        }
        if (request.totalVolumes() != null){
            m.setTotalVolumes(request.totalVolumes());
        }
        if (request.idioma() != null){
            m.setIdioma(request.idioma());
        }

        Manga mangaSalvo = mangaRepository.save(m);

        return new MangaResponseDTO(
                mangaSalvo.getId(),
                mangaSalvo.getTitulo(),
                mangaSalvo.getAutor(),
                mangaSalvo.getEditora(),
                mangaSalvo.getGenero(),
                mangaSalvo.getSinopse(),
                mangaSalvo.getIssn(),
                mangaSalvo.getTotalVolumes(),
                mangaSalvo.getIdioma(),
                mangaSalvo.getColecao().getId()
        );

    }

    private String clean(String titulo) {
        return titulo.trim();
    }

    public void delete(Long id){
        Optional<Manga> manga = mangaRepository.findById(id);
        if (manga.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Mangá não encontrada.");
        }
        mangaRepository.deleteById(id);
    }
}
