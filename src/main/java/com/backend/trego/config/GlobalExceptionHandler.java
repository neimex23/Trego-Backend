package com.backend.trego.config;

import org.springframework.http.HttpStatus;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import com.backend.trego.exception.PagoRechazadoException;
import com.backend.trego.exception.RestauranteCerradoException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Hidden
public class GlobalExceptionHandler {

    // Captura los errores de @Valid cuando un DTO no cumple sus reglas.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String campo = ((FieldError) error).getField();
            String mensajeServidor = error.getDefaultMessage();
            errores.put(campo, mensajeServidor);
        });

        return new ResponseEntity<>(errores, HttpStatus.BAD_REQUEST);
    }

    // Normaliza las ResponseStatusException a un JSON {message} conservando el código original.
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatus(ResponseStatusException ex) {
        Map<String, String> body = new HashMap<>();
        String mensaje = ex.getReason() != null ? ex.getReason() : ex.getStatusCode().toString();
        body.put("message", mensaje);
        return new ResponseEntity<>(body, HttpStatus.valueOf(ex.getStatusCode().value()));
    }

    @ExceptionHandler(RestauranteCerradoException.class)
    public ResponseEntity<Map<String, String>> handleRestauranteCerrado(RestauranteCerradoException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    // Pago rechazado por la pasarela: 402 Payment Required con el idPedido para
    // que el front pueda ofrecer reintentar el pago.
    @ExceptionHandler(PagoRechazadoException.class)
    public ResponseEntity<Map<String, Object>> handlePagoRechazado(PagoRechazadoException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "PAGO_RECHAZADO");
        body.put("mensaje", ex.getMessage());
        body.put("idPedido", ex.getIdPedido());
        return new ResponseEntity<>(body, HttpStatus.PAYMENT_REQUIRED);
    }
}