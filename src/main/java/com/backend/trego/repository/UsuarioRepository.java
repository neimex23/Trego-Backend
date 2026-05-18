package com.backend.trego.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.trego.entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
// Metodo clave para mapear el UID que nos da el token de Firebase
    Optional<Usuario> findByFirebaseUid(String firebaseUid);
}
