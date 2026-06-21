package com.backend.trego.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.trego.entity.Ingrediente;

@Repository
public interface IngredienteRepository extends JpaRepository<Ingrediente, Integer> {

    @Query(value = "SELECT COUNT(*) FROM ingrediente WHERE id_ingrediente = :ingredienteId AND restaurante_id = :restauranteId", nativeQuery = true)
    long countByIdAndRestauranteId(
            @Param("ingredienteId") Integer ingredienteId,
            @Param("restauranteId") Integer restauranteId);
}
