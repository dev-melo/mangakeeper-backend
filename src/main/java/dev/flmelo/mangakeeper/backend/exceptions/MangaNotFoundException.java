package dev.flmelo.mangakeeper.backend.exceptions;

public class MangaNotFoundException extends RuntimeException{
    public MangaNotFoundException() { super("Mangá não Encontrado");}

    public MangaNotFoundException(String message) { super(message);}
}
