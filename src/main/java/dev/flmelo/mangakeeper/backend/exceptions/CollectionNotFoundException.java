package dev.flmelo.mangakeeper.backend.exceptions;

public class CollectionNotFoundException extends RuntimeException{
    public CollectionNotFoundException() { super("Coleção não encontrada.");
    }

    public CollectionNotFoundException(String message) { super(message);}
}
