package com.backend.trego.entity;

import java.util.ArrayList;
import java.util.List;

import com.backend.trego.entity.DTOs.DTDireccion;
import com.backend.trego.entity.Enums.EnumRoles;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

@Entity
public class Cliente extends Usuario {
    private String uidCliente;
    private String telefono;

    @ElementCollection
    private List<DTDireccion> direcciones = new ArrayList<>();

    private boolean habilitado = true;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> comentarios = new ArrayList<>();

    protected Cliente(String nombre, String email, String foto, EnumRoles cliente, String uid, Object object, List<DTDireccion> direccionesVaciasGoogle) {
    }

    public Cliente() {
    }

    // Para los flujos de Firebase (Google/SMS), con uid.
    public Cliente(String nombre, String email, String urlImagen, EnumRoles rol, String firebaseUid,
            String telefono, List<DTDireccion> direcciones) {
        super(nombre, email, urlImagen, rol, firebaseUid);
        this.telefono = telefono;
        if (direcciones != null) {
            this.direcciones = direcciones;
        }
    }

    // Para el registro clásico por formulario, sin uid.
    public Cliente(String nombre, String email, String urlImagen, EnumRoles rol,
            String telefono, List<DTDireccion> direcciones) {
        super(nombre, email, urlImagen, rol, null);
        this.telefono = telefono;
        if (direcciones != null) {
            this.direcciones = direcciones;
        }
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public void addComentario(Comentario comentario) {
        comentario.setCliente(this);
        this.comentarios.add(comentario);
    }

    public void addDireccion(DTDireccion direccion) {
        this.direcciones.add(direccion);
    }

    public String getUidCliente() {
        return uidCliente;
    }

    public void setUidCliente(String uidCliente) {
        this.uidCliente = uidCliente;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public List<DTDireccion> getDirecciones() {
        return direcciones;
    }

    public void setDirecciones(List<DTDireccion> direcciones) {
        this.direcciones = direcciones;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }
}
