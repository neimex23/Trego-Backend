package com.backend.trego.service;

import com.backend.trego.entity.DTOs.DTOCarrito;
import com.backend.trego.entity.DTOs.DTODireccion;
import com.backend.trego.entity.DTOs.DTOPedido;
import com.backend.trego.entity.DTOs.DTOPreferenciaMP;
import com.backend.trego.entity.DTOs.DTORestaurante;
import com.backend.trego.entity.DTOs.DTOUsuario;
import com.backend.trego.entity.Enums.EnumEstadoPedido;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

// Ciclo de vida de los pedidos: creación, confirmación, estados y tiempos.
@Service
public class PedidoService {

    public PedidoService() {
        // TODO: inyectar PedidoRepository, RestauranteService, NotificacionesService, PagoService
    }

    public List<DTOPedido> listarPedidosConfirmados(String restauranteId) {
        // TODO: implementar
        return List.of();
    }

    public DTOPedido actualizarEstadoPedido(DTOPedido pedidoDTO, EnumEstadoPedido estado) {
        // TODO: implementar
        return null;
    }

    public DTOPedido crearPedido(String uid, DTOCarrito carritoDTO, Date fecha, DTORestaurante restauranteDTO) {
        // TODO: implementar
        return null;
    }

    public DTOPedido crearPedido(DTOUsuario usuarioDTO, DTOCarrito carritoDTO, DTODireccion direccionDTO,
                                 Date fecha, DTORestaurante restauranteDTO) {
        // TODO: implementar
        return null;
    }

    public DTORestaurante obtenerRestaurante(String idRestaurante) {
        // TODO: implementar
        return null;
    }

    public Date obtenerHoraActual() {
        // TODO: implementar
        return new Date();
    }

    public boolean verificarRestauranteAbierto(String restauranteID) {
        // TODO: implementar
        return false;
    }

    public Integer calcularTiempoPreparacion() {
        // TODO: implementar
        return 0;
    }

    public DTOPedido actualizarPedido(DTOPedido pedidoDTO) {
        // TODO: implementar
        return null;
    }

    public void guardarPedido(DTOPedido pedidoDTO) {
        // TODO: implementar
    }

    public List<DTOPedido> listarPedidosPendientes(String restauranteId) {
        // TODO: implementar
        return List.of();
    }

    public void confirmarPedido(DTOCarrito carritoDTO, DTODireccion direccionDTO,
                                String restauranteId, DTOUsuario usuarioDTO) {
        // TODO: implementar
    }

    public DTOPedido actualizarHoraEntrega() {
        // TODO: implementar
        return null;
    }

    public void pagoConfirmado(DTOPreferenciaMP preferenciaDTO) {
        // TODO: implementar
    }
}
