package com.backend.trego.entity.DTOs;

// Respuesta con una preferencia de pago de MercadoPago.
public class DTOPreferenciaMP {

    private String preferenceId;
    private String initPoint;
    private String sandboxInitPoint;
    private Integer idPedido;

    public DTOPreferenciaMP() {
    }

    public DTOPreferenciaMP(String preferenceId, String initPoint, String sandboxInitPoint, Integer idPedido) {
        this.preferenceId = preferenceId;
        this.initPoint = initPoint;
        this.sandboxInitPoint = sandboxInitPoint;
        this.idPedido = idPedido;
    }

    public String getPreferenceId() {
        return preferenceId;
    }

    public String getInitPoint() {
        return initPoint;
    }

    public String getSandboxInitPoint() {
        return sandboxInitPoint;
    }

    public Integer getIdPedido() {
        return idPedido;
    }
}
