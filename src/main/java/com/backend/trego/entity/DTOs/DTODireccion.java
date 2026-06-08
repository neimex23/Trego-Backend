package com.backend.trego.entity.DTOs;

import jakarta.persistence.Embeddable;

@Embeddable
public class DTODireccion {

    // Etiqueta opcional de la direccion (por ej. "Casa", "Trabajo" o el "name" que
    // devuelve Geoapify cuando reconoce el lugar).
    private String tag;
    private String calle;
    private String numero;
    private String apartamento;
    private String esquina;
    private double latitud;
    private double longitud;

    protected DTODireccion() {
    }

    public DTODireccion(String tag, String calle, String numero, String apartamento, String esquina, double latitud, double longitud) {
        this.tag = tag;
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

    public String getTag() {
        return tag;
    }

    public String getCalle() {
        return calle;
    }

    public String getNumero() {
        return numero;
    }

    public String getApartamento() {
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

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public void setApartamento(String apartamento) {
        this.apartamento = apartamento;
    }

    public void setEsquina(String esquina) {
        this.esquina = esquina;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    // Representación legible de la dirección. Se usa en el PDF de comprobante y
    // en los emails. Si no hay calle se cae a las coordenadas; si tampoco hay
    // coordenadas devuelve "Sin dirección". Cuando hay tag se antepone como
    // etiqueta (ej: "Casa - Av. Italia 1234").
    @Override
    public String toString() {
        boolean tieneCalle = calle != null && !calle.isBlank();
        boolean tieneTag = tag != null && !tag.isBlank();

        if (!tieneCalle) {
            if (latitud != 0 || longitud != 0) {
                String coords = String.format("Lat %.5f, Lon %.5f", latitud, longitud);
                return tieneTag ? tag + " - " + coords : coords;
            }
            return tieneTag ? tag : "Sin dirección";
        }

        StringBuilder sb = new StringBuilder();
        if (tieneTag) {
            sb.append(tag).append(" - ");
        }
        sb.append(calle);
        if (numero != null && numero == "") {
            sb.append(" ").append(numero);
        }
        if (esquina != null && !esquina.isBlank()) {
            sb.append(" esq. ").append(esquina);
        }
        if (apartamento != null && apartamento != "") {
            sb.append(", apto ").append(apartamento);
        }
        return sb.toString();
    }
}
