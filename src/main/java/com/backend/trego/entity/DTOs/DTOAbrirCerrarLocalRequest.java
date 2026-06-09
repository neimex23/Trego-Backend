package com.backend.trego.entity.DTOs;
import java.time.LocalTime;

public class DTOAbrirCerrarLocalRequest {
    
    private final LocalTime horaApertura;
    private final LocalTime horaCierre;

    public DTOAbrirCerrarLocalRequest(LocalTime horaApertura, LocalTime horaCierre) {
        this.horaApertura = horaApertura;
        this.horaCierre = horaCierre;
    }

    public LocalTime getHoraApertura() {
        return horaApertura;
    }

    public LocalTime getHoraCierre() {
        return horaCierre;
    }
}
