package dev.flmelo.mangakeeper.backend.exceptions;

public class VolumeNotFoundException extends RuntimeException{
    public VolumeNotFoundException() { super("Volume não encontrado.");}

    public VolumeNotFoundException(String message) { super(message);}
}
