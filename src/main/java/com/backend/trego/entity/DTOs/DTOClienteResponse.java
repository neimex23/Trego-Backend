package com.backend.trego.entity.DTOs;

import java.util.ArrayList;
import java.util.List;

import com.backend.trego.entity.Cliente;
import com.backend.trego.entity.Enums.EnumRoles;

/**
 * DTO de salida para el Cliente.
 * Evita serializar la entidad JPA directamente (con sus relaciones LAZY,
 * herencia JOINED y referencias a Comentario/Pedido).
 */
public class DTOClienteResponse {
    private int id;
    private String nombre;
    private String email;
    private String urlImagen;
    private String telefono;
    private String uidCliente;
    private boolean habilitado;
    private EnumRoles rol;
    private List<DTDireccion> direcciones = new ArrayList<>();

    public DTOClienteResponse() {
    }

    public DTOClienteResponse(int id, String nombre, String email, String urlImagen, String telefono,
                              String uidCliente, boolean habilitado, EnumRoles rol,
                              List<DTDireccion> direcciones) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.urlImagen = urlImagen;
        this.telefono = telefono;
        this.uidCliente = uidCliente;
        this.habilitado = habilitado;
        this.rol = rol;
        if (direcciones != null) {
            this.direcciones = direcciones;
        }
    }

    /**
     * Mapeo Cliente -> DTOClienteResponse.
     */
    public static DTOClienteResponse desde(Cliente c) {
        return new DTOClienteResponse(
                c.getIdUsuario(),
                c.getNombre(),
                c.getEmail(),
                c.getUrlImagen(),
                c.getTelefono(),
                c.getUidCliente(),
                c.isHabilitado(),
                c.getRol(),
                c.getDirecciones()
        );
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getUidCliente() {
        return uidCliente;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public EnumRoles getRol() {
        return rol;
    }

    public List<DTDireccion> getDirecciones() {
        return direcciones;
    }
}