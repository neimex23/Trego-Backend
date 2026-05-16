package com.backend.trego.entity;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.backend.trego.entity.DTOs.DTDireccion;
import com.backend.trego.entity.Enums.EnumCategoriaRestaurante;
import com.backend.trego.entity.Enums.EnumRoles;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

@Entity
public class Restaurante extends Usuario {
    private String password;
    private String rut;
    private String telefono;

    @Embedded
    private DTDireccion direccion;

    private String descripcion;
    private float calificacionProm;
    private boolean habilitado = false;
    private String fotoPortada;

    @Enumerated(EnumType.STRING)
    private EnumCategoriaRestaurante categoria;

    /** [0] = apertura, [1] = cierre */
    @ElementCollection
    private List<LocalTime> horarioAtencion = new ArrayList<>();

    private int radioEntrega;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "restaurante_id")
    private List<Producto> productos = new ArrayList<>();

    protected Restaurante() {
    }

    public Restaurante(String nombre, String email, String urlImagen, String password, String rut,
            String telefono, DTDireccion direccion, String descripcion, float calificacionProm,
            EnumCategoriaRestaurante categoria, LocalTime apertura, LocalTime cierre, int radioEntrega) {
        super(nombre, email, urlImagen, EnumRoles.Restaurante);
        this.password = password;
        this.rut = rut;
        this.telefono = telefono;
        this.direccion = direccion;
        this.descripcion = descripcion;
        this.calificacionProm = calificacionProm;
        this.categoria = categoria;
        this.horarioAtencion.add(apertura);
        this.horarioAtencion.add(cierre);
        this.radioEntrega = radioEntrega;
    }

    public void addProducto(Producto producto) {
        this.productos.add(producto);
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public DTDireccion getDireccion() {
        return direccion;
    }

    public void setDireccion(DTDireccion direccion) {
        this.direccion = direccion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public float getCalificacionProm() {
        return calificacionProm;
    }

    public void setCalificacionProm(float calificacionProm) {
        this.calificacionProm = calificacionProm;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }

    public String getFotoPortada() {
        return fotoPortada;
    }

    public void setFotoPortada(String fotoPortada) {
        this.fotoPortada = fotoPortada;
    }

    public EnumCategoriaRestaurante getCategoria() {
        return categoria;
    }

    public void setCategoria(EnumCategoriaRestaurante categoria) {
        this.categoria = categoria;
    }

    public List<LocalTime> getHorarioAtencion() {
        return horarioAtencion;
    }

    public LocalTime getApertura() {
        return horarioAtencion.isEmpty() ? null : horarioAtencion.get(0);
    }

    public LocalTime getCierre() {
        return horarioAtencion.size() < 2 ? null : horarioAtencion.get(1);
    }

    public void setHorario(LocalTime apertura, LocalTime cierre) {
        this.horarioAtencion.clear();
        this.horarioAtencion.add(apertura);
        this.horarioAtencion.add(cierre);
    }

    public int getRadioEntrega() {
        return radioEntrega;
    }

    public void setRadioEntrega(int radioEntrega) {
        this.radioEntrega = radioEntrega;
    }
}
