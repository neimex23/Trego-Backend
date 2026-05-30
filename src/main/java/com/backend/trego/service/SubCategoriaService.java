package com.backend.trego.service;

import com.backend.trego.entity.SubCategoria;
import com.backend.trego.entity.DTOs.DTOSubCategoria;
import com.backend.trego.entity.Enums.EnumCategoriaProducto;
import com.backend.trego.repository.SubCategoriaRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

// Catálogo global de subcategorías. No depende del restaurante: las subcategorías
// son compartidas entre todos los restaurantes (ej: "Pizzas", "Hamburguesas").
@Service
public class SubCategoriaService {

    private final SubCategoriaRepository subCategoriaRepository;

    public SubCategoriaService(SubCategoriaRepository subCategoriaRepository) {
        this.subCategoriaRepository = subCategoriaRepository;
    }

    public List<DTOSubCategoria> listar() {
        return subCategoriaRepository.findAll().stream()
                .map(DTOSubCategoria::desde)
                .collect(Collectors.toList());
    }

    public List<DTOSubCategoria> listarPorCategoria(EnumCategoriaProducto categoria) {
        return subCategoriaRepository.findByCategoria(categoria).stream()
                .map(DTOSubCategoria::desde)
                .collect(Collectors.toList());
    }

    public DTOSubCategoria crear(DTOSubCategoria dto) {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El nombre de la subcategoría es obligatorio");
        }
        if (dto.getCategoria() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La categoría madre es obligatoria");
        }
        subCategoriaRepository.findByNombre(dto.getNombre()).ifPresent(s -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe una subcategoría con nombre: " + dto.getNombre());
        });
        SubCategoria sub = new SubCategoria(dto.getNombre(), dto.getCategoria(), dto.getUrlImagen());
        return DTOSubCategoria.desde(subCategoriaRepository.save(sub));
    }

    // Carga la entidad o devuelve 404. Pensado para uso interno desde otros services.
    public SubCategoria buscarPorId(Integer idSubCategoria) {
        return subCategoriaRepository.findById(idSubCategoria)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "SubCategoria no encontrada con id: " + idSubCategoria));
    }
}
