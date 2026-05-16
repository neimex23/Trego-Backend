package com.backend.trego.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.trego.entity.Plato;

@Repository
public interface PlatoRepository extends JpaRepository<Plato, Integer> {
}
