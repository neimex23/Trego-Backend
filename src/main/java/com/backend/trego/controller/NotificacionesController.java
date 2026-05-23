package com.backend.trego.controller;


import com.backend.trego.service.NotificacionesService;
import org.springframework.web.bind.annotation.*;


// Endpoints para el envío de notificaciones.
@RestController
@RequestMapping("/api/notificaciones")
@CrossOrigin("*")
public class NotificacionesController {

    private final NotificacionesService notificacionesService;

    public NotificacionesController(NotificacionesService notificacionesService) {
        this.notificacionesService = notificacionesService;
    }

}
