package com.backend.trego.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.trego.entity.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
	List<Producto> findByRestauranteIdRestaurante(Integer idRestaurante);
}

