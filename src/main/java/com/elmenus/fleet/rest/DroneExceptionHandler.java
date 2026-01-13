package com.elmenus.fleet.rest;

import com.elmenus.fleet.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class DroneExceptionHandler {

    @ExceptionHandler(DuplicateSerialNumberException.class)
    public ResponseEntity<DroneErrorResponse> handleDuplicateSerialNumberException(DuplicateSerialNumberException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(createErrorResponse(ex));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<DroneErrorResponse> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorResponse(ex));
    }

    @ExceptionHandler(DroneLoadingException.class)
    public ResponseEntity<DroneErrorResponse> handleDroneLoadingException(DroneLoadingException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse(ex));
    }

    @ExceptionHandler(DroneNotLoadedException.class)
    public ResponseEntity<DroneErrorResponse> handleDroneNotLoadedException(DroneNotLoadedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse(ex));
    }

    @ExceptionHandler(LoadEmptyException.class)
    public ResponseEntity<DroneErrorResponse> handleLoadEmptyException(LoadEmptyException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse(ex));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DroneErrorResponse> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new DroneErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", System.currentTimeMillis()));
    }

    private DroneErrorResponse createErrorResponse(Exception ex) {
        return new DroneErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), System.currentTimeMillis());
    }

}
