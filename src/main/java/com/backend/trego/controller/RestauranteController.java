package com.backend.trego.controller;

import com.backend.trego.service.RestauranteService;

import org.springframework.web.bind.annotation.*;

// Endpoints de restaurantes.
@RestController
@RequestMapping("/api/restaurantes")
@CrossOrigin("*")
public class RestauranteController {

    private final RestauranteService restauranteService;

    public RestauranteController(RestauranteService restauranteService) {
        this.restauranteService = restauranteService;
    }

   
}
