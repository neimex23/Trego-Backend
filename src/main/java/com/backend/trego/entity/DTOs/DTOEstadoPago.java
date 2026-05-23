package com.backend.trego.entity.DTOs;

import com.backend.trego.entity.Enums.EnumEstadoPedido;

// Estado de pago de un pedido, consultado por el front al volver del checkout
// de MercadoPago para saber si el webhook ya confirmó el pago.
public class DTOEstadoPago {

    private Integer idPedido;
    private EnumEstadoPedido estado;
    private boolean pagado;
    private String idTransaccion;
    private Float total;

    public DTOEstadoPago() {
    }

    public DTOEstadoPago(Integer idPedido, EnumEstadoPedido estado, boolean pagado,
                         String idTransaccion, Float total) {
        this.idPedido = idPedido;
        this.estado = estado;
        this.pagado = pagado;
        this.idTransaccion = idTransaccion;
        this.total = total;
    }

    public Integer getIdPedido() {
        return idPedido;
    }

    public EnumEstadoPedido getEstado() {
        return estado;
    }

    public boolean isPagado() {
        return pagado;
    }

    public String getIdTransaccion() {
        return idTransaccion;
    }

    public Float getTotal() {
        return total;
    }
}
