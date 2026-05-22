package com.backend.trego.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Se lanza cuando se intenta confirmar un pedido para un restaurante que no
// está operativo (deshabilitado o fuera de su horario de atención). Spring la
// traduce a un 409 Conflict gracias a @ResponseStatus.
@ResponseStatus(HttpStatus.CONFLICT)
public class RestauranteCerradoException extends RuntimeException {

    public RestauranteCerradoException(String mensaje) {
        super(mensaje);
    }
}
