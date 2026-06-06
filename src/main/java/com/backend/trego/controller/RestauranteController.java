package com.backend.trego.controller;

import com.backend.trego.config.AuthenticatedUser;
import com.backend.trego.entity.DTOs.DTOAbrirCerrarLocalRequest;
import com.backend.trego.entity.DTOs.DTODireccion;
import com.backend.trego.entity.DTOs.DTORestaurante;
import com.backend.trego.service.RestauranteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// Endpoints de restaurantes.
@RestController
@RequestMapping("/api/restaurantes")
@CrossOrigin("*")
@Tag(name = "Restaurantes", description = "Listado y consulta de restaurantes para el cliente")
public class RestauranteController {

    private final RestauranteService restauranteService;

    public RestauranteController(RestauranteService restauranteService) {
        this.restauranteService = restauranteService;
    }

    // CU-CLI: Listar restaurantes registrados. Si se pasa 'nombre' se filtra por
    // coincidencia parcial; si no, devuelve todos los habilitados.
    @GetMapping("/listar")
    @Operation(summary = "Listar restaurantes", description = "Devuelve los restaurantes habilitados. Acepta un filtro opcional por nombre.")
    @ApiResponse(responseCode = "200", description = "Listado de restaurantes obtenido")
    public ResponseEntity<List<DTORestaurante>> listar(@RequestParam(required = false) String nombre) {
        List<DTORestaurante> restaurantes = (nombre == null || nombre.isBlank())
                ? restauranteService.listarRestaurantes()
                : restauranteService.buscarRestaurantePorNombre(nombre);
        return ResponseEntity.ok(restaurantes);
    }

    @PostMapping("/listarXdirreccion")
    @Operation(summary = "Listar restaurantes dado una direccion", description = "Lista todos los restaurantes con cobertura segun la dirrecion provista")
    @ApiResponse(responseCode = "200", description =  "Listado de restaurantes obtenido")
    @ApiResponse(responseCode = "404", description = "Ningun restaurante obtenido")
    public ResponseEntity<List<DTORestaurante>> listarRestaurantesDirreccion (@RequestBody DTODireccion dirreccionBusqueda){
        return ResponseEntity.ok(restauranteService.listarRestaurantesZona(dirreccionBusqueda));
    }

    // Devuelve los datos del restaurante autenticado (extraídos del JWT).
    @GetMapping("/actual")
    @Operation(summary = "Obtener restaurante actual", description = "Devuelve los datos del restaurante actualmente autenticado, según el token JWT.")
    @ApiResponse(responseCode = "200", description = "Restaurante autenticado encontrado")
    @ApiResponse(responseCode = "401", description = "No autenticado o token sin rol Restaurante")
    @ApiResponse(responseCode = "404", description = "Restaurante autenticado no encontrado")
    public ResponseEntity<DTORestaurante> obtenerActual() {
        return ResponseEntity.ok(restauranteService.obtenerRestauranteActual());
    }

    // CU-CLI: Ver datos de un restaurante puntual (sin el menú).
    @GetMapping("obtenerRestaurante/{id}")
    @Operation(summary = "Obtener restaurante", description = "Devuelve los datos públicos de un restaurante por id.")
    @ApiResponse(responseCode = "200", description = "Restaurante encontrado")
    @ApiResponse(responseCode = "404", description = "Restaurante no encontrado")
    public ResponseEntity<DTORestaurante> obtener(@PathVariable String id) {
        return ResponseEntity.ok(restauranteService.obtenerRestaurante(id));
    }

    // Actualizar datos de un restaurante. Sólo se aplican los campos no nulos del
    // DTO; id y habilitado no se modifican.
    @PatchMapping("/actualizar")
    @Operation(summary = "Actualizar restaurante", description = "Actualiza los datos del restaurante actualmente logeado. Sólo se modifican los campos no nulos del DTO."+
         " No Modifica: ID, Habilitado,Direcciones ni Contraseña.")
    @ApiResponse(responseCode = "200", description = "Restaurante actualizado")
    @ApiResponse(responseCode = "404", description = "Restaurante no encontrado")
    public ResponseEntity<DTORestaurante> actualizar(@RequestBody DTORestaurante dto) {
        return ResponseEntity.ok(restauranteService.actualizarRestaurante(dto));
    }

    @PatchMapping("/altaRestaurante")
    @Operation(summary = "Dar de alta un restaurante", description = "Crea un nuevo restaurante en el sistema.")
    @ApiResponse(responseCode = "201", description = "Restaurante creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos incompletos o erroneos")
    public ResponseEntity<DTORestaurante> altaRestaurante(@RequestBody DTORestaurante dto) {
        return ResponseEntity.ok(restauranteService.altaRestaurante(dto));
    }

        // CU-RES-05: Abrir Local
    @PatchMapping("/abrirLocal")
    @Operation(summary = "Abrir el local", description = "Cambia el estado del restaurante a abierto y establece su hora de cierre. Valida que existan productos.")
    @ApiResponse(responseCode = "200", description = "El local se encuentra abierto")
    @ApiResponse(responseCode = "400", description = "Datos incompletos o erroneos")
    @ApiResponse(responseCode = "409", description = "Local ya abierto o sin productos")
    public ResponseEntity<Void> abrirLocal(@RequestBody DTOAbrirCerrarLocalRequest request) {
        restauranteService.abrirLocal(request.getHoraCierre());
        return ResponseEntity.ok().build();
    }

    // CU-RES-06: Cerrar Local
    @PatchMapping("/cerrarLocal")
    @Operation(summary = "Cerrar el local", description = "Cambia el estado del restaurante a cerrado.")
    @ApiResponse(responseCode = "200", description = "El local ya no es visible para los clientes")
    @ApiResponse(responseCode = "409", description = "El local ya se encontraba cerrado")
    @ApiResponse(responseCode = "404", description = "Restaurante no encontrado")
    public ResponseEntity<Void> cerrarLocal() {
        restauranteService.cerrarLocal();
        return ResponseEntity.ok().build();
    }

    // CU-RES-06: Cerrar Local - Sirve para actualizar la hora del cierre del local
    @PatchMapping("/actualizarCierre")
    @Operation(summary = "Actualizar hora de cierre", description = "Actualiza la hora de cierre del restaurante.")
    @ApiResponse(responseCode = "200", description = "Hora de cierre actualizada")
    @ApiResponse(responseCode = "400", description = "Datos incompletos o erróneos")
    @ApiResponse(responseCode = "404", description = "Restaurante no encontrado")
    public ResponseEntity<Void> actualizarCierre(@RequestBody DTOAbrirCerrarLocalRequest request) {
        restauranteService.actualizarHoraCierre(request.getHoraCierre());
        return ResponseEntity.ok().build();
    }

}
