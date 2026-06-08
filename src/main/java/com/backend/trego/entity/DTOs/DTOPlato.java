package com.backend.trego.entity.DTOs;

public class DTOPlato {

    private Integer tiempoPreparacionMinutos;

    protected DTOPlato() {
    }

    public DTOPlato(Integer tiempoPreparacionMinutos) {
        this.tiempoPreparacionMinutos = tiempoPreparacionMinutos;
    }

    public Integer getTiempoPreparacionMinutos() {
        return tiempoPreparacionMinutos;
    }
}
