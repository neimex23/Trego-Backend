package com.backend.trego.controller;

import com.backend.trego.entity.DTOs.DTOPedido;
import com.backend.trego.entity.DTOs.DTOPreferenciaMP;
import com.backend.trego.entity.DTOs.DTOProducto;
import com.backend.trego.entity.DTOs.DTORestaurante;
import com.backend.trego.entity.Enums.EnumEstadoPedido;
import com.backend.trego.service.CarritoService;
import com.backend.trego.service.PedidoService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints REST para la gestión de Pedidos.
 */
@RestController
@RequestMapping("/api/pedido")
public class PedidoController {

    private final PedidoService pedidoService;
    private final CarritoService carritoService;

    public PedidoController(PedidoService pedidoService, CarritoService carritoService) {
        this.pedidoService = pedidoService;
        this.carritoService = carritoService;
    }

    @PostMapping 
    public ResponseEntity<Boolean> agregarAlCarrito(@RequestBody DTOProducto productoDTO, @RequestBody DTORestaurante restauranteDTO) {
        productoDTO.setIdRestaurante(restauranteDTO.getIdRestaurante());
        carritoService.agregarProducto(productoDTO,restauranteDTO);
        return ResponseEntity.ok(true);
    }


}
