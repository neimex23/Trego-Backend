package com.backend.trego.entity.DTOs;

public class DTOResolverReclamoRequest {

    private Boolean accion; // true = aceptar, false = rechazar

    // Requerido sólo cuando accion == "RECHAZAR"; se valida en el servicio.
    private String motivoRechazo;

    protected DTOResolverReclamoRequest() {
    }

    public Boolean getAccion() {
        return accion;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public void setAccion(Boolean accion) {
        this.accion = accion;
    }

    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }
}
