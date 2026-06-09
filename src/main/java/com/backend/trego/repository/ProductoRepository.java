package com.backend.trego.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.trego.entity.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
	List<Producto> findByRestauranteIdUsuario(Integer idRestaurante);
	List<Producto> findByRestauranteIdUsuarioAndDisponibleTrue(Integer idRestaurante);

	@Query("SELECT p FROM Producto p WHERE p.ofertaActiva = true AND (p.oferta IS NULL OR p.oferta.fechaFin < :now OR p.oferta.fechaInicio > :now)")
	List<Producto> findProductosConOfertaInvalida(@Param("now") LocalDateTime now);
}

