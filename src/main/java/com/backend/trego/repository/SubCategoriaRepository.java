package com.backend.trego.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.trego.entity.SubCategoria;
import com.backend.trego.entity.Enums.EnumCategoriaProducto;

@Repository
public interface SubCategoriaRepository extends JpaRepository<SubCategoria, Integer> {
    Optional<SubCategoria> findByNombre(String nombre);

    List<SubCategoria> findByCategoria(EnumCategoriaProducto categoria);
}
