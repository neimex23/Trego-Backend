package com.backend.trego.config;

import org.springframework.http.HttpStatus;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Hidden
public class GlobalExceptionHandler {

    /**
     * Captura los errores automáticos de @Valid cuando los campos no cumplen las
     * reglas del DTO.
     * (Flujo Alternativo 4.1 y 4.3 del caso de uso)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();

        // Recorremos todos los errores que encontró Spring en el DTO
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String campo = ((FieldError) error).getField();
            String mensajeServidor = error.getDefaultMessage();

            // Guardamos el campo (ej: "password") y la razón (ej: "La contraseña debe

            // al menos un número...")
            errores.put(campo, mensajeServidor);
        });

        // Retornamos un HTTP 400 Bad Request junto con el mapa de campos erróneos
        return new ResponseEntity<>(errores, HttpStatus.BAD_REQUEST);
    }
}