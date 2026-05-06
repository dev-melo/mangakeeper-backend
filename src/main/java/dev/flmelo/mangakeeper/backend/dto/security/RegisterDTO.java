package dev.flmelo.mangakeeper.backend.dto.security;

import dev.flmelo.mangakeeper.backend.entity.RoleModel;

public record RegisterDTO(String login, String password, RoleModel role) {
}
