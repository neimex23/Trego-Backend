package com.backend.trego.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.trego.entity.Usuario;
import com.backend.trego.entity.DTOs.DTODireccion;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    // Metodo clave para mapear el UID que nos da el token de Firebase
    Optional<Usuario> findByFirebaseUid(String firebaseUid);

    boolean existsByEmail(String email);

    Optional<Usuario> findByEmail(String email);

    @Query("SELECT d FROM Cliente c JOIN c.direcciones d WHERE c.firebaseUid = :uid")
    List<DTODireccion> findDireccionesByFirebaseUid(@Param("uid") String uid);

}
