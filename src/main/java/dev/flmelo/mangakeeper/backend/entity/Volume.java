package dev.flmelo.mangakeeper.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "volumes")
public class Volume {

    @Id
    @GeneratedValue( strategy =  GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Integer numero;

    @NotNull
    @Pattern(regexp = "\\d{13}")
    private String codigoDeBarras;

    private String isbn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manga_id", nullable = false)
    private Manga manga;

    private String imagemUrl;

    public Volume() {}

    public Volume(Integer numero, String codigoDeBarras, String isbn, Manga manga, String imagemUrl) {
        this.numero = numero;
        this.codigoDeBarras = codigoDeBarras;
        this.isbn = isbn;
        this.manga = manga;
        this.imagemUrl = imagemUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getCodigoDeBarras() {
        return codigoDeBarras;
    }

    public void setCodigoDeBarras(String codigoDeBarras) {
        this.codigoDeBarras = codigoDeBarras;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Manga getManga() {
        return manga;
    }

    public void setManga(Manga manga) {
        this.manga = manga;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }
}
