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
    private final RestauranteService restauranteService;

    public TregoSchedulers(PedidoService pedidoService, ProductoRepository productoRepository,
            RestauranteService restauranteService) {
        this.pedidoService = pedidoService;
        this.productoRepository = productoRepository;
        this.restauranteService = restauranteService;
    }

    // Reconciliación de cierre de locales. Corre cada minuto y cierra los restaurantes
    // cuyo cierreProgramado ya venció.
    @Scheduled(fixedRate = 60_000)
    public void cerrarLocalesVencidos() {
        int cerrados = restauranteService.cerrarLocalesVencidos();
        if (cerrados > 0) {
            System.out.println("[Locales] Se cerraron " + cerrados + " local(es) automáticamente.");
        }
    }

    //Elimina Pedidos en estado Solicitado
    @Scheduled(cron = "0 0 0 * * *", zone = "${app.timezone:America/Montevideo}")
    public void cancelarPedidosExpirados() {
        Integer eliminados = pedidoService.cancelarPedidosExpirados();
        if (eliminados > 0) {
            System.out.println("[Pedidos] Se eliminaron " + eliminados + " pedido(s) solicitados por expiración.");
        }
    }

    //Cancela todas las ofertas vencidas
    @Scheduled(cron = "0 0 0 * * *", zone = "${app.timezone:America/Montevideo}")
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
