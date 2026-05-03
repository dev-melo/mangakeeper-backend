package dev.flmelo.mangakeeper.backend.infra;

import dev.flmelo.mangakeeper.backend.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    private ResponseEntity<RestErrorMessage> userNotFoundHandler(UserNotFoundException exception){
        HttpStatus status = HttpStatus.NOT_FOUND;
        Integer statusCode = status.value();
        RestErrorMessage errorResponse = new RestErrorMessage(status,statusCode, exception.getMessage());
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    private ResponseEntity<RestErrorMessage> userAlreadyExistsHandler(UserAlreadyExistsException exception){
        HttpStatus status = HttpStatus.CONFLICT;
        Integer statusCode = status.value();
        RestErrorMessage errorResponse = new RestErrorMessage(status,statusCode, exception.getMessage());
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(CollectionNotFoundException.class)
    private ResponseEntity<RestErrorMessage> colletionNotFoundHandler(CollectionNotFoundException exception){
        HttpStatus status = HttpStatus.NOT_FOUND;
        Integer statusCode = status.value();
        RestErrorMessage errorResponse = new RestErrorMessage(status,statusCode, exception.getMessage());
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(InvalidCollectionNameException.class)
    private ResponseEntity<RestErrorMessage> invalidCollectionNameHandler(InvalidCollectionNameException exception){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Integer statusCode = status.value();
        RestErrorMessage errorResponse = new RestErrorMessage(status,statusCode, exception.getMessage());
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(MangaNotFoundException.class)
    private ResponseEntity<RestErrorMessage> mangaNotFoundHandler(MangaNotFoundException exception){
        HttpStatus status = HttpStatus.NOT_FOUND;
        Integer statusCode = status.value();
        RestErrorMessage errorResponse = new RestErrorMessage(status,statusCode, exception.getMessage());
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(VolumeNotFoundException.class)
    private ResponseEntity<RestErrorMessage> volumeNotFoundHandler(VolumeNotFoundException exception){
        HttpStatus status = HttpStatus.NOT_FOUND;
        Integer statusCode = status.value();
        RestErrorMessage errorResponse = new RestErrorMessage(status,statusCode, exception.getMessage());
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(LikeAlreadyExistsException.class)
    private ResponseEntity<RestErrorMessage> likeAlreadyExistsException(LikeAlreadyExistsException exception){
        HttpStatus status = HttpStatus.CONFLICT;
        Integer statusCode = status.value();
        RestErrorMessage errorResponse = new RestErrorMessage(status,statusCode, exception.getMessage());
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    private ResponseEntity<RestErrorMessage> accessDeniedHandler(AccessDeniedException exception){
        HttpStatus status = HttpStatus.FORBIDDEN;
        Integer statusCode = status.value();
        RestErrorMessage errorResponse = new RestErrorMessage(status,statusCode, exception.getMessage());
        return ResponseEntity.status(status).body(errorResponse);
    }
}
