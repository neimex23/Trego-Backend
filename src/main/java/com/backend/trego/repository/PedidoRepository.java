package com.backend.trego.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.trego.entity.Pedido;
import com.backend.trego.entity.Enums.EnumEstadoPedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    List<Pedido> findByClienteIdUsuario(int idCliente);

    List<Pedido> findByRestauranteIdUsuario(int idRestaurante);

    List<Pedido> findByEstado(EnumEstadoPedido estado);
}
