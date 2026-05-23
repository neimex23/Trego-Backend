package com.backend.trego.controller;

import com.backend.trego.service.ProductosService;

import org.springframework.web.bind.annotation.*;

// Endpoints del catálogo de productos e ingredientes.
@RestController
@RequestMapping("/api/productos")
@CrossOrigin("*")
public class ProductosController {

    private final ProductosService productosService;

    public ProductosController(ProductosService productosService) {
        this.productosService = productosService;
    }

}
