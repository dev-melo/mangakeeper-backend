package dev.flmelo.mangakeeper.backend.exceptions;

public class InvalidCollectionNameException extends RuntimeException{
    public InvalidCollectionNameException() { super("Nome da coleção inválido.");
    }

    public InvalidCollectionNameException(String message) { super(message);}
}
