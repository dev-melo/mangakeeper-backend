package dev.flmelo.mangakeeper.backend.exceptions;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException() { super("Usuário não encontrado.");}

    public UserNotFoundException(String message) {super(message);}
}
