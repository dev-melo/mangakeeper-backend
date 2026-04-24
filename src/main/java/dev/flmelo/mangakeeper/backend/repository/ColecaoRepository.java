package dev.flmelo.mangakeeper.backend.repository;

import dev.flmelo.mangakeeper.backend.entity.Colecao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColecaoRepository extends JpaRepository<Colecao, Long> {
}
