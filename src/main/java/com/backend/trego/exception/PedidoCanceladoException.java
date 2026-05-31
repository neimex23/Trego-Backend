package com.backend.trego.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class PedidoCanceladoException extends RuntimeException {
    public PedidoCanceladoException(String mensaje) {
        super(mensaje);
    }
}
