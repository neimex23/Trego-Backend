package com.backend.trego.entity.DTOs;

import jakarta.persistence.Embeddable;

@Embeddable
public class DTODireccion {
    private String calle;
    private int numero;
    private int apartamento;
    private String esquina;
    private double latitud;
    private double longitud;

    protected DTODireccion() {
    }

    public DTODireccion(String calle, int numero, int apartamento, String esquina, double latitud, double longitud) {
        this.calle = calle;
        this.numero = numero;
        this.apartamento = apartamento;
        this.esquina = esquina;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public String getCalle() {
        return calle;
    }

    public int getNumero() {
        return numero;
    }

    public int getApartamento() {
        return apartamento;
    }

    public String getEsquina() {
        return esquina;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }
}
