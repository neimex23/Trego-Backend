package com.backend.trego.config;

import com.backend.trego.entity.DTOs.DTOUsuario;
import com.backend.trego.entity.Enums.EnumRoles;
import com.backend.trego.service.UsuarioService;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultAdminConf {

    private final UsuarioService usuarioService;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    public DefaultAdminConf(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostConstruct
    public void init() {
        crearAdminDefault(adminEmail, adminPassword);
    }

    private void crearAdminDefault(String email, String password) {

        if (!usuarioService.existeUsuario(email)) {

            DTOUsuario admin = new DTOUsuario(
                    0,
                    null,
                    null,
                    email,
                    password,
                    null,
                    null,
                    EnumRoles.Administrador
            );

            usuarioService.altaUsuario(admin);

            System.out.println(">>> Admin por defecto creado: " + email);
        }
    }
}