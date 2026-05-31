package com.backend.trego.entity.DTOs;

import com.backend.trego.entity.Enums.EnumCategoriaRestaurante;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

// Representa un restaurante.
public class DTORestaurante {

    private Integer idRestaurante;
    private String nombre;
    private String email;
    private String password;
    private String rut;
    private String telefono;
    private String fotoPerfil;
    private String fotoPortada;
    private DTODireccion direccion;
    private String descripcion;
    private EnumCategoriaRestaurante categoria;
    private Float calificacionProm;
    private Integer radioEntrega = 10;
    private Boolean habilitado = false;
    private Boolean abierto = false;
    private LocalTime horaApertura;
    private LocalTime horaCierre;
    private List<DTOProducto> productos = new ArrayList<>();
    private List<DTOIngrediente> ingredientesDisponibles = new ArrayList<>();

    public DTORestaurante() {
    }

    // Constructor completo: usado al exponer todos los datos del restaurante.
    public DTORestaurante(Integer idRestaurante, String nombre, String email, String password, String rut,
            String telefono, String fotoPortada, String fotoPerfil, DTODireccion direccion, String descripcion,
            EnumCategoriaRestaurante categoria, Float calificacionProm, Integer radioEntrega, Boolean habilitado,
            Boolean abierto, LocalTime horaApertura, LocalTime horaCierre, List<DTOProducto> productos) {
        this.idRestaurante = idRestaurante;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.rut = rut;
        this.telefono = telefono;
        this.fotoPortada = fotoPortada;
        this.direccion = direccion;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.calificacionProm = calificacionProm;
        this.radioEntrega = radioEntrega;
        this.habilitado = habilitado;
        this.abierto = abierto;
        this.horaApertura = horaApertura;
        this.horaCierre = horaCierre;
        this.productos = productos != null ? productos : new ArrayList<>();
    }


    public DTORestaurante(Integer idRestaurante, String nombre, String email, String telefono,
            String fotoPortada, String fotoPerfil, DTODireccion direccion, String descripcion,
            EnumCategoriaRestaurante categoria, Float calificacionProm,
            Integer radioEntrega, Boolean habilitado, Boolean abierto, LocalTime horaApertura, LocalTime horaCierre) {
        this.idRestaurante = idRestaurante;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.fotoPortada = fotoPortada;
        this.fotoPerfil = fotoPerfil;
        this.direccion = direccion;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.calificacionProm = calificacionProm;
        this.radioEntrega = radioEntrega;
        this.habilitado = habilitado;
        this.abierto = abierto;
        this.horaApertura = horaApertura;
        this.horaCierre = horaCierre;
    }

    public Integer getIdRestaurante() { return idRestaurante; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRut() { return rut; }
    public String getTelefono() { return telefono; }
    public String getFotoPortada() { return fotoPortada; }
    public DTODireccion getDireccion() { return direccion; }
    public String getDescripcion() { return descripcion; }
    public EnumCategoriaRestaurante getCategoria() { return categoria; }
    public Float getCalificacionProm() { return calificacionProm; }
    public Integer getRadioEntrega() { return radioEntrega; }
    public void setRadioEntrega(Integer radioEntrega) { this.radioEntrega = radioEntrega; }
    public Boolean getHabilitado() { return habilitado; }
    public Boolean getAbierto() { return abierto; }
    public LocalTime getHoraApertura() { return horaApertura; }
    public LocalTime getHoraCierre() { return horaCierre; }
    public List<DTOProducto> getProductos() { return productos; }
    public String getFotoPerfil() { return fotoPerfil; }
    public List<DTOIngrediente> getIngredientesDisponibles() { return ingredientesDisponibles; }
    public void setIngredientesDisponibles(List<DTOIngrediente> ingredientesDisponibles) {
        this.ingredientesDisponibles = ingredientesDisponibles != null ? ingredientesDisponibles : new ArrayList<>();
    }
}
