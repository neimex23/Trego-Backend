package com.backend.trego.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.trego.entity.Reclamo;
import com.backend.trego.entity.Enums.EnumEstadoReclamo;

@Repository
public interface ReclamoRepository extends JpaRepository<Reclamo, Integer> {
    List<Reclamo> findByEstado(EnumEstadoReclamo estado);
}
