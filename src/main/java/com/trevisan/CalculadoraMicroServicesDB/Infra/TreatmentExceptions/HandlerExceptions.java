package com.trevisan.CalculadoraMicroServicesDB.Infra.TreatmentExceptions;

import com.trevisan.CalculadoraMicroServicesDB.Infra.TreatmentExceptions.Exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@RestControllerAdvice
public class HandlerExceptions {


    @ExceptionHandler(GenericException.class)
    public ResponseEntity<ApiError> handleException(Exception e) {
        return new ResponseEntity<>(
                ApiError
                        .builder()
                        .messageError(e.getMessage())
                        .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.name())
                        .timestamp(OffsetDateTime.now())
                        .cause(e.getCause())
                        .messagesErrors(List.of(e.getMessage()))
                        .build(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(InvalidDatabaseNameException.class)
    public ResponseEntity<ApiError> handleInvalidDatabaseNameException(InvalidDatabaseNameException e) {
        return new ResponseEntity<>(
                ApiError
                        .builder()
                        .messageError(e.getMessage())
                        .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.name())
                        .timestamp(OffsetDateTime.now())
                        .cause(e.getCause())
                        .messagesErrors(List.of(e.getMessage()))
                        .build(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(FailedToGetConnectionException.class)
    public ResponseEntity<ApiError> handleFailedToGetConnectionException(FailedToGetConnectionException e) {
        return new ResponseEntity<>(
                ApiError
                        .builder()
                        .messageError(e.getMessage())
                        .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.name())
                        .timestamp(OffsetDateTime.now())
                        .cause(e.getCause())
                        .messagesErrors(List.of(e.getMessage()))
                        .build(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(FailedToExecuteOperationsException.class)
    public ResponseEntity<ApiError> handleFailedToExecuteOperationsException(FailedToExecuteOperationsException e) {
        return new ResponseEntity<>(
                ApiError
                        .builder()
                        .messageError(e.getMessage())
                        .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.name())
                        .timestamp(OffsetDateTime.now())
                        .cause(e.getCause())
                        .messagesErrors(List.of(e.getMessage()))
                        .build(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(ConnectionTestException.class)
    public ResponseEntity<ApiError> handleConnectionTestException(ConnectionTestException e) {
        return new ResponseEntity<>(
                ApiError
                        .builder()
                        .messageError(e.getMessage())
                        .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.name())
                        .timestamp(OffsetDateTime.now())
                        .cause(e.getCause())
                        .messagesErrors(List.of(e.getMessage()))
                        .build(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
