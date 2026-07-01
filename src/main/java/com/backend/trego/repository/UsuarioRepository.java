package com.backend.trego.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.trego.entity.Administrador;
import com.backend.trego.entity.Cliente;
import com.backend.trego.entity.Restaurante;
import com.backend.trego.entity.Usuario;
import com.backend.trego.entity.DTOs.DTODireccion;
import com.backend.trego.entity.Enums.EnumCategoriaRestaurante;

// Repositorio unificado para toda la jerarquia Usuario (Administrador, Cliente, Restaurante).
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    boolean existsByEmail(String email);

    Optional<Usuario> findByEmail(String email);

    @Query("SELECT d FROM Cliente c JOIN c.direcciones d WHERE c.uidCliente = :uid")
    List<DTODireccion> findDireccionesByUid(@Param("uid") String uid);

    // ---- Administrador ----
    @Query("SELECT a FROM Administrador a WHERE a.email = :email")
    Optional<Administrador> findAdministradorByEmail(@Param("email") String email);

    @Query("SELECT a FROM Administrador a WHERE a.idUsuario = :id")
    Optional<Administrador> findAdministradorById(@Param("id") Integer id);

    // ---- Cliente ----
    @Query("SELECT c FROM Cliente c WHERE c.email = :email")
    Optional<Cliente> findClienteByEmail(@Param("email") String email);

    @Query("SELECT c FROM Cliente c WHERE c.uidCliente = :uidCliente")
    Optional<Cliente> findClienteByUidCliente(@Param("uidCliente") String uidCliente);

    @Query("SELECT c FROM Cliente c WHERE c.idUsuario = :id")
    Optional<Cliente> findClienteById(@Param("id") Integer id);

    @Query("SELECT c FROM Cliente c")
    List<Cliente> findAllClientes();

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Cliente c WHERE c.idUsuario = :id")
    boolean existsClienteById(@Param("id") Integer id);

    @Query("Select c FROM Cliente c WHERE c.uidCliente = :uidCliente")
    Optional<Cliente> findByUidCliente(@Param("uidCliente") String uidCliente);

    @Query("SELECT c FROM Cliente c WHERE c.telefono = :telefono")
    Optional<Cliente> findClienteByTelefono(@Param("telefono") String telefono);

    // ---- Restaurante ----
    @Query("SELECT r FROM Restaurante r WHERE r.email = :email")
    Optional<Restaurante> findRestauranteByEmail(@Param("email") String email);

    @Query("SELECT r FROM Restaurante r WHERE r.rut = :rut")
    Optional<Restaurante> findRestauranteByRut(@Param("rut") String rut);

    @Query("SELECT r FROM Restaurante r WHERE r.idUsuario = :id")
    Optional<Restaurante> findRestauranteById(@Param("id") Integer id);

    @Query("SELECT r FROM Restaurante r WHERE r.habilitado = true")
    List<Restaurante> findRestaurantesHabilitados();

    @Query("SELECT r FROM Restaurante r WHERE r.abierto = true AND r.cierreProgramado IS NOT NULL AND r.cierreProgramado <= :ahora")
    List<Restaurante> findRestaurantesParaCerrar(@Param("ahora") LocalDateTime ahora);

    @Query("SELECT r FROM Restaurante r WHERE r.habilitado = false and r.rut IS NOT NULL")
    List<Restaurante> findRestaurantesNoHabilitados();

    @Query("SELECT r FROM Restaurante r WHERE r.habilitado = true AND LOWER(r.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Restaurante> findRestaurantesHabilitadosPorNombre(@Param("nombre") String nombre);

    @Query("SELECT r FROM Restaurante r WHERE r.categoria = :categoria")
    List<Restaurante> findRestaurantesPorCategoria(@Param("categoria") EnumCategoriaRestaurante categoria);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Restaurante r SET r.calificacionProm = :promedio WHERE r.idUsuario = :idRestaurante")
    void updateCalificacionProm(
            @Param("idRestaurante") Integer idRestaurante,
            @Param("promedio") float promedio);
}
