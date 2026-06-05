package com.backend.trego.service;

import com.backend.trego.entity.DTOs.DTOCliente;
import com.backend.trego.entity.DTOs.DTODireccion;
import com.backend.trego.entity.DTOs.DTOFirma;
import com.backend.trego.entity.DTOs.DTOUsuario;
import com.backend.trego.repository.UsuarioRepository;
import com.backend.trego.entity.RegistroTemporal;
import com.backend.trego.entity.Restaurante;
import com.backend.trego.entity.Usuario;
import com.backend.trego.entity.Administrador;
import com.backend.trego.entity.Cliente;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Map;

// Gestión de usuarios: alta, búsqueda, direcciones y verificación por email.
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final NotificacionesService notificacionesService;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserService currentUserService;
    private final CloudinaryService cloudinaryService;

    // Mapa en memoria: email -> datos del registro pendiente (código + contraseña)
    private final Map<String, RegistroTemporal> registrosPendientes = new ConcurrentHashMap<>();

    public UsuarioService(UsuarioRepository usuarioRepository, NotificacionesService notificacionesService,
            PasswordEncoder passwordEncoder, CurrentUserService currentUserService,
            CloudinaryService cloudinaryService) {
        this.usuarioRepository = usuarioRepository;
        this.notificacionesService = notificacionesService;
        this.passwordEncoder = passwordEncoder;
        this.currentUserService = currentUserService;
        this.cloudinaryService = cloudinaryService;
    }

    // Da de alta un cliente nuevo a partir del DTO y lo devuelve ya con su id.
    @Transactional
    public Usuario altaUsuario(DTOUsuario usuarioDTO) {
        Cliente nuevoCliente = new Cliente();

        if (!usuarioDTO.getUid().isBlank()) {
            ((Cliente) nuevoCliente).setUidCliente(usuarioDTO.getUid());
        }
        nuevoCliente.setEmail(usuarioDTO.getEmail());
        nuevoCliente.setNombre(usuarioDTO.getNombre());
        nuevoCliente.setFotoPerfil(usuarioDTO.getUrlImagen());
        nuevoCliente.setTelefono(usuarioDTO.getTelefono());
        nuevoCliente.setRol(usuarioDTO.getRol());

        return usuarioRepository.save(nuevoCliente);
    }

    // Da de alta un administrador con email y contraseña cifrada.
    @Transactional
    public Administrador altaAdministrador(String email, String password) {
        if (existeUsuario(email)) {
            throw new IllegalArgumentException("El correo electrónico ya se encuentra ingresado en el sistema.");
        }

        String passwordCifrada = passwordEncoder.encode(password);
        Administrador nuevoAdmin = new Administrador(
                "DefaultAdmin",
                email,
                null,
                passwordCifrada);
        return usuarioRepository.save(nuevoAdmin);
    }

    @Transactional
    // Da de alta un administrador con los datos del DTO generando una contraseña
    // nueva
    public Administrador altaAdministrador(DTOUsuario adminDTO) {
        if (existeUsuario(adminDTO.getEmail())) {
            throw new IllegalArgumentException("El correo electrónico ya se encuentra ingresado en el sistema.");
        }
        String passwordPlana = java.util.UUID.randomUUID().toString().substring(0, 10);
        String passwordCifrada = passwordEncoder.encode(passwordPlana);
        Administrador nuevoAdmin = new Administrador(
                adminDTO.getNombre(),
                adminDTO.getEmail(),
                adminDTO.getUrlImagen(),
                passwordCifrada);
        Administrador adminGuardado = usuarioRepository.save(nuevoAdmin); // Guardar el administrador en la base de
                                                                          // datos
        notificacionesService.notificarCredencialesNuevoAdmin(adminDTO.getEmail(), passwordPlana); // Se notificia por
                                                                                                   // correo al nuevo
                                                                                                   // Administrador
        return adminGuardado;
    }

    // Arranca el registro de un restaurante: chequea que el email no exista,
    // manda el código y guarda los datos en el mapa hasta que lo confirme.
    public void iniciarRegistroRestaurante(String email, String password) {
        if (existeUsuario(email)) {
            throw new IllegalArgumentException("El correo electrónico ya se encuentra ingresado en el sistema.");
        }
        String codigo = this.enviarCodigoVerificacion(email);

        RegistroTemporal registro = new RegistroTemporal(email, password, codigo);
        registrosPendientes.put(email, registro);
    }

    @Transactional
    public DTOUsuario registrarRestaurante(String email, String password) {

        String passwordCifrada = passwordEncoder.encode(password);

        Restaurante nuevoRestaurante = new Restaurante(email, passwordCifrada);

        Restaurante restauranteGuardado = usuarioRepository.save(nuevoRestaurante);

        registrosPendientes.remove(email);

        DTOUsuario usuario = new DTOUsuario(
                restauranteGuardado.getIdUsuario(),
                null,
                null,
                restauranteGuardado.getEmail(),
                null,
                null,
                null,
                restauranteGuardado.getRol());

        return usuario;
    }

    public Boolean existeUsuario(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public String enviarCodigoVerificacion(String email) {
        return notificacionesService.codigoVerificacionEmail(email);
    }

    // Valida el código que el usuario ingresó y, si es correcto, da de alta el
    // restaurante.
    public DTOUsuario verificarCodigo(String email, String codigo) {
        RegistroTemporal pendiente = registrosPendientes.get(email);

        if (pendiente == null || pendiente.estaExpiradoCodigo() || !pendiente.getCodigoVerificacion().equals(codigo)) {
            throw new IllegalArgumentException("Código inválido o expirado");
        }

        return registrarRestaurante(pendiente.getEmail(), pendiente.getPassword());

    }

    // Reenvía el código reutilizando los datos que quedaron guardados en el mapa.
    public void reenviarCodigoVerificacion(String email) {
        RegistroTemporal pendiente = registrosPendientes.get(email);

        // Si ya no está en el mapa es porque expiró del todo y lo borró el @Scheduled
        if (pendiente == null) {
            throw new IllegalArgumentException(
                    "El tiempo de registro ha expirado por completo. Por favor, vuelve a ingresar tus datos.");
        }

        String nuevoCodigo = this.enviarCodigoVerificacion(email);

        RegistroTemporal registroActualizado = new RegistroTemporal(email, pendiente.getPassword(), nuevoCodigo);
        registrosPendientes.put(email, registroActualizado);

        System.out.println("[Caché] Nuevo código generado y guardado para: " + email);
    }

    // Cada 5 minutos limpia del mapa los registros que quedaron sin completar.
    @Scheduled(fixedRate = 300000)
    public void limpiarRegistrosExpirados() {
        if (registrosPendientes.isEmpty()) {
            return;
        }

        System.out.println("[Caché] Iniciando limpieza automática de registros expirados...");
        int sizeInitial = registrosPendientes.size();

        registrosPendientes.entrySet().removeIf(entry -> {
            boolean expirado = entry.getValue().estaExpiradoCacheReenvio();
            if (expirado) {
                System.out.println("[Caché] Removiendo registro colgado del email: " + entry.getKey());
            }
            return expirado;
        });

        int eliminados = sizeInitial - registrosPendientes.size();
        if (eliminados > 0) {
            System.out.println("[Caché] Limpieza terminada. Se eliminaron " + eliminados + " registros colgados.");
        }
    }

    // Devuelve el usuario autenticado actualmente, resuelto desde el token.
    // Cliente: se busca por firebaseUid; Restaurante/Administrador: por idUsuario.
    public DTOUsuario obtenerUsuarioActual() {
        var auth = currentUserService.getCurrentUser();
        Usuario u = null;
        String uid = "null";

        if (auth.getIdUsuario() != null) {
            u = usuarioRepository.findById(auth.getIdUsuario()).orElse(null);
        }
        if (u == null && auth.getUid() != null) {
            u = usuarioRepository.findByUidCliente(auth.getUid()).orElse(null);
            uid = auth.getUid();
        }
        if (u == null && auth.getEmail() != null) {
            u = usuarioRepository.findByEmail(auth.getEmail()).orElse(null);
        }
        if (u == null) {
            throw new IllegalStateException("No se encontró el usuario autenticado en la base de datos.");
        }

        return new DTOUsuario(
                u.getIdUsuario(),
                uid,
                u.getNombre(),
                u.getEmail(),
                null,
                u.getFotoPerfil(),
                null,
                u.getRol());
    }

    public List<DTODireccion> obtenerDirecciones() {
        String uid = currentUserService.getCurrentUid();
        if (uid == null) {
            throw new IllegalStateException(
                    "No se encontró el uid del usuario autenticado. Este endpoint es solo para clientes autenticados con Firebase.");
        }
        return usuarioRepository.findDireccionesByUid(uid);
    }

    // Agrega una nueva direccion al cliente autenticado. Restaurante usa
    // actualizarDireccion (tiene una sola); Administrador no maneja direcciones.
    @Transactional
    public void agregarDireccion(DTODireccion dto) {
        var auth = currentUserService.getCurrentUser();
        if (!"Cliente".equals(auth.getRol())) {
            throw new IllegalStateException(
                    "Solo los clientes pueden agregar direcciones (rol actual: " + auth.getRol() + ").");
        }
        String uid = auth.getUid();
        if (uid == null || uid.isBlank()) {
            throw new IllegalStateException(
                    "El cliente autenticado no tiene UID de Firebase asociado.");
        }
        Cliente cliente = usuarioRepository.findByUidCliente(uid)
                .orElseThrow(() -> new IllegalStateException("Cliente no encontrado para el UID: " + uid));
        cliente.addDireccion(dto);
        usuarioRepository.save(cliente);
    }

    public void actualizarDireccion(String tagModificar, DTODireccion dtoNueva, Boolean client) {
        if (client) {
            System.out.println("Entro con " + client + " El tag: " + tagModificar);
            String uid = currentUserService.getCurrentUid();
            Cliente cliente = usuarioRepository.findByUidCliente(uid)
                    .orElseThrow(() -> new IllegalStateException("Cliente no encontrado para el UID: " + uid));
            cliente.getDirecciones().removeIf(
                    d -> d.getTag().equals(tagModificar));
            cliente.getDirecciones().add(dtoNueva);
            usuarioRepository.save(cliente);
        } else {
            Integer id = currentUserService.getCurrentId();
            Usuario u = usuarioRepository.findById(id)
                    .orElseThrow(() -> new IllegalStateException("Usuario no encontrado para el ID: " + id));
            if (u instanceof Restaurante restaurante) {
                restaurante.setDireccion(dtoNueva);
                usuarioRepository.save(restaurante);
            }
        }

        throw new IllegalStateException(
                "El usuario autenticado no es ni Cliente ni Restaurante");
    }

    public void eliminarDireccion(String tagEliminar) {
        Integer id = currentUserService.getCurrentId();
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado para el ID: " + id));

        if (u instanceof Cliente cliente) {
            cliente.getDirecciones().removeIf(
                    d -> d.getTag().equals(tagEliminar));
            usuarioRepository.save(cliente);
            return;
        }

        if (u instanceof Restaurante restaurante) {
            throw new IllegalStateException(
                    "Los restaurantes no pueden no tener una dirección, para modificarla deben usar el endpoint de actualizar dirección con el tag de la dirección actual.");
        }

        throw new IllegalStateException(
                "El usuario autenticado no es ni Cliente ni Restaurante");
    }

    public DTOFirma firmarArchivo(String nombreArchivo, String tipoArchivo) {
        return cloudinaryService.firmar(nombreArchivo, tipoArchivo);
    }

    // Actualiza la contraseña del usuario autenticado segun su rol.
    // Cliente: delega en Firebase Auth (no se guarda contraseña local).
    // Restaurante / Administrador: cifra con PasswordEncoder y persiste.
    @Transactional
    public void actualizarContraseña(String nuevaContraseña) {
        if (nuevaContraseña == null || nuevaContraseña.length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres.");
        }

        var auth = currentUserService.getCurrentUser();
        String rol = auth.getRol();

        if ("Cliente".equals(rol)) {
            throw new IllegalStateException(
                    "Los clientes autenticados con Firebase deben cambiar su contraseña desde la aplicación google, delegando en Firebase Auth. Este endpoint es solo para usuarios con rol Restaurante o Administrador.");
        }

        Integer id = auth.getIdUsuario();
        if (id == null) {
            throw new IllegalStateException(
                    "El token del usuario autenticado no contiene idUsuario (rol: " + rol + ").");
        }
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado para el ID: " + id));

        String passwordCifrada = passwordEncoder.encode(nuevaContraseña);

        if (u instanceof Administrador admin) {
            admin.setPassword(passwordCifrada);
            usuarioRepository.save(admin);
            return;
        }

        if (u instanceof Restaurante restaurante) {
            restaurante.setPassword(passwordCifrada);
            usuarioRepository.save(restaurante);
            return;
        }

        throw new IllegalStateException(
                "Rol no soportado para actualizacion de contraseña: " + rol);
    }

}
