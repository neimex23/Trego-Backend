package com.backend.trego.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.backend.trego.entity.Cliente;
import com.backend.trego.entity.DTOs.DTOCliente;
import com.backend.trego.repository.UsuarioRepository;

@Service
public class ClienteService {

    private final UsuarioRepository repo;
    private final CurrentUserService currentUserService;

    public ClienteService(UsuarioRepository repo, CurrentUserService currentUserService) {
        this.repo = repo;
        this.currentUserService = currentUserService;
    }

    public List<Cliente> listar() {
        return repo.findAllClientes();
    }

    @Transactional
    public Cliente guardar(Cliente cliente) {
        return repo.save(cliente);
    }

    @Transactional
    public Cliente crear(DTOCliente dto) {
        validarDto(dto);
        return guardar(desdeDto(dto));
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

    // Actualiza un cliente existente aplicando sólo los campos no nulos del DTO.
    // Direcciones, Horarios y Contraseña se actualizan por endpoints específicos.
    @Transactional
    public Cliente actualizar(DTOCliente dto) {
        Cliente existente = obtenerOFallar(currentUserService.getCurrentId());
        if (dto.getEmail() != null && !dto.getEmail().isBlank() && !dto.getEmail().equals(existente.getEmail())) {
            if (repo.findClienteByEmail(dto.getEmail()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo electrónico ya se encuentra registrado");
            }
            existente.setEmail(dto.getEmail());
        }
        if (dto.getNombre() != null && !dto.getNombre().isBlank() && !dto.getNombre().equals(existente.getNombre())) {
            existente.setNombre(dto.getNombre());
        }

        if (dto.getUrlImagen() != null && !dto.getUrlImagen().isBlank() && !dto.getUrlImagen().equals(existente.getFotoPerfil())) {
            existente.setFotoPerfil(dto.getUrlImagen());
        }

        if (dto.getTelefono() != null && !dto.getTelefono().isBlank() && !dto.getTelefono().equals(existente.getTelefono())) {
            existente.setTelefono(dto.getTelefono());
        }
        return repo.save(existente);
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!repo.existsClienteById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado");
        }
        repo.deleteById(id);
    }

    @Transactional
    public Cliente cambiarHabilitado(Integer id, boolean habilitado) {
        Cliente c = obtenerOFallar(id);
        c.setHabilitado(habilitado);
        return repo.save(c);
    }

    // Persiste el token de FCM del dispositivo del cliente para poder enviarle
    // notificaciones push. Si el token viene vacío se interpreta como un logout
    // y se limpia el campo para evitar enviar a un dispositivo que ya no aplica.
    @Transactional
    public Cliente actualizarFcmToken(Integer id, String fcmToken) {
        Cliente c = obtenerOFallar(id);
        c.setFcmToken((fcmToken == null || fcmToken.isBlank()) ? null : fcmToken);
        return repo.save(c);
    }
}
