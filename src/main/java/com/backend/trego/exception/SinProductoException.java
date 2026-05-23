package com.backend.trego.exceptions;

public class SinProductoException extends RuntimeException {
    public SinProductoException(String mensaje) {
        super(mensaje);
    }
}
