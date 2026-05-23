package com.backend.trego.controller;

import com.backend.trego.service.RestauranteService;

import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

// Endpoints de restaurantes. Ya se sabe 
@RestController
@RequestMapping("/api/restaurantes")
@CrossOrigin("*")
public class RestauranteController {

    private final RestauranteService restauranteService;
	private final MenuRestauranteService menuRestauranteService;

    public RestauranteController(RestauranteService restauranteService, MenuRestauranteService menuRestauranteService) {
        this.restauranteService = restauranteService;
        this.menuRestauranteService = menuRestauranteService;
    }
//endpoint ver menu
	@GetMapping("/{restauranteId}/verMenu") //Es para que responda solo a peticiones get restauranteId funciona como variable guarda el Id del resturante que selecciono el cliente para que no se manden menus de otros restaurantes
	public ResponseEntity<?> verMenu(
        @PathVariable Integer restauranteId,
        @RequestParam(required = false) String categoria,
        @RequestParam(required = false) String orden) {

	    try {
	        return ResponseEntity.ok(menuRestauranteService.verRestaurante(restauranteId, categoria, orden));
	    } catch (SinProductoException e) {
	        // Ojo el diagrama exige 200 OK incluso si no hay productos
	        return ResponseEntity.ok(Map.of("mensaje", e.getMessage()));
	    }
	}
   
}
