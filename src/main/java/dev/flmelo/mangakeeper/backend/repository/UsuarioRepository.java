package dev.flmelo.mangakeeper.backend.repository;

import dev.flmelo.mangakeeper.backend.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

}
