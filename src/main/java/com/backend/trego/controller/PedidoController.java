package com.backend.trego.controller;

import com.backend.trego.entity.DTOs.DTOPedido;
import com.backend.trego.entity.Enums.EnumEstadoPedido;
import com.backend.trego.service.PedidoService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints REST para la gestión de Pedidos.
 */
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }


}
