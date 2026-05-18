package com.backend.trego.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.trego.entity.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    // Hereda los métodos ABM estándar (save, findById, etc.)
    Optional<Cliente> findByEmail(String email);
    Optional<Cliente> findByUidCliente(String uidCliente);
}
