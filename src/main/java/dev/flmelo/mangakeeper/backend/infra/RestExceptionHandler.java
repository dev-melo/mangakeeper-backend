package dev.flmelo.mangakeeper.backend.infra;

import dev.flmelo.mangakeeper.backend.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    private ResponseEntity<RestErrorMessage> userNotFoundHandler(UserNotFoundException exception){
        HttpStatus status = HttpStatus.NOT_FOUND;
        RestErrorMessage errorResponse = new RestErrorMessage(status, exception.getMessage());
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    private ResponseEntity<RestErrorMessage> userAlreadyExistsHandler(UserAlreadyExistsException exception){
        HttpStatus status = HttpStatus.CONFLICT;
        RestErrorMessage errorResponse = new RestErrorMessage(status, exception.getMessage());
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(CollectionNotFoundException.class)
    private ResponseEntity<RestErrorMessage> colletionNotFoundHandler(CollectionNotFoundException exception){
        HttpStatus status = HttpStatus.NOT_FOUND;
        RestErrorMessage errorResponse = new RestErrorMessage(status, exception.getMessage());
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(InvalidCollectionNameException.class)
    private ResponseEntity<RestErrorMessage> invalidCollectionNameHandler(InvalidCollectionNameException exception){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        RestErrorMessage errorResponse = new RestErrorMessage(status, exception.getMessage());
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(MangaNotFoundException.class)
    private ResponseEntity<RestErrorMessage> mangaNotFoundHandler(MangaNotFoundException exception){
        HttpStatus status = HttpStatus.NOT_FOUND;
        RestErrorMessage errorResponse = new RestErrorMessage(status, exception.getMessage());
        return ResponseEntity.status(status).body(errorResponse);
    }

}
