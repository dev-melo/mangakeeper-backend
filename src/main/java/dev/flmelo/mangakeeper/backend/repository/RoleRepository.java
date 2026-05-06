package dev.flmelo.mangakeeper.backend.repository;

import dev.flmelo.mangakeeper.backend.entity.RoleModel;
import dev.flmelo.mangakeeper.backend.entity.enuns.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleModel, Long> {
    Optional<RoleModel> findByRoleName(RoleName roleName);
}
