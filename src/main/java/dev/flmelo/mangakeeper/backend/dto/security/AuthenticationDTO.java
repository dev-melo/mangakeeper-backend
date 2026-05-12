package dev.flmelo.mangakeeper.backend.dto.security;

public record AuthenticationDTO(
        String login,
        String password
) {
}
