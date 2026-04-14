package dev.flmelo.mangakeeper.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "curtidas",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"usuario_id", "colecao_id"})
        }
)
public class Curtida {
    @Id
    @GeneratedValue( strategy =  GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataCurtida;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "colecao_id", nullable = false)
    private Colecao colecao;

    public Colecao getColecao() {
        return colecao;
    }

    public void setColecao(Colecao colecao) {
        this.colecao = colecao;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Curtida() {
    }

    public Curtida(Usuario usuario, Colecao colecao) {

        this.usuario = usuario;
        this.colecao = colecao;
    }

    public LocalDateTime getDataCurtida() {
        return dataCurtida;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    @PrePersist
    public void prePersist(){
        this.dataCurtida = LocalDateTime.now();
    }
}
