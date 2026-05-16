package com.backend.trego.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.backend.trego.entity.MPResponse;
import com.backend.trego.entity.Pago;
import com.backend.trego.entity.Pedido;
import com.backend.trego.entity.Producto;

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

    public MPResponse crearOrden(Pedido pedido) { //Generar  DTOPedido para frontend
        pedido.setPago(new Pago(LocalDateTime.now(), pedido.calcularTotal(), null , null));
        

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
            .success("http://localhost:3000/success")
            .failure("http://localhost:3000/failure")
            .pending("http://localhost:3000/pending")
            .build();


        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
            .items(items)
            .notificationUrl("https://webhook.site/d843dc6e-6757-413c-adc0-36bdc5616d8d")
            //.backUrls(backUrls)
            .externalReference(String.valueOf(pedido.getIdPedido()))
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

    //en elWebhook se debe procesar el pago con el idDepago, en el externalreference viene el idPedido, se busca el pedido, se actualiza su estado a pagado y se guarda el pago con el id de mp
    
}
