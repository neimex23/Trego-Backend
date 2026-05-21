package com.backend.trego.controller;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.backend.trego.entity.DTOs.DTOAgregarAlCarritoRequest;
import com.backend.trego.entity.DTOs.DTOCarrito;
import com.backend.trego.entity.DTOs.DTOProducto;
import com.backend.trego.service.CarritoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

// Carrito del cliente autenticado. En la API todo viaja como DTOProducto;
// la cantidad y las observaciones van dentro del propio DTOProducto.
@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }


    @GetMapping
    @Operation(summary = "Obtener el carrito del usuario autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Carrito devuelto correctamente"),
            @ApiResponse(responseCode = "204", description = "El usuario no tiene carrito activo")
    })
    public ResponseEntity<DTOCarrito> obtenerCarrito() {
        DTOCarrito carrito = carritoService.obtenerCarrito();
        if (carrito == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(carrito);
    }

    @PostMapping("/productos")
    @Operation(summary = "Agregar un producto al carrito",
            description = "El front envía DTOProducto (con cantidad y observaciones) + DTORestaurante "
                    + "dentro de DTOAgregarAlCarritoRequest.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto agregado / cantidad acumulada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o producto de otro restaurante"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<DTOCarrito> agregarProducto(@RequestBody DTOAgregarAlCarritoRequest request) {
        try {
            DTOCarrito carrito = carritoService.agregarProducto(request);
            return ResponseEntity.ok(carrito);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }


    @PatchMapping("/productos")
    @Operation(summary = "Modificar la cantidad u observaciones de un producto del carrito",
            description = "Recibe el DTOProducto del carrito con la nueva cantidad. Si cantidad <= 0 se elimina la línea.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto modificado"),
            @ApiResponse(responseCode = "204", description = "Cantidad <= 0: la línea fue eliminada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado en el carrito")
    })
    public ResponseEntity<DTOProducto> modificarProducto(@RequestBody DTOProducto producto) {
        try {
            DTOProducto actualizado = carritoService.modificarProductoCarrito(producto);
            if (actualizado == null) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/total")
    @Operation(summary = "Recalcular el total del carrito a partir de las líneas actuales")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Total recalculado"),
            @ApiResponse(responseCode = "404", description = "El usuario no tiene carrito activo")
    })
    public ResponseEntity<DTOCarrito> actualizarTotal() {
        try {
            return ResponseEntity.ok(carritoService.actualizarTotal());
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/productos")
    @Operation(summary = "Eliminar un producto específico del carrito",
            description = "Recibe el DTOProducto a eliminar en el body. Devuelve el carrito actualizado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto eliminado, se devuelve el carrito actualizado"),
            @ApiResponse(responseCode = "400", description = "DTOProducto inválido"),
            @ApiResponse(responseCode = "404", description = "El producto no estaba en el carrito o el carrito no existe")
    })
    public ResponseEntity<DTOCarrito> eliminarProducto(@RequestBody DTOProducto producto) {
        try {
            DTOCarrito carrito = carritoService.eliminarProducto(producto);
            if (carrito == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "El producto no está en el carrito o el carrito no existe");
            }
            return ResponseEntity.ok(carrito);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/items")
    @Operation(summary = "Vaciar las líneas del carrito manteniendo la entidad")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Items vaciados"),
            @ApiResponse(responseCode = "204", description = "El usuario no tenía carrito")
    })
    public ResponseEntity<DTOCarrito> limpiarItems() {
        DTOCarrito carrito = carritoService.limpiarItemsCarrito();
        if (carrito == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(carrito);
    }
    
    @DeleteMapping
    @Operation(summary = "Eliminar completamente el carrito del usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Carrito eliminado (o no existía)")
    })
    public ResponseEntity<Void> limpiarCarrito() {
        carritoService.limpiarCarrito();
        return ResponseEntity.noContent().build();
    }
}
