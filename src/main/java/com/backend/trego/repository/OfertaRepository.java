package com.backend.trego.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.trego.entity.Oferta;

@Repository
public interface OfertaRepository extends JpaRepository<Oferta, Integer> {
    List<Oferta> findByFechaInicioBeforeAndFechaFinAfter(LocalDateTime now, LocalDateTime sameNow);
}
