package dev.flmelo.mangakeeper.backend.infra;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public class RestErrorMessage {
    private HttpStatus status;
    private String message;
}
