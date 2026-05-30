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

    public DTODireccion(double latitud, double longitud) {
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

    // Representación legible de la dirección. Se usa en el PDF de comprobante y
    // en los emails. Si no hay calle se cae a las coordenadas; si tampoco hay
    // coordenadas devuelve "Sin dirección".
    @Override
    public String toString() {
        boolean tieneCalle = calle != null && !calle.isBlank();
        if (!tieneCalle) {
            if (latitud != 0 || longitud != 0) {
                return String.format("Lat %.5f, Lon %.5f", latitud, longitud);
            }
            return "Sin dirección";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(calle);
        if (numero > 0) {
            sb.append(" ").append(numero);
        }
        if (esquina != null && !esquina.isBlank()) {
            sb.append(" esq. ").append(esquina);
        }
        if (apartamento > 0) {
            sb.append(", apto ").append(apartamento);
        }
        return sb.toString();
    }
}