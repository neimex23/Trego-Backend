package com.backend.trego.entity.Enums;

public enum EnumEstadoPedido {
    Solicitado,
    Pagado,
    PagoRechazado,
    EnPreparacion,
    EnCamino,
    Entregado,
    Cancelado,
    Reembolsado;

    public boolean permiteReclamo() {
        return this != Pagado && this != Reembolsado && this != Cancelado;
    }
}
