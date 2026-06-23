package com.backend.trego.entity.Enums;

public enum EnumEstadoPedido {
    Solicitado,
    Pagado,
    PagoRechazado,
    EnPreparacion,
    EnCamino,
    Entregado,
    Reembolsado;

    public boolean permiteReclamo() {
        return this != Pagado && this != Reembolsado;
    }
}
