package com.backend.trego.entity.DTOs;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class DTOEstadisticas {
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    
    private List<DTOProductoSimplificado> productosMasVendidos;
    private Map<LocalDateTime, Integer> ventasPorFecha;
    private Map<LocalDateTime, Float> ingresosPorFecha;
    
    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public List<DTOProductoSimplificado> getProductosMasVendidos() {
        return productosMasVendidos;
    }

    public Map<LocalDateTime, Integer> getVentasPorFecha() {
        return ventasPorFecha;
    }

    public Map<LocalDateTime, Float> getIngresosPorFecha() {
        return ingresosPorFecha;
    }

    public DTOEstadisticas() {}

    public DTOEstadisticas(LocalDateTime fechaInicio, LocalDateTime fechaFin,
            List<DTOProductoSimplificado> productosMasVendidos, Map<LocalDateTime, Integer> ventasPorFecha,
            Map<LocalDateTime, Float> ingresosPorFecha) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.productosMasVendidos = productosMasVendidos;
        this.ventasPorFecha = ventasPorFecha;
        this.ingresosPorFecha = ingresosPorFecha;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

}
