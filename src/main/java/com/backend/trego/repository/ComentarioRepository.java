package com.backend.trego.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.trego.entity.Comentario;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Integer> {
    List<Comentario> findByClienteIdUsuario(Integer idCliente);

    boolean existsByClienteIdUsuarioAndRestauranteIdUsuario(Integer idCliente, Integer idRestaurante);

    List<Comentario> findByRestauranteIdUsuarioOrderByFechaCreacionDesc(Integer idRestaurante);

    @Query("""
            SELECT c FROM Comentario c
            JOIN FETCH c.cliente
            WHERE c.restaurante.idUsuario = :idRestaurante
            ORDER BY c.fechaCreacion DESC
            """)
    List<Comentario> findByRestauranteWithClienteOrderByFechaCreacionDesc(
            @Param("idRestaurante") Integer idRestaurante);

    @Query("""
            SELECT COALESCE(AVG(c.calificacion), 0)
            FROM Comentario c
            WHERE c.restaurante.idUsuario = :idRestaurante
            """)
    Double promedioCalificacionPorRestaurante(@Param("idRestaurante") Integer idRestaurante);
}
