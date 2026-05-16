package com.backend.trego.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.trego.entity.Combo;

@Repository
public interface ComboRepository extends JpaRepository<Combo, Integer> {
}
