package com.backend.trego.controller;

import com.backend.trego.entity.DTOs.DTOPedido;
import com.backend.trego.entity.DTOs.DTORestaurante;
import com.backend.trego.service.NotificacionesService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// Endpoints para el envío de notificaciones.
@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionesController {

    private final NotificacionesService notificacionesService;

    public NotificacionesController(NotificacionesService notificacionesService) {
        this.notificacionesService = notificacionesService;
    }

}
