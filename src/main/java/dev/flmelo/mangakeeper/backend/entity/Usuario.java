package dev.flmelo.mangakeeper.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotBlank
    @Size(min = 4, max = 20, message = "O usuário precisa ter entre 4 a 20 caracteres.")
    private String username;

    @Column(unique = true)
    @Email(message = "O e-mail deve ser válido.")
    @NotBlank
    @Size(max = 40)
    private String email;

    @NotBlank
    @Size(min = 8, max = 20, message = "A senha precisa ter entre 8 a 20 caracteres.")
    private String password;

    private String avatarUrl;

    public Usuario() {
    }

    public Usuario(String username, String email, String password, String avatarUrl) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.avatarUrl = avatarUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}


