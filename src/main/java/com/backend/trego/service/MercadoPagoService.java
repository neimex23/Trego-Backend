package com.backend.trego.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.backend.trego.entity.DTOs.DTOPedido;
import com.backend.trego.entity.DTOs.DTOProducto;
import com.backend.trego.entity.MPResponse;
import com.backend.trego.entity.Pago;
import com.backend.trego.entity.Pedido;

import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;

@Service
public class MercadoPagoService {

    private final OrdenesService ordenesService;

    public MercadoPagoService(OrdenesService ordenesService) {
        this.ordenesService = ordenesService;
    }

    public MPResponse crearOrden(DTOPedido pedidoDTO) {
        if (pedidoDTO.getProductos() == null || pedidoDTO.getProductos().isEmpty()) {
            throw new IllegalArgumentException("El pedido debe incluir al menos un producto");
        }

        Pedido pedido = ordenesService.obtenerOFallar(pedidoDTO.getIdPedido());
        float monto = pedidoDTO.getTotal() != null
                ? pedidoDTO.getTotal().floatValue()
                : pedido.getTotal();
        pedido.setPago(new Pago(LocalDateTime.now(), monto, null, null));

        List<PreferenceItemRequest> items = pedidoDTO.getProductos().stream()
            .map(this::toPreferenceItem)
            .toList();
        
        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
            .success("http://localhost:3000/success")
            .failure("http://localhost:3000/failure")
            .pending("http://localhost:3000/pending")
            .build();


        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
            .items(items)
            .notificationUrl("https://webhook.site/d843dc6e-6757-413c-adc0-36bdc5616d8d")
            //.backUrls(backUrls)
            .externalReference(String.valueOf(pedidoDTO.getIdPedido()))
            .build();

        
        try {
            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);
            ordenesService.guardar(pedido);

            return new MPResponse(
                preference.getId(),
                preference.getInitPoint(),
                preference.getSandboxInitPoint()
            );
        } catch (MPApiException e) {
        throw new RuntimeException(
            "MP ERROR: " + e.getApiResponse().getContent(),
            e
        );
        } catch (MPException e) {

            throw new RuntimeException(
                "MP GENERAL ERROR: " + e.getMessage(),
                e
            );
        }
    }

    private PreferenceItemRequest toPreferenceItem(DTOProducto producto) {
        int cantidad = producto.getCantidad() != null && producto.getCantidad() > 0
                ? producto.getCantidad()
                : 1;
        String descripcion = producto.getDescripcion() != null ? producto.getDescripcion() : "";
        return PreferenceItemRequest.builder()
                .currencyId("UYU")
                .title(producto.getNombre())
                .description(descripcion)
                .quantity(cantidad)
                .unitPrice(BigDecimal.valueOf(producto.getPrecio()))
                .build();
    }

    //en elWebhook se debe procesar el pago con el idDepago, en el externalreference viene el idPedido, se busca el pedido, se actualiza su estado a pagado y se guarda el pago con el id de mp

}
