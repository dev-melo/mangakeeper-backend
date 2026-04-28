package dev.flmelo.mangakeeper.backend.exceptions;

public class LikeAlreadyExistsException extends RuntimeException {
    public LikeAlreadyExistsException() {super("Este usuario já curtiu essa coleção.");}

    public LikeAlreadyExistsException(String message) {
        super(message);
    }
}
