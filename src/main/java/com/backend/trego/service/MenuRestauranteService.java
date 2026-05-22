package com.backend.trego.service;

import com.backend.trego.entity.DTOs.DTOProducto;
import com.backend.trego.entity.DTOs.DTORestaurante;
import com.backend.trego.exceptions.SinProductoException;
import com.backend.trego.repository.RestauranteRepository; //No se si anda
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuRestauranteService {

    private final RestauranteRepository restauranteRepo;

    public MenuRestauranteService(RestauranteRepository restauranteRepo) {
        this.restauranteRepo = restauranteRepo;
    }

    public DTORestaurante verRestaurante(Integer restauranteId, String categoria, String orden) {
        
        List<DTOProducto> productos = obtenerProductosDeBaseDeDatos(restauranteId);

        //Filtra la lista
        if (categoria != null && !categoria.isBlank()) {
            productos = aplicarFiltro(productos, categoria);
        }

        //Ordena
        if (orden != null && !orden.isBlank()) {
            productos = aplicarOrden(productos, orden);
        }

        if (productos.isEmpty()) {
            throw new SinProductoException("Restaurante sin Productos");
        }

        return new DTORestaurante(
                restauranteId,
                "Nombre del Restaurante",
                "contacto@restaurante.com",
                null, // no enviar el password al frontend por si acaso
                "099123456",
                "imagen.jpg",
                null, //direccion
                null, //categoria
                true, //esta habilitado
                true, //esta abierto
                null, // la hora apertura
                null, //la hora cierre
                productos
        );
    }

    private List<DTOProducto> aplicarFiltro(List<DTOProducto> productos, String categoria) {
        return productos.stream()
                .filter(p -> p.getCategoria().equalsIgnoreCase(categoria))
                .collect(Collectors.toList());
    }

    private List<DTOProducto> aplicarOrden(List<DTOProducto> productos, String orden) {
        if (orden.equalsIgnoreCase("precio_asc")) {
            productos.sort(Comparator.comparing(DTOProducto::getPrecio)); 
        } else if (orden.equalsIgnoreCase("precio_desc")) {
            productos.sort(Comparator.comparing(DTOProducto::getPrecio).reversed());
        }
        return productos;
    }

    private List<DTOProducto> obtenerProductosDeBaseDeDatos(Integer restauranteId) {
        return new java.util.ArrayList<>(); 
    }
}
