package com.backend.trego.entity.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DTOGeoapifyProperties {

    @JsonProperty("name")
    private String tag;

    // --- Campos de geocoding (reverse geocode) ---
    @JsonProperty("street")
    private String calle;

    @JsonProperty("housenumber")
    private String numeroPuerta;

    @JsonProperty("suburb")
    private String barrio;

    // --- Campos de routing ---
    // distancia en metros
    @JsonProperty("distance")
    private Double distancia;

    // tiempo estimado en segundos
    @JsonProperty("time")
    private Double tiempo;

    public String getTag() {
        return tag;
    }

    public String getCalle() {
        return calle;
    }

    public String getNumeroPuerta() {
        return numeroPuerta;
    }

    public String getBarrio() {
        return barrio;
    }

    public Double getDistancia() {
        return distancia;
    }

    public Double getTiempo() {
        return tiempo;
    }
}
