package dev.flmelo.mangakeeper.backend.entity;

import dev.flmelo.mangakeeper.backend.entity.enuns.Idioma;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "mangas", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"colecao_id", "titulo", "idioma"})})



public class Manga {
    @Id
    @GeneratedValue( strategy =  GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String titulo;

    private String issn;

    @NotBlank
    private String autor;

    @NotBlank
    private String editora;

    @NotBlank
    private String genero;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String sinopse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Idioma idioma;

    @NotNull
    @Positive
    private Integer totalVolumes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "colecao_id", nullable = false)
    private Colecao colecao;

    public Manga() {
    }

    public Manga(String titulo, String issn, String autor, String editora, String genero, String sinopse, Idioma idioma,Integer totalVolumes, Colecao colecao) {
        this.titulo = titulo;
        this.issn = issn;
        this.autor = autor;
        this.editora = editora;
        this.genero = genero;
        this.sinopse = sinopse;
        this.idioma = idioma;
        this.totalVolumes = totalVolumes;
        this.colecao = colecao;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getEditora() {
        return editora;
    }

    public void setEditora(String editora) {
        this.editora = editora;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    public Integer getTotalVolumes() {
        return totalVolumes;
    }

    public void setTotalVolumes(Integer totalVolumes) {
        this.totalVolumes = totalVolumes;
    }

    public Colecao getColecao() {
        return colecao;
    }

    public void setColecao(Colecao colecao) {
        this.colecao = colecao;
    }


    public String getIssn() {
        return issn;
    }

    public void setIssn(String issn) {
        this.issn = issn;
    }

    public Idioma getIdioma() {
        return idioma;
    }

    public void setIdioma(Idioma idioma) {
        this.idioma = idioma;
    }
}
