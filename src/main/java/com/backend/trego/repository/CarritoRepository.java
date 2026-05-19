package com.backend.trego.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.trego.entity.Carrito;


 @Repository
public interface CarritoRepository extends JpaRepository<Carrito, Integer> {
    public Carrito findByUidCliente(String uidCliente);

    public void deleteByUidCliente(String uidCliente);

    public boolean addProductoToCarrito(String uidCliente, Integer idProducto);
}
