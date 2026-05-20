package com.backend.trego.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.backend.trego.entity.Carrito;
import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Integer> {
    
    // Spring Data JPA traducirá esto como:
    // JOIN cliente c ON carrito.cliente_id = c.id WHERE c.uid_cliente = ?
    Optional<Carrito> findByCliente_UidCliente(String uidCliente);
    
    void deleteByCliente_UidCliente(String uidCliente);
    
    // Nota: addProductoToCarrito fue eliminado. 
    // Esa lógica se implementa en CarritoService usando .save()
}
