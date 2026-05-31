package com.backend.trego.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.backend.trego.entity.Cliente;
import com.backend.trego.entity.DTOs.DTOCliente;
import com.backend.trego.entity.DTOs.DTODireccion;
import com.backend.trego.entity.Enums.EnumRoles;
import com.backend.trego.repository.UsuarioRepository;

@Service
public class ClienteService {

    private final UsuarioRepository repo;

    public ClienteService(UsuarioRepository repo) {
        this.repo = repo;
    }

    public List<Cliente> listar() {
        return repo.findAllClientes();
    }

    public Cliente guardar(Cliente cliente) {
        return repo.save(cliente);
    }

    public Cliente crear(DTOCliente dto) {
        validarDto(dto);
        return guardar(desdeDto(dto));
    }

    public Cliente actualizar(Integer id, DTOCliente dto) {
        return actualizar(id, desdeDto(dto));
    }

    private void validarDto(DTOCliente dto) {
        if (dto.getNombre() == null || dto.getNombre().isBlank()
                || dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Nombre y email son obligatorios");
        }
    }

    private Cliente desdeDto(DTOCliente dto) {
        return new Cliente(
               dto.getNombre(),
               dto.getEmail(),
               dto.getUrlImagen(),
               dto.getTelefono(),
               dto.getDirecciones(),
               dto.getUidCliente()
        );
    }

    public Optional<Cliente> obtener(Integer id) {
        return repo.findClienteById(id);
    }

    public Cliente obtenerOFallar(Integer id) {
        return repo.findClienteById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));
    }

    public Optional<Cliente> buscarPorEmail(String email) {
        return repo.findClienteByEmail(email);
    }

    public Optional<Cliente> buscarPorUid(String uidCliente) {
        return repo.findClienteByUidCliente(uidCliente);
    }

    public Cliente actualizar(Integer id, Cliente datos) {
        Cliente existente = obtenerOFallar(id);
        existente.setNombre(datos.getNombre());
        existente.setEmail(datos.getEmail());
        existente.setFotoPerfil(datos.getFotoPerfil());
        existente.setTelefono(datos.getTelefono());
        existente.setUidCliente(datos.getUidCliente());
        if (datos.getDirecciones() != null) {
            existente.setDirecciones(datos.getDirecciones());
        }
        return repo.save(existente);
    }

    public void eliminar(Integer id) {
        if (!repo.existsClienteById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado");
        }
        repo.deleteById(id);
    }

    public Cliente cambiarHabilitado(Integer id, boolean habilitado) {
        Cliente c = obtenerOFallar(id);
        c.setHabilitado(habilitado);
        return repo.save(c);
    }
}
