package ru.netology.cloudstorage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.netology.cloudstorage.dto.ErrorResponse;

@RestControllerAdvice

public class GlobalExceptionHandler {

    @ExceptionHandler(
            BadCredentialsException.class
    )

    public ResponseEntity<ErrorResponse>
    handleBadCredentials(
            BadCredentialsException e
    ) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)

                .body(
                        ErrorResponse.builder()
                                .message(e.getMessage())
                                .id(400)
                                .build()
                );
    }

    @ExceptionHandler(
            UnauthorizedException.class
    )

    public ResponseEntity<ErrorResponse>
    handleUnauthorized(
            UnauthorizedException e
    ) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)

                .body(
                        ErrorResponse.builder()
                                .message(e.getMessage())
                                .id(401)
                                .build()
                );
    }

    @ExceptionHandler(
            FileNotFoundException.class
    )

    public ResponseEntity<ErrorResponse>
    handleFileNotFound(
            FileNotFoundException e
    ) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)

                .body(
                        ErrorResponse.builder()
                                .message(e.getMessage())
                                .id(404)
                                .build()
                );
    }

    @ExceptionHandler(Exception.class)

    public ResponseEntity<ErrorResponse>
    handleOther(
            Exception e
    ) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)

                .body(
                        ErrorResponse.builder()
                                .message(e.getMessage())
                                .id(500)
                                .build()
                );
    }

    @ExceptionHandler(
            FileAlreadyExistsException.class
    )

    public ResponseEntity<ErrorResponse>
    handleFileAlreadyExists(
            FileAlreadyExistsException e
    ) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)

                .body(
                        ErrorResponse.builder()
                                .message(e.getMessage())
                                .id(400)
                                .build()
                );
    }
}