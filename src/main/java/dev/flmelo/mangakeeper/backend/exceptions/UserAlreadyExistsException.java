package dev.flmelo.mangakeeper.backend.exceptions;

public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException() { super("Usuário já existe.");
    }

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
