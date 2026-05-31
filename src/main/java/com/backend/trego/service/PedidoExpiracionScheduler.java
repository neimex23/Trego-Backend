package com.backend.trego.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PedidoExpiracionScheduler {

    private final PedidoService pedidoService;

    public PedidoExpiracionScheduler(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void cancelarPedidosExpirados() {
        int cancelados = pedidoService.cancelarPedidosExpirados();
        if (cancelados > 0) {
            System.out.println("[Pedidos] Se cancelaron " + cancelados + " pedido(s) por expiración.");
        }
    }
}
