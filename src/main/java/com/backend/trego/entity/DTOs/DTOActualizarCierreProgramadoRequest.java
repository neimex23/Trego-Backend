package com.backend.trego.entity.DTOs;

import java.time.LocalDateTime;

public class DTOActualizarCierreProgramadoRequest {

    private final LocalDateTime cierreProgramado;

    public DTOActualizarCierreProgramadoRequest(LocalDateTime cierreProgramado) {
        this.cierreProgramado = cierreProgramado;
    }

    public LocalDateTime getCierreProgramado() {
        return cierreProgramado;
    }
}
