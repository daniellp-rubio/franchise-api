package com.company.franchise.exception;

import com.company.franchise.dto.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.stream.Collectors;

/*
 * Manejador global de excepciones
 * Captura todas las excepciones lanzadas por los controladores
 * Y las convierte en respuestas HTTP estructuradas
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomExceptions.FranchiseNotFoundException.class)
    public ResponseEntity<ResponseDTO.ErrorResponse> handleFranchiseNotFound(
            CustomExceptions.FranchiseNotFoundException ex) {

        ResponseDTO.ErrorResponse error = ResponseDTO.ErrorResponse.builder()
                .message(ex.getMessage())
                .error("NOT_FOUND")
                .status(HttpStatus.NOT_FOUND.value())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(CustomExceptions.BranchNotFoundException.class)
    public ResponseEntity<ResponseDTO.ErrorResponse> handleBranchNotFound(
            CustomExceptions.BranchNotFoundException ex) {

        ResponseDTO.ErrorResponse error = ResponseDTO.ErrorResponse.builder()
                .message(ex.getMessage())
                .error("NOT_FOUND")
                .status(HttpStatus.NOT_FOUND.value())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(CustomExceptions.ProductNotFoundException.class)
    public ResponseEntity<ResponseDTO.ErrorResponse> handleProductNotFound(
            CustomExceptions.ProductNotFoundException ex) {

        ResponseDTO.ErrorResponse error = ResponseDTO.ErrorResponse.builder()
                .message(ex.getMessage())
                .error("NOT_FOUND")
                .status(HttpStatus.NOT_FOUND.value())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(CustomExceptions.DuplicateNameException.class)
    public ResponseEntity<ResponseDTO.ErrorResponse> handleDuplicateName(
            CustomExceptions.DuplicateNameException ex) {

        ResponseDTO.ErrorResponse error = ResponseDTO.ErrorResponse.builder()
                .message(ex.getMessage())
                .error("CONFLICT")
                .status(HttpStatus.CONFLICT.value())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ResponseDTO.ErrorResponse> handleValidationErrors(
            WebExchangeBindException ex) {

        // Concatena todos los mensajes de error de validación
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        ResponseDTO.ErrorResponse error = ResponseDTO.ErrorResponse.builder()
                .message(errorMessage)
                .error("VALIDATION_ERROR")
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO.ErrorResponse> handleGenericError(Exception ex) {

        ResponseDTO.ErrorResponse error = ResponseDTO.ErrorResponse.builder()
                .message("Error interno del servidor")
                .error("INTERNAL_SERVER_ERROR")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
