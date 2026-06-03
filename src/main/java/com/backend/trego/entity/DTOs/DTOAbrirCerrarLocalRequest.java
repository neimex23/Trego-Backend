package com.backend.trego.entity.DTOs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalTime;

public class DTOAbrirCerrarLocalRequest {
    
    private final LocalTime horaCierre;

    @JsonCreator
    public DTOAbrirCerrarLocalRequest(@JsonProperty("horaCierre") LocalTime horaCierre) {
        this.horaCierre = horaCierre;
    }

    public LocalTime getHoraCierre() {
        return horaCierre;
    }
}
