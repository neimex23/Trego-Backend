package com.backend.trego.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.backend.trego.entity.Cliente;
import com.backend.trego.entity.Pedido;
import com.backend.trego.entity.Reclamo;
import com.backend.trego.entity.DTOs.DTOCrearReclamoRequest;
import com.backend.trego.entity.DTOs.DTOReclamo;
import com.backend.trego.entity.DTOs.DTOResolverReclamoRequest;
import com.backend.trego.entity.Enums.EnumEstadoPedido;
import com.backend.trego.entity.Enums.EnumEstadoReclamo;
import com.backend.trego.repository.PedidoRepository;;


@Service
public class ReclamoService {

    private final PedidoRepository pedidoRepository;
    private final NotificacionesService notificacionesService;
    private final CurrentUserService currentUserService;

    public ReclamoService(PedidoRepository pedidoRepository,
            NotificacionesService notificacionesService,
            CurrentUserService currentUserService) {
        this.pedidoRepository = pedidoRepository;
        this.notificacionesService = notificacionesService;
        this.currentUserService = currentUserService;
    }


    @Transactional
    public DTOReclamo crearReclamo(DTOCrearReclamoRequest request) {
        Pedido pedido = pedidoRepository.findById(request.getIdPedido())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Pedido " + request.getIdPedido() + " no encontrado"));

        if (pedido.getEstado() != EnumEstadoPedido.Entregado) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Solo se puede reclamar un pedido en estado Entregado (estado actual: "
                            + pedido.getEstado() + ")");
        }

        if (pedido.getReclamo() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El pedido ya tiene un reclamo asociado (estado: "
                            + pedido.getReclamo().getEstado() + ")");
        }

        Reclamo reclamo = new Reclamo(request.getTexto().trim(), EnumEstadoReclamo.Pendiente);
        pedido.setReclamo(reclamo);
        pedidoRepository.save(pedido);

        return toDTOReclamo(pedido);
    }


    public List<DTOReclamo> listarReclamos(String nombreCliente,
            EnumEstadoReclamo estado,
            LocalDate fechaDesde,
            LocalDate fechaHasta) {

        Integer idRestaurante = currentUserService.getCurrentId();

        LocalDateTime desde = fechaDesde != null ? fechaDesde.atStartOfDay() : null;
        LocalDateTime hasta = fechaHasta != null ? fechaHasta.atTime(23, 59, 59) : null;

        List<Pedido> pedidos = pedidoRepository.findPedidosConReclamoPorRestaurante(
                idRestaurante,
                estado,
                (nombreCliente == null || nombreCliente.isBlank()) ? null : nombreCliente,
                desde,
                hasta);

        return pedidos.stream()
                .map(this::toDTOReclamo)
                .toList();
    }


    @Transactional
    public DTOReclamo resolverReclamo(Integer idReclamo, DTOResolverReclamoRequest request) {
        Integer idRestaurante = currentUserService.getCurrentId();

        Pedido pedido = pedidoRepository
                .findPedidoByReclamoIdAndRestaurante(idReclamo, idRestaurante)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Reclamo " + idReclamo + " no encontrado o no pertenece a este restaurante"));

        Reclamo reclamo = pedido.getReclamo();

        if (reclamo.getEstado() != EnumEstadoReclamo.Pendiente) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El reclamo ya fue resuelto (estado actual: " + reclamo.getEstado() + ")");
        }

        if (request.getAccion() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Se requiere la acción a realizar (ACEPTAR o RECHAZAR)");
        }

        if (request.getAccion()){
                reclamo.setEstado(EnumEstadoReclamo.Resuelto);
        }else {
                if (request.getMotivoRechazo() == null || request.getMotivoRechazo().isBlank()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Se requiere un motivo de rechazo");
                }
                reclamo.setEstado(EnumEstadoReclamo.Rechazado);
                reclamo.setMotivoRechazo(request.getMotivoRechazo().trim());
        }

        pedidoRepository.save(pedido);

        notificacionesService.notificarResolucionReclamo(pedido);

        return toDTOReclamo(pedido);
    }

    private DTOReclamo toDTOReclamo(Pedido pedido) {
        Reclamo r = pedido.getReclamo();
        Cliente c = pedido.getCliente();
        return new DTOReclamo(
                r.getIdReclamo(),
                pedido.getIdPedido(),
                c != null ? c.getNombre() : null,
                c != null ? c.getEmail() : null,
                r.getTexto(),
                r.getEstado(),
                r.getFechaReclamo(),
                r.getMotivoRechazo());
    }
}
