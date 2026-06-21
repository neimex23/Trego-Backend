package com.backend.trego.service;

import com.backend.trego.entity.DTOs.DTOIngrediente;
import com.backend.trego.entity.Ingrediente;
import com.backend.trego.entity.Plato;
import com.backend.trego.entity.Producto;
import com.backend.trego.repository.IngredienteRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Resuelve y valida ingredientes que el cliente pide quitar en carrito/pedido. */
@Service
public class IngredientePedidoService {

    private final IngredienteRepository ingredienteRepository;

    public IngredientePedidoService(IngredienteRepository ingredienteRepository) {
        this.ingredienteRepository = ingredienteRepository;
    }

    public List<Ingrediente> resolverIngredientesAQuitar(List<DTOIngrediente> dtos, Producto producto) {
        if (dtos == null || dtos.isEmpty()) {
            return new ArrayList<>();
        }
        if (!(producto instanceof Plato)) {
            return new ArrayList<>();
        }
        Set<Integer> permitidos = idsPermitidosDelPlato(producto);
        if (permitidos.isEmpty()) {
            return new ArrayList<>();
        }
        List<Ingrediente> resultado = new ArrayList<>();
        Set<Integer> yaAgregados = new HashSet<>();

        for (DTOIngrediente dto : dtos) {
            if (dto == null) {
                continue;
            }
            Ingrediente ing = resolverUno(dto, producto);
            if (!permitidos.isEmpty() && !permitidos.contains(ing.getIdIngrediente())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "El ingrediente '" + ing.getNombre() + "' no pertenece al producto seleccionado");
            }
            if (yaAgregados.add(ing.getIdIngrediente())) {
                resultado.add(ing);
            }
        }
        return resultado;
    }

    private Ingrediente resolverUno(DTOIngrediente dto, Producto producto) {
        if (dto.getIdIngrediente() != null) {
            return ingredienteRepository.findById(dto.getIdIngrediente())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Ingrediente no encontrado con id: " + dto.getIdIngrediente()));
        }
        if (dto.getNombre() != null && !dto.getNombre().isBlank()) {
            String nombre = dto.getNombre().trim();
            // Se resuelve solo contra los ingredientes del plato: un ingrediente a
            // quitar válido siempre pertenece a él.
            if (producto instanceof Plato plato) {
                for (Ingrediente ing : plato.getIngredientes()) {
                    if (ing.getNombre() != null && ing.getNombre().equalsIgnoreCase(nombre)) {
                        return ing;
                    }
                }
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El ingrediente '" + nombre + "' no pertenece al producto seleccionado");
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ingrediente inválido en la petición");
    }

    private Set<Integer> idsPermitidosDelPlato(Producto producto) {
        Set<Integer> ids = new HashSet<>();
        if (producto instanceof Plato plato) {
            for (Ingrediente ing : plato.getIngredientes()) {
                ids.add(ing.getIdIngrediente());
            }
        }
        return ids;
    }
}
