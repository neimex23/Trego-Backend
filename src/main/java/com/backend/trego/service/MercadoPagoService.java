package com.backend.trego.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.backend.trego.entity.MPResponse;
import com.backend.trego.entity.Pago;
import com.backend.trego.entity.Pedido;

import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentRefundClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.payment.PaymentRefund;
import com.mercadopago.resources.preference.Preference;

import java.util.HashMap;
import java.util.Map;

@Service
public class MercadoPagoService {

    private final OrdenesService ordenesService;

    // URL pública a la que MercadoPago enviará las notificaciones (webhook).
    // En producción debe apuntar al backend; en local se usa un túnel (ngrok) o
    // webhook.site para inspeccionar el payload.
    @Value("${mercadopago.webhook.url:}")
    private String notificationUrl;

    // URLs a las que MercadoPago redirige al usuario tras el checkout. Apuntan a
    // la app front (React) para mostrar el resultado del pago.
    @Value("${mercadopago.back.url.success:http://localhost:5173/success}")
    private String backUrlSuccess;

    @Value("${mercadopago.back.url.failure:http://localhost:5173/failure}")
    private String backUrlFailure;

    @Value("${mercadopago.back.url.pending:http://localhost:5173/pending}")
    private String backUrlPending;

    public MercadoPagoService(OrdenesService ordenesService) {
        this.ordenesService = ordenesService;
    }

    // Crea la preferencia de pago en MercadoPago a partir del pedido y devuelve
    // las URLs de checkout. El externalReference lleva el idPedido para poder
    // resolver el pedido cuando llegue el webhook.
    public MPResponse crearOrden(Pedido pedido) {
        pedido.setPago(new Pago(LocalDateTime.now(), pedido.calcularTotal(), null, null));

        List<PreferenceItemRequest> items = pedido.getProductos().stream()
                .map(pp -> PreferenceItemRequest.builder()
                        .currencyId("UYU")
                        .title(pp.getProducto().getNombre())
                        .description(pp.getProducto().getDescripcion())
                        .quantity(pp.getCantidad())
                        .unitPrice(BigDecimal.valueOf(pp.getProducto().getPrecio()))
                        .build())
                .toList();

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success(backUrlSuccess)
                .failure(backUrlFailure)
                .pending(backUrlPending)
                .build();

        PreferenceRequest.PreferenceRequestBuilder builder = PreferenceRequest.builder()
                .items(items)
                .backUrls(backUrls)
                .autoReturn("approved")
                .externalReference(String.valueOf(pedido.getIdPedido()));

        // notificationUrl solo se setea si está configurada (MP rechaza URLs vacías
        // o de localhost). En local conviene usar un túnel público.
        if (notificationUrl != null && !notificationUrl.isBlank()) {
            builder.notificationUrl(notificationUrl);
        }

        PreferenceRequest preferenceRequest = builder.build();

        try {
            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);
            ordenesService.guardar(pedido);

            return new MPResponse(
                    preference.getId(),
                    preference.getInitPoint(),
                    preference.getSandboxInitPoint());
        } catch (MPApiException e) {
            throw new RuntimeException(
                    "MP ERROR: " + e.getApiResponse().getContent(),
                    e);
        } catch (MPException e) {
            throw new RuntimeException(
                    "MP GENERAL ERROR: " + e.getMessage(),
                    e);
        }
    }

    // Consulta el pago en MercadoPago por su id (el que llega en el webhook).
    // Devuelve el objeto Payment con el estado (approved/rejected/pending), el
    // externalReference (idPedido), el monto y los datos de la tarjeta.
    // Realiza el reembolso total de un pago en MercadoPago a partir del id de
    // pago (el que quedó guardado en Pago.idTransaccion). Se envía un header
    // x-idempotency-key para que MercadoPago descarte reintentos del mismo
    // reembolso: si se vuelve a llamar con la misma clave, MP devuelve el
    // resultado original en lugar de generar un reembolso duplicado.
    public PaymentRefund reembolsarPago(Long paymentId, String idempotencyKey) {
        try {
            PaymentRefundClient client = new PaymentRefundClient();
            Map<String, String> headers = new HashMap<>();
            if (idempotencyKey != null && !idempotencyKey.isBlank()) {
                headers.put("x-idempotency-key", idempotencyKey);
            }
            MPRequestOptions options = MPRequestOptions.builder()
                    .customHeaders(headers)
                    .build();
            return client.refund(paymentId, options);
        } catch (MPApiException e) {
            throw new RuntimeException(
                    "MP ERROR al reembolsar pago " + paymentId + ": " + e.getApiResponse().getContent(),
                    e);
        } catch (MPException e) {
            throw new RuntimeException(
                    "MP GENERAL ERROR al reembolsar pago " + paymentId + ": " + e.getMessage(),
                    e);
        }
    }

    public Payment consultarPago(Long paymentId) {
        try {
            PaymentClient client = new PaymentClient();
            return client.get(paymentId);
        } catch (MPApiException e) {
            throw new RuntimeException(
                    "MP ERROR al consultar pago " + paymentId + ": " + e.getApiResponse().getContent(),
                    e);
        } catch (MPException e) {
            throw new RuntimeException(
                    "MP GENERAL ERROR al consultar pago " + paymentId + ": " + e.getMessage(),
                    e);
        }
    }
}
