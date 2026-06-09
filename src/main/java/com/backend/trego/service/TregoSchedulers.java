package com.backend.trego.service;

import com.backend.trego.entity.Producto;
import com.backend.trego.repository.ProductoRepository;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class TregoSchedulers {

    private final PedidoService pedidoService;
    private final ProductoRepository productoRepository;

    public TregoSchedulers(PedidoService pedidoService, ProductoRepository productoRepository) {
        this.pedidoService = pedidoService;
        this.productoRepository = productoRepository;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void cancelarPedidosExpirados() {
        Integer cancelados = pedidoService.cancelarPedidosExpirados();
        if (cancelados > 0) {
            System.out.println("[Pedidos] Se cancelaron " + cancelados + " pedido(s) por expiración.");
        }
    }

    @Scheduled(cron = "0 * * * * *")
    public void desactivarOfertasExpiradas() {
        List<Producto> productos = productoRepository.findProductosConOfertaInvalida(LocalDateTime.now());
        if (!productos.isEmpty()) {
            for (Producto producto : productos) {
                producto.setOfertaActiva(false);
            }
            productoRepository.saveAll(productos);
            System.out.println("[Ofertas] Se desactivaron " + productos.size() + " oferta(s) expirada(s).");
        }
    }
}
