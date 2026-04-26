package dev.flmelo.mangakeeper.backend.repository;

import dev.flmelo.mangakeeper.backend.entity.Curtida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurtidaRepository extends JpaRepository<Curtida, Long> {
}
