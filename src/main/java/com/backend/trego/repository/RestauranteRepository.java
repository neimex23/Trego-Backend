package com.backend.trego.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.trego.entity.Restaurante;
import com.backend.trego.entity.Enums.EnumCategoriaRestaurante;

@Repository
public interface RestauranteRepository extends JpaRepository<Restaurante, Integer> {
    Optional<Restaurante> findByEmail(String email);

    Optional<Restaurante> findByRut(String rut);

    List<Restaurante> findByHabilitadoTrue();

    List<Restaurante> findByCategoria(EnumCategoriaRestaurante categoria);
}
