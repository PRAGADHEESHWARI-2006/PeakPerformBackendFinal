package com.example.PeakPerform.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> buildError(HttpStatus status, String message) {

        Map<String, Object> error = new HashMap<>();

        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", status.value());
        error.put("error", status.getReasonPhrase());
        error.put("message", message);

        return error;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {

        return new ResponseEntity<>(buildError(HttpStatus.NOT_FOUND, ex.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BusinessValidationException.class)
    public ResponseEntity<Map<String, Object>> handleBusiness(BusinessValidationException ex) {

        return new ResponseEntity<>(buildError(HttpStatus.CONFLICT, ex.getMessage()),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException ex) {

        Map<String, Object> body = buildError(HttpStatus.BAD_REQUEST,
                "Validation Failed");

        Map<String, String> fieldErrors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->

                fieldErrors.put(error.getField(), error.getDefaultMessage()));

        body.put("fieldErrors", fieldErrors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
public ResponseEntity<Map<String, Object>> handleBadCredentials() {

    return new ResponseEntity<>(
            buildError(HttpStatus.UNAUTHORIZED,
                    "Invalid email or password."),
            HttpStatus.UNAUTHORIZED);
}

@ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
public ResponseEntity<Map<String, Object>> handleAccessDenied() {

    return new ResponseEntity<>(
            buildError(HttpStatus.FORBIDDEN,
                    "You do not have permission to perform this action."),
            HttpStatus.FORBIDDEN);
}

@ExceptionHandler(Exception.class)
public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {

    return new ResponseEntity<>(
            buildError(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "An unexpected error occurred: " + ex.getMessage()),
            HttpStatus.INTERNAL_SERVER_ERROR);
}
}