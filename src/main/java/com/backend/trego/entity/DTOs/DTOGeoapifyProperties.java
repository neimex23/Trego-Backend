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

    public String getNumeroPuerta() {
        return numeroPuerta;
    }

    public String getBarrio() {
        return barrio;
    }
}
