package com.backend.trego.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.backend.trego.entity.Pedido;
import com.backend.trego.entity.Enums.EnumEstadoPedido;
import com.backend.trego.repository.PedidoRepository;

@Service
public class OrdenesService {

    private final PedidoRepository repo;

    public OrdenesService(PedidoRepository repo) {
        this.repo = repo;
    }

    public List<Pedido> listar() {
        return repo.findAll();
    }

    public Pedido guardar(Pedido pedido) {
        return repo.save(pedido);
    }

    public Optional<Pedido> obtener(Integer id) {
        return repo.findById(id);
    }

    public Pedido obtenerOFallar(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
    }

    public List<Pedido> listarPorCliente(int idCliente) {
        return repo.findByClienteIdUsuario(idCliente);
    }

    public List<Pedido> listarPorRestaurante(int idRestaurante) {
        return repo.findByRestauranteIdUsuario(idRestaurante);
    }

    public List<Pedido> listarPorEstado(EnumEstadoPedido estado) {
        return repo.findByEstado(estado);
    }

    public Pedido cambiarEstado(Integer id, EnumEstadoPedido nuevoEstado) {
        Pedido p = obtenerOFallar(id);
        p.setEstado(nuevoEstado);
        return repo.save(p);
    }

    public Pedido cancelar(Integer id, String razon) {
        Pedido p = obtenerOFallar(id);
        p.setEstado(EnumEstadoPedido.Cancelado);
        p.setRazonCancelacion(razon != null ? razon : "Cancelado por el cliente");
        return repo.save(p);
    }

    public Pedido recalcularTotal(Integer id) {
        Pedido p = obtenerOFallar(id);
        p.setTotal(p.calcularTotal());
        return repo.save(p);
    }

    public void eliminar(Integer id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado");
        }
        repo.deleteById(id);
    }

    @Transactional
    public int cancelarPedidosExpirados() {
        List<Pedido> expirados = repo.findByFechaExpiracionNotNullAndFechaExpiracionBefore(LocalDateTime.now());
        for (Pedido pedido : expirados) {
            pedido.setEstado(EnumEstadoPedido.Cancelado);
            pedido.setRazonCancelacion("Expirado: plazo de pago de 24 horas vencido");
            pedido.setFechaExpiracion(null);
        }
        repo.saveAll(expirados);
        return expirados.size();
    }
}
