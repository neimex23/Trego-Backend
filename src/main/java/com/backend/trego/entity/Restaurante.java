package com.backend.trego.entity;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.backend.trego.entity.DTOs.DTODireccion;
import com.backend.trego.entity.Enums.EnumCategoriaRestaurante;
import com.backend.trego.entity.Enums.EnumRoles;

import jakarta.persistence.CascadeType;
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
    private DTODireccion direccion;

    private String descripcion;
    private float calificacionProm;
    private boolean habilitado = false;
    private boolean abierto = false;
    private String fotoPortada;

    @Enumerated(EnumType.STRING)
    private EnumCategoriaRestaurante categoria;

    private LocalTime horaApertura;
    private LocalTime horaCierre;

    private Integer radioEntrega = 10;

    @OneToMany(mappedBy = "restaurante", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Producto> productos = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "restaurante_id")
    private List<Ingrediente> ingredientesDisponibles = new ArrayList<>();

    @OneToMany(mappedBy = "restaurante", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> comentarios = new ArrayList<>();

    protected Restaurante() {
    }

    public Restaurante(String email, String password) {
        super(email, EnumRoles.Restaurante);
        this.password = password;
    }

    public Restaurante(String nombre, String email, String urlImagen, String password, String rut,
            String telefono, DTODireccion direccion, String descripcion, float calificacionProm,
            EnumCategoriaRestaurante categoria, LocalTime apertura, LocalTime cierre, Integer radioEntrega) {
        super(nombre, email, urlImagen, EnumRoles.Restaurante);
        this.password = password;
        this.rut = rut;
        this.telefono = telefono;
        this.direccion = direccion;
        this.descripcion = descripcion;
        this.calificacionProm = calificacionProm;
        this.categoria = categoria;
        this.horaApertura = apertura;
        this.horaCierre = cierre;
        this.radioEntrega = radioEntrega;
    }

    public void addProducto(Producto producto) {
        this.productos.add(producto);
        producto.setRestaurante(this);
    }

    public List<Ingrediente> getIngredientesDisponibles() {
        return ingredientesDisponibles;
    }

    public void setIngredientesDisponibles(List<Ingrediente> ingredientesDisponibles) {
        this.ingredientesDisponibles = ingredientesDisponibles;
    }

    public void addIngredienteDisponible(Ingrediente ingrediente) {
        this.ingredientesDisponibles.add(ingrediente);
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRut() { return rut; }
    public void setRut(String rut) { this.rut = rut; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public DTODireccion getDireccion() { return direccion; }
    public void setDireccion(DTODireccion direccion) { this.direccion = direccion; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public float getCalificacionProm() { return calificacionProm; }
    public void setCalificacionProm(float calificacionProm) { this.calificacionProm = calificacionProm; }
    public boolean isHabilitado() { return habilitado; }
    public void setHabilitado(boolean habilitado) { this.habilitado = habilitado; }
    public boolean getAbierto() { return abierto; }
    public void setAbierto(boolean abierto) { this.abierto = abierto; }
    public String getFotoPortada() { return fotoPortada; }
    public void setFotoPortada(String fotoPortada) { this.fotoPortada = fotoPortada; }
    public EnumCategoriaRestaurante getCategoria() { return categoria; }
    public void setCategoria(EnumCategoriaRestaurante categoria) { this.categoria = categoria; }
    public LocalTime getApertura() { return horaApertura; }
    public void setApertura(LocalTime apertura) { this.horaApertura = apertura; };
    public LocalTime getCierre() { return horaCierre; }
    public void setCierre(LocalTime cierre) { this.horaCierre = cierre; };
    public List<Comentario> getComentarios() { return comentarios;}
    public void setComentarios(List<Comentario> comentarios) { this.comentarios = comentarios; }
    public void addComentario(Comentario comentario) {
        this.comentarios.add(comentario);
        calcularCalificacionProm();
    }

    public void calcularCalificacionProm() {
        if (comentarios.isEmpty()) {
            this.calificacionProm = 0;
        } else {
            float suma = 0;
            for (Comentario c : comentarios) {
                suma += c.getCalificacion();
            }
            this.calificacionProm = suma / comentarios.size();
        }
    }


    public void setHorario(LocalTime apertura, LocalTime cierre) {
        if (apertura != null) this.horaApertura = apertura;
        if (cierre != null) this.horaCierre = cierre;
    }

    public Integer getRadioEntrega() { return radioEntrega; }
    public void setRadioEntrega(Integer radioEntrega) { this.radioEntrega = radioEntrega; }

    public boolean existeIngrediente(String nombre) {
        return ingredientesDisponibles.stream()
            .anyMatch(i -> i.getNombre().equalsIgnoreCase(nombre));
    }
}
