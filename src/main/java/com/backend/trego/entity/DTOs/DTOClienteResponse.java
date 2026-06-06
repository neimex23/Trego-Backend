package com.backend.trego.entity.DTOs;

import java.util.ArrayList;
import java.util.List;

import com.backend.trego.entity.Cliente;
import com.backend.trego.entity.Enums.EnumRoles;

// DTO de salida del Cliente. Evita serializar la entidad JPA directa, que
// arrastra relaciones LAZY, herencia JOINED y referencias a Comentario/Pedido.
public class DTOClienteResponse {
    private Integer id;
    private String nombre;
    private String email;
    private String fotoPerfil;
    private String telefono;
    private String uidCliente;
    private boolean habilitado;
    private EnumRoles rol;
    private List<DTODireccion> direcciones = new ArrayList<>();

    public DTOClienteResponse() {
    }

    public DTOClienteResponse(Integer id, String nombre, String email, String fotoPerfil, String telefono,
                              String uidCliente, boolean habilitado, EnumRoles rol,
                              List<DTODireccion> direcciones) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.fotoPerfil = fotoPerfil;
        this.telefono = telefono;
        this.uidCliente = uidCliente;
        this.habilitado = habilitado;
        this.rol = rol;
        if (direcciones != null) {
            this.direcciones = direcciones;
        }
    }

    public static DTOClienteResponse desde(Cliente c) {
        return new DTOClienteResponse(
                c.getIdUsuario(),
                c.getNombre(),
                c.getEmail(),
                c.getFotoPerfil(),
                c.getTelefono(),
                c.getUidCliente(),
                c.isHabilitado(),
                c.getRol(),
                c.getDirecciones()
        );
    }

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
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

    public List<DTODireccion> getDirecciones() {
        return direcciones;
    }
}