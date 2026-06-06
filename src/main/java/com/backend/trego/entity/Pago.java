package com.backend.trego.entity;

import java.time.LocalDateTime;

import com.backend.trego.entity.Enums.EnumMetodoDePago;
import com.backend.trego.entity.Enums.EnumMoneda;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPago;

    private LocalDateTime fechaPago;
    private float monto;

    @Enumerated(EnumType.STRING)
    private EnumMetodoDePago metodoDePago = EnumMetodoDePago.MercadoPago;

    @Enumerated(EnumType.STRING)
    private EnumMoneda moneda = EnumMoneda.UYU;

    private String idTransaccion;
    private String nroUltimDigTarjeta;

    protected Pago() {
    }

    public Pago(LocalDateTime fechaPago, float monto, String idTransaccion, String nroUltimDigTarjeta) {
        this.fechaPago = fechaPago;
        this.monto = monto;
        this.idTransaccion = idTransaccion;
        this.nroUltimDigTarjeta = nroUltimDigTarjeta;
    }

    public Integer getIdPago() {
        return idPago;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public float getMonto() {
        return monto;
    }

    public void setMonto(float monto) {
        this.monto = monto;
    }

    public EnumMetodoDePago getMetodoDePago() {
        return metodoDePago;
    }

    public void setMetodoDePago(EnumMetodoDePago metodoDePago) {
        this.metodoDePago = metodoDePago;
    }

    public EnumMoneda getMoneda() {
        return moneda;
    }

    public void setMoneda(EnumMoneda moneda) {
        this.moneda = moneda;
    }

    public String getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(String idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public String getNroUltimDigTarjeta() {
        return nroUltimDigTarjeta;
    }

    public void setNroUltimDigTarjeta(String nroUltimDigTarjeta) {
        this.nroUltimDigTarjeta = nroUltimDigTarjeta;
    }
}
