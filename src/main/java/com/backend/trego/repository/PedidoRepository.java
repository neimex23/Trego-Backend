package com.backend.trego.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.trego.entity.Pedido;
import com.backend.trego.entity.Enums.EnumEstadoPedido;
import com.backend.trego.entity.Enums.EnumEstadoReclamo;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    List<Pedido> findByClienteIdUsuario(Integer idCliente);

    @Query("""
            SELECT p FROM Pedido p
            LEFT JOIN FETCH p.restaurante
            WHERE p.cliente.idUsuario = :idCliente
            ORDER BY p.fechaCreacion DESC
            """)
    List<Pedido> findHistorialByClienteIdUsuario(@Param("idCliente") Integer idCliente);

    @Query("""
            SELECT DISTINCT p FROM Pedido p
            LEFT JOIN FETCH p.restaurante
            LEFT JOIN FETCH p.productos
            WHERE p.cliente.idUsuario = :idCliente
            AND p.estado IN :estadosPermitidos
            ORDER BY p.fechaCreacion DESC
            """)
    List<Pedido> findPedidosActualesByCliente(
            @Param("idCliente") Integer idCliente, 
            @Param("estadosPermitidos") List<EnumEstadoPedido> estadosPermitidos
    );

    List<Pedido> findByRestauranteIdUsuario(Integer idRestaurante);

    List<Pedido> findByEstado(EnumEstadoPedido estado);

    List<Pedido> findByFechaExpiracionNotNullAndFechaExpiracionBefore(LocalDateTime instante);

    List<Pedido> findByEstadoAndFechaExpiracionNotNullAndFechaExpiracionBefore(
            EnumEstadoPedido estado, LocalDateTime instante);

    List<Pedido> findByRestauranteIdUsuarioAndEstado(Integer idRestaurante, EnumEstadoPedido estado);

    @Query("""
            SELECT COUNT(p) FROM Pedido p
            WHERE p.cliente.idUsuario = :idCliente
            AND p.restaurante.idUsuario = :idRestaurante
            """)
    long countPedidosPorClienteYRestaurante(
            @Param("idCliente") Integer idCliente,
            @Param("idRestaurante") Integer idRestaurante);


    @Query("""
            SELECT p FROM Pedido p
            JOIN FETCH p.reclamo r
            JOIN FETCH p.cliente c
            WHERE p.restaurante.idUsuario = :idRestaurante
            AND (:estado IS NULL OR r.estado = :estado)
            AND (:nombreCliente IS NULL
                 OR LOWER(c.nombre) LIKE LOWER(CONCAT('%', :nombreCliente, '%')))
            AND (:fechaDesde IS NULL OR r.fechaReclamo >= :fechaDesde)
            AND (:fechaHasta IS NULL OR r.fechaReclamo <= :fechaHasta)
            ORDER BY r.fechaReclamo DESC
            """)
    List<Pedido> findPedidosConReclamoPorRestaurante(
            @Param("idRestaurante") Integer idRestaurante,
            @Param("estado") EnumEstadoReclamo estado,
            @Param("nombreCliente") String nombreCliente,
            @Param("fechaDesde") LocalDateTime fechaDesde,
            @Param("fechaHasta") LocalDateTime fechaHasta);

    @Query("""
            SELECT p FROM Pedido p
            JOIN FETCH p.reclamo r
            JOIN FETCH p.cliente c
            WHERE r.idReclamo = :idReclamo
            AND p.restaurante.idUsuario = :idRestaurante
            """)
    Optional<Pedido> findPedidoByReclamoIdAndRestaurante(
            @Param("idReclamo") Integer idReclamo,
            @Param("idRestaurante") Integer idRestaurante);
}
