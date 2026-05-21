package com.backend.trego.entity.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DTOGeoapifyProperties {

    @JsonProperty("street")
    private String calle;

    @JsonProperty("housenumber")
    private String numeroPuerta;

    @JsonProperty("suburb")
    private String barrio;

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getNumeroPuerta() {
        return numeroPuerta;
    }

    public void setNumeroPuerta(String numeroPuerta) {
        this.numeroPuerta = numeroPuerta;
    }

    public String getBarrio() {
        return barrio;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }
}
