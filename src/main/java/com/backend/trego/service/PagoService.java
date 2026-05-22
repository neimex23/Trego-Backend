package com.backend.trego.service;

import com.backend.trego.entity.MPResponse;
import com.backend.trego.entity.Pago;
import com.backend.trego.entity.Pedido;
import com.backend.trego.entity.Producto;
import com.backend.trego.entity.DTOs.DTOEstadoPago;
import com.backend.trego.entity.DTOs.DTOPedido;
import com.backend.trego.entity.DTOs.DTOPreferenciaMP;
import com.backend.trego.entity.Enums.EnumEstadoPedido;
import com.backend.trego.exception.PagoRechazadoException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadopago.resources.payment.Payment;
import com.backend.trego.entity.ProductoPedido;
import com.backend.trego.entity.DTOs.DTOPedido;
import com.backend.trego.entity.DTOs.DTOPreferenciaMP;
import com.backend.trego.entity.DTOs.DTOProducto;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

// Integración con MercadoPago: crea preferencias de pago y procesa los webhooks.
// La construcción de la preferencia se delega en MercadoPagoService, que ya
// tiene configurado el PreferenceClient.
@Service
public class PagoService {

    private final MercadoPagoService mercadoPagoService;
    private final OrdenesService ordenesService;
    private final NotificacionesService notificacionesService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PagoService(MercadoPagoService mercadoPagoService,
                       OrdenesService ordenesService,
                       NotificacionesService notificacionesService) {
        this.mercadoPagoService = mercadoPagoService;
        this.ordenesService = ordenesService;
        this.notificacionesService = notificacionesService;
    }

    // Recibe el pedido ya creado (con id), recupera la entidad y genera la
    // preferencia en MercadoPago. Devuelve la preferencia con la URL de checkout.
    public DTOPreferenciaMP crearPreferencia(DTOPedido pedidoDTO) {
        if (pedidoDTO == null || pedidoDTO.getIdPedido() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pedido inválido para generar la preferencia");
        }

        Pedido pedido = ordenesService.obtenerOFallar(pedidoDTO.getIdPedido());

        MPResponse mpResponse = mercadoPagoService.crearOrden(pedido);

        return new DTOPreferenciaMP(
                mpResponse.getOrderid(),
                mpResponse.getInit_point(),
                mpResponse.getSandbox_init_point(),
                pedido.getIdPedido());
    }

    public void procesarWebHook(String payload) {
        Long paymentId = extraerPaymentId(payload);
        if (paymentId == null) {
            // Notificaciones que no son de pago (merchant_order, etc.): se ignoran.
            return;
        }

        Payment pago = mercadoPagoService.consultarPago(paymentId);
        Integer idPedido = resolverIdPedido(pago);
        Pedido pedido = ordenesService.obtenerOFallar(idPedido);

        String estado = pago.getStatus() == null ? "" : pago.getStatus().toLowerCase();
        switch (estado) {
            case "approved" -> confirmarPago(pedido, pago);
            case "rejected", "cancelled" -> registrarRechazo(pedido, pago);
            default -> {
                // pending / in_process / authorized: se deja el pedido a la espera.
                System.out.println("Pago " + paymentId + " del pedido " + idPedido
                        + " en estado '" + estado + "'. Pedido sin cambios.");
            }
        }
    }

    // Rama de pago aprobado: guarda los datos del pago, marca el pedido como
    // Pagado, fija una hora de entrega estimada y dispara la notificación al
    // cliente con el comprobante en PDF.
    private void confirmarPago(Pedido pedido, Payment pago) {
        Pago entidadPago = pedido.getPago();
        if (entidadPago == null) {
            entidadPago = new Pago(LocalDateTime.now(), pedido.getTotal(), null, null);
            pedido.setPago(entidadPago);
        }
        entidadPago.setFechaPago(LocalDateTime.now());
        entidadPago.setIdTransaccion(String.valueOf(pago.getId()));
        if (pago.getTransactionAmount() != null) {
            entidadPago.setMonto(pago.getTransactionAmount().floatValue());
        }
        if (pago.getCard() != null && pago.getCard().getLastFourDigits() != null) {
            entidadPago.setNroUltimDigTarjeta(pago.getCard().getLastFourDigits());
        }

        pedido.setEstado(EnumEstadoPedido.Pagado);

        ordenesService.guardar(pedido);

        enviarNotificacionConfirmacion(pedido);
        System.out.println("Pago aprobado para el pedido " + pedido.getIdPedido()
                + " (transacción " + pago.getId() + "). Pedido marcado como Pagado.");
    }

    // Rama de pago rechazado: el pedido se deja en Solicitado para que el cliente
    // pueda reintentar el pago. Solo se registra el motivo.
    private void registrarRechazo(Pedido pedido, Payment pago) {
        String motivo = pago.getStatusDetail() != null ? pago.getStatusDetail() : "rechazado";
        System.out.println("Pago RECHAZADO para el pedido " + pedido.getIdPedido()
                + " (transacción " + pago.getId() + "). Motivo: " + motivo
                + ". El pedido queda disponible para reintentar el pago.");
    }

    private void enviarNotificacionConfirmacion(Pedido pedido) {
        try {
            List<Producto> productos = pedido.getProductos().stream()
                    .map(pp -> pp.getProducto())
                    .toList();
            notificacionesService.notificarConfirmacionPedidoConPDF(
                    pedido.getCliente(), productos, pedido.getRestaurante(), pedido);
        } catch (Exception e) {
            // La notificación no debe romper el procesamiento del webhook.
            System.err.println("No se pudo enviar la notificación de confirmación: " + e.getMessage());
        }
    }

    // Estado de pago consultable por el front al volver del checkout (back_urls).
    public DTOEstadoPago consultarEstadoPedido(Integer idPedido) {
        Pedido pedido = ordenesService.obtenerOFallar(idPedido);
        boolean pagado = pedido.getEstado() == EnumEstadoPedido.Pagado;
        String idTransaccion = pedido.getPago() != null ? pedido.getPago().getIdTransaccion() : null;
        return new DTOEstadoPago(
                pedido.getIdPedido(),
                pedido.getEstado(),
                pagado,
                idTransaccion,
                pedido.getTotal());
    }

    // Extrae el id del pago del payload del webhook. MP usa varias formas:
    // {"type":"payment","data":{"id":"123"}} o {"action":"payment.created", ...}.
    // Devuelve null si la notificación no corresponde a un pago.
    private Long extraerPaymentId(String payload) {
        if (payload == null || payload.isBlank()) {
            return null;
        }
        try {
            JsonNode root = objectMapper.readTree(payload);

            String type = root.path("type").asText("");
            String action = root.path("action").asText("");
            String topic = root.path("topic").asText("");

            boolean esPago = type.equalsIgnoreCase("payment")
                    || action.startsWith("payment")
                    || topic.equalsIgnoreCase("payment");
            if (!esPago) {
                return null;
            }

            JsonNode dataId = root.path("data").path("id");
            if (!dataId.isMissingNode() && !dataId.asText().isBlank()) {
                return Long.valueOf(dataId.asText());
            }
            // Algunas notificaciones traen el id directamente.
            JsonNode id = root.path("id");
            if (!id.isMissingNode() && id.asText().matches("\\d+")) {
                return Long.valueOf(id.asText());
            }
            return null;
        } catch (Exception e) {
            System.err.println("Payload de webhook no parseable: " + e.getMessage());
            return null;
        }
    }

    private Integer resolverIdPedido(Payment pago) {
        String extRef = pago.getExternalReference();
        if (extRef == null || !extRef.matches("\\d+")) {
            throw new PagoRechazadoException(null,
                    "El pago no tiene un externalReference válido (idPedido)");
        }
        return Integer.valueOf(extRef);
    }
}
