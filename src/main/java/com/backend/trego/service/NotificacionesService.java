package com.backend.trego.service;

import com.backend.trego.entity.Pago;
import com.backend.trego.entity.Pedido;
import com.backend.trego.entity.Producto;
import com.backend.trego.entity.Reclamo;
import com.backend.trego.entity.Restaurante;
import com.backend.trego.entity.Usuario;
import com.backend.trego.entity.Cliente;
import com.backend.trego.entity.Enums.EnumEstadoReclamo;
import com.backend.trego.entity.DTOs.DTODireccion;
import com.backend.trego.entity.DTOs.DTOPedido;
import com.backend.trego.entity.DTOs.DTOProductoPedido;
import com.backend.trego.entity.DTOs.DTOProductoSimplificado;
import com.backend.trego.repository.UsuarioRepository;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;

import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

// Envío de notificaciones a clientes y restaurantes (email, y a futuro push/SMS).
@Service
public class NotificacionesService {

    public enum TipoCredencial {
        NUEVO_ADMIN,
        RECUPERACION
    }

    private final JavaMailSender mailSender;
    private final ManejadorPDFService generarPDF;
    private final UsuarioRepository usuarioRepository;

    @Value("${mail.from}")
    private String mailFrom;

    @Value("${mail.from.name}")
    private String mailFromName;

    @Value("${app.dev.mail-log-only:false}")
    private boolean mailLogOnly;

    public NotificacionesService(JavaMailSender mailSender, ManejadorPDFService generarPDF,
            UsuarioRepository usuarioRepository) {
        this.mailSender = mailSender;
        this.generarPDF = generarPDF;
        this.usuarioRepository = usuarioRepository;
    }

    // Notifica al cliente que su pedido fue confirmado por el restaurante, con el
    // tiempo estimado de entrega (preparación + viaje).
    @Async
    public void notificarConfirmacionPedido(DTOPedido pedidoDTO, Integer tiempoEstimado) {
        if (pedidoDTO == null) {
            System.err.println("[Notificacion] pedidoDTO nulo, se omite el envio.");
            return;
        }

        Integer idCliente = pedidoDTO.getIdCliente();
        if (idCliente == null) {
            System.err.println("[Notificacion] Pedido " + pedidoDTO.getIdPedido()
                    + " sin idCliente, se omite el envio.");
            return;
        }

        Optional<Cliente> clienteOpt = usuarioRepository.findClienteById(idCliente);
        if (clienteOpt.isEmpty() || clienteOpt.get().getEmail() == null
                || clienteOpt.get().getEmail().isBlank()) {
            System.err.println("[Notificacion] Cliente " + idCliente
                    + " no encontrado o sin email; pedido " + pedidoDTO.getIdPedido());
            return;
        }
        Cliente cliente = clienteOpt.get();

        try {
            MimeMessage mail = mailSender.createMimeMessage();
            MimeMessageHelper estructuraMail = new MimeMessageHelper(mail, false, "UTF-8");

            estructuraMail.setTo(cliente.getEmail());
            estructuraMail.setFrom(mailFrom, mailFromName);
            estructuraMail.setSubject("Tu pedido #" + pedidoDTO.getIdPedido() + " fue confirmado");
            estructuraMail.setText(construirCuerpoConfirmacion(pedidoDTO, tiempoEstimado), true);

            mailSender.send(mail);
            System.out.println("Mail de confirmación enviado al cliente: " + cliente.getEmail()
                    + " (pedido " + pedidoDTO.getIdPedido() + ")");
        } catch (Exception e) {
            System.err.println("Error al enviar mail de confirmación del pedido "
                    + pedidoDTO.getIdPedido() + ": " + e.getMessage());
        }
    }

    // Cuerpo HTML del mail de confirmación: nombre del cliente, productos del
    // pedido, total, tiempo estimado y, si está disponible, la hora estimada de entrega
    private String construirCuerpoConfirmacion(DTOPedido pedidoDTO, Integer tiempoEstimado) {
        String nombreCliente = textoOGuion(pedidoDTO.getNombreCliente());
        String direccionEntrega = formatearDireccion(pedidoDTO.getDireccionEntrega());

        String tiempoMostrado = (tiempoEstimado != null && tiempoEstimado > 0)
                ? tiempoEstimado + " min"
                : "—";

        LocalDateTime horaEntrega = pedidoDTO.getHoraEntregaEstimada();
        String horaEntregaMostrada = horaEntrega != null
                ? horaEntrega.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : null;

        StringBuilder sb = new StringBuilder();

        sb.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e0e0e0;'>");
        sb.append("<div style='background-color: #FF6600; padding: 12px; text-align: center;'>");
        sb.append("<img src='https://tu-dominio.com/images/logo.png' alt='Trego' style='height: 50px;'/>");
        sb.append("</div>");
        sb.append("<div style='padding: 32px;'>");
        sb.append("<h2 style='color: #333;'>¡").append(nombreCliente)
                .append(", tu pedido fue confirmado!</h2>");

        sb.append("<p style='color: #555; font-size: 16px;'>")
                .append("El restaurante ya recibió tu pedido <strong>#")
                .append(pedidoDTO.getIdPedido())
                .append("</strong> y empezó a prepararlo.")
                .append("</p>");

        sb.append("<div style='background-color: #f9f9f9; border-left: 4px solid #FF6600; padding: 12px 16px; color: #555; margin: 16px 0;'>")
                .append("<p style='margin: 0;'><strong>Tiempo estimado:</strong> ")
                .append(tiempoMostrado)
                .append("</p>");
        if (horaEntregaMostrada != null) {
            sb.append("<p style='margin: 8px 0 0 0;'><strong>Entrega aproximada:</strong> ")
                    .append(horaEntregaMostrada)
                    .append("</p>");
        }
        sb.append("</div>");

        List<DTOProductoPedido> productos = pedidoDTO.getProductos();
        if (productos != null && !productos.isEmpty()) {
            sb.append("<p style='color: #555; font-size: 16px;'><strong>Detalle del pedido:</strong></p>");
            sb.append("<ul style='color: #555;'>");
            for (DTOProductoPedido linea : productos) {
                DTOProductoSimplificado prod = linea.getProducto();
                String nombreProducto = (prod != null) ? textoOGuion(prod.getNombre()) : "—";
                Integer cantidad = linea.getCantidad();
                sb.append("<li>")
                        .append(nombreProducto)
                        .append(" x ")
                        .append(cantidad != null ? cantidad : 1)
                        .append("</li>");
            }
            sb.append("</ul>");
        }

        if (pedidoDTO.getTotal() != null) {
            sb.append("<p style='color: #555; font-size: 16px;'><strong>Total:</strong> $")
                    .append(pedidoDTO.getTotal())
                    .append("</p>");
        }

        sb.append("<p style='color: #555;'><strong>Dirección de entrega:</strong> ")
                .append(direccionEntrega)
                .append("</p>");

        sb.append("<p style='color: #555;'>Podrás seguir el estado de tu pedido desde la app.</p>");
        sb.append("<hr style='border: none; border-top: 1px solid #e0e0e0; margin: 24px 0;'>");
        sb.append("<p style='color: #555;'><strong>El equipo de Trego</strong></p>");
        sb.append("</div>");

        sb.append("<div style='background-color: #f5f5f5; padding: 16px; text-align: center;'>");
        sb.append("<p style='color: #999; font-size: 12px; margin: 0;'>© 2026 Trego. Todos los derechos reservados.</p>");
        sb.append("</div>");
        sb.append("</div>");

        return sb.toString();
    }

    // Notifica al cliente que su pedido salió del restaurante y está en camino,
    // con el tiempo estimado de viaje.
    @Async
    public void notificarPedidoEnCamino(DTOPedido pedidoDTO, Integer tiempoViaje) {
        if (pedidoDTO == null) {
            System.err.println("[Notificacion] pedidoDTO nulo, se omite el envio.");
            return;
        }

        Integer idCliente = pedidoDTO.getIdCliente();
        if (idCliente == null) {
            System.err.println("[Notificacion] Pedido " + pedidoDTO.getIdPedido()
                    + " sin idCliente, se omite el envio.");
            return;
        }

        Optional<Cliente> clienteOpt = usuarioRepository.findClienteById(idCliente);
        if (clienteOpt.isEmpty() || clienteOpt.get().getEmail() == null
                || clienteOpt.get().getEmail().isBlank()) {
            System.err.println("[Notificacion] Cliente " + idCliente
                    + " no encontrado o sin email; pedido " + pedidoDTO.getIdPedido());
            return;
        }
        Cliente cliente = clienteOpt.get();

        try {
            MimeMessage mail = mailSender.createMimeMessage();
            MimeMessageHelper estructuraMail = new MimeMessageHelper(mail, false, "UTF-8");

            estructuraMail.setTo(cliente.getEmail());
            estructuraMail.setFrom(mailFrom, mailFromName);
            estructuraMail.setSubject("Tu pedido #" + pedidoDTO.getIdPedido() + " está en camino");
            estructuraMail.setText(construirCuerpoEnCamino(pedidoDTO, tiempoViaje), true);

            mailSender.send(mail);
            System.out.println("Mail de en-camino enviado al cliente: " + cliente.getEmail()
                    + " (pedido " + pedidoDTO.getIdPedido() + ")");
        } catch (Exception e) {
            System.err.println("Error al enviar mail de en-camino del pedido "
                    + pedidoDTO.getIdPedido() + ": " + e.getMessage());
        }
    }

    // Cuerpo HTML del mail de "en camino": nombre del cliente, número de pedido,
    // tiempo estimado de viaje, dirección de entrega y, si está disponible, la hora estimada de llegada
    private String construirCuerpoEnCamino(DTOPedido pedidoDTO, Integer tiempoViaje) {
        String nombreCliente = textoOGuion(pedidoDTO.getNombreCliente());
        String direccionEntrega = formatearDireccion(pedidoDTO.getDireccionEntrega());

        String tiempoMostrado = (tiempoViaje != null && tiempoViaje > 0)
                ? tiempoViaje + " min"
                : "—";

        LocalDateTime horaEntrega = pedidoDTO.getHoraEntregaEstimada();
        String horaEntregaMostrada = horaEntrega != null
                ? horaEntrega.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : null;

        StringBuilder sb = new StringBuilder();

        sb.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e0e0e0;'>");
        sb.append("<div style='background-color: #FF6600; padding: 12px; text-align: center;'>");
        sb.append("<img src='https://tu-dominio.com/images/logo.png' alt='Trego' style='height: 50px;'/>");
        sb.append("</div>");

        sb.append("<div style='padding: 32px;'>");
        sb.append("<h2 style='color: #333;'>").append(nombreCliente)
                .append(", tu pedido está en camino</h2>");

        sb.append("<p style='color: #555; font-size: 16px;'>")
                .append("Tu pedido <strong>#")
                .append(pedidoDTO.getIdPedido())
                .append("</strong> ya salió del restaurante y se dirige a tu dirección.")
                .append("</p>");

        sb.append("<div style='background-color: #f9f9f9; border-left: 4px solid #FF6600; padding: 12px 16px; color: #555; margin: 16px 0;'>")
                .append("<p style='margin: 0;'><strong>Tiempo estimado de viaje:</strong> ")
                .append(tiempoMostrado)
                .append("</p>");
        if (horaEntregaMostrada != null) {
            sb.append("<p style='margin: 8px 0 0 0;'><strong>Llegada aproximada:</strong> ")
                    .append(horaEntregaMostrada)
                    .append("</p>");
        }
        sb.append("</div>");

        sb.append("<p style='color: #555;'><strong>Dirección de entrega:</strong> ")
                .append(direccionEntrega)
                .append("</p>");

        sb.append("<p style='color: #555;'>Podés seguir el recorrido en tiempo real desde la app.</p>");
        sb.append("<hr style='border: none; border-top: 1px solid #e0e0e0; margin: 24px 0;'>");
        sb.append("<p style='color: #555;'><strong>El equipo de Trego</strong></p>");
        sb.append("</div>");

        sb.append("<div style='background-color: #f5f5f5; padding: 16px; text-align: center;'>");
        sb.append("<p style='color: #999; font-size: 12px; margin: 0;'>© 2026 Trego. Todos los derechos reservados.</p>");
        sb.append("</div>");
        sb.append("</div>");

        return sb.toString();
    }

    // Notifica al cliente que su pedido fue entregado. Igual que las demás
    // notificaciones de pedido, los errores se loguean sin propagar para no
    // bloquear el cambio de estado.
    @Async
    public void notificarPedidoEntregado(DTOPedido pedidoDTO) {
        if (pedidoDTO == null) {
            System.err.println("[Notificacion] pedidoDTO nulo, se omite el envio.");
            return;
        }

        Integer idCliente = pedidoDTO.getIdCliente();
        if (idCliente == null) {
            System.err.println("[Notificacion] Pedido " + pedidoDTO.getIdPedido()
                    + " sin idCliente, se omite el envio.");
            return;
        }

        Optional<Cliente> clienteOpt = usuarioRepository.findClienteById(idCliente);
        if (clienteOpt.isEmpty() || clienteOpt.get().getEmail() == null
                || clienteOpt.get().getEmail().isBlank()) {
            System.err.println("[Notificacion] Cliente " + idCliente
                    + " no encontrado o sin email; pedido " + pedidoDTO.getIdPedido());
            return;
        }
        Cliente cliente = clienteOpt.get();

        try {
            MimeMessage mail = mailSender.createMimeMessage();
            MimeMessageHelper estructuraMail = new MimeMessageHelper(mail, false, "UTF-8");

            estructuraMail.setTo(cliente.getEmail());
            estructuraMail.setFrom(mailFrom, mailFromName);
            estructuraMail.setSubject("Tu pedido #" + pedidoDTO.getIdPedido() + " fue entregado");
            estructuraMail.setText(construirCuerpoEntregado(pedidoDTO), true);

            mailSender.send(mail);
            System.out.println("Mail de entrega enviado al cliente: " + cliente.getEmail()
                    + " (pedido " + pedidoDTO.getIdPedido() + ")");
        } catch (Exception e) {
            System.err.println("Error al enviar mail de entrega del pedido "
                    + pedidoDTO.getIdPedido() + ": " + e.getMessage());
        }
    }

    // Cuerpo HTML del mail de entrega: confirma la entrega, muestra el total,
    // la dirección y la hora real de entrega (se usa LocalDateTime.now() porque
    // el DTO no transporta la marca de tiempo del cambio de estado).
    private String construirCuerpoEntregado(DTOPedido pedidoDTO) {
        String nombreCliente = textoOGuion(pedidoDTO.getNombreCliente());
        String direccionEntrega = formatearDireccion(pedidoDTO.getDireccionEntrega());
        String horaEntrega = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        StringBuilder sb = new StringBuilder();

        sb.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e0e0e0;'>");
        sb.append("<div style='background-color: #FF6600; padding: 12px; text-align: center;'>");
        sb.append("<img src='https://tu-dominio.com/images/logo.png' alt='Trego' style='height: 50px;'/>");
        sb.append("</div>");

        sb.append("<div style='padding: 32px;'>");
        sb.append("<h2 style='color: #333;'>").append(nombreCliente)
                .append(", tu pedido fue entregado</h2>");

        sb.append("<p style='color: #555; font-size: 16px;'>")
                .append("Confirmamos la entrega de tu pedido <strong>#")
                .append(pedidoDTO.getIdPedido())
                .append("</strong>. ¡Que lo disfrutes!")
                .append("</p>");

        sb.append("<div style='background-color: #f9f9f9; border-left: 4px solid #FF6600; padding: 12px 16px; color: #555; margin: 16px 0;'>")
                .append("<p style='margin: 0;'><strong>Entregado:</strong> ")
                .append(horaEntrega)
                .append("</p>")
                .append("<p style='margin: 8px 0 0 0;'><strong>Dirección:</strong> ")
                .append(direccionEntrega)
                .append("</p>");
        if (pedidoDTO.getTotal() != null) {
            sb.append("<p style='margin: 8px 0 0 0;'><strong>Total:</strong> $")
                    .append(pedidoDTO.getTotal())
                    .append("</p>");
        }
        sb.append("</div>");

        sb.append("<p style='color: #555;'>Si algo no salió como esperabas, podés dejarnos tu comentario desde la app.</p>");
        sb.append("<hr style='border: none; border-top: 1px solid #e0e0e0; margin: 24px 0;'>");
        sb.append("<p style='color: #555;'>Gracias por elegirnos.</p>");
        sb.append("<p style='color: #555;'><strong>El equipo de Trego</strong></p>");
        sb.append("</div>");

        sb.append("<div style='background-color: #f5f5f5; padding: 16px; text-align: center;'>");
        sb.append("<p style='color: #999; font-size: 12px; margin: 0;'>© 2026 Trego. Todos los derechos reservados.</p>");
        sb.append("</div>");
        sb.append("</div>");

        return sb.toString();
    }

    // Manda el correo de confirmación con el cuerpo HTML y el comprobante en PDF.
    @Async
    public void notificarConfirmacionPedidoConPDF(Usuario usuario, List<Producto> productos, Restaurante restaurante,
            Pedido pedido) {
        try {
            System.out.println("Enviando mail en hilo: " + Thread.currentThread().getName());
            MimeMessage mailConPDF = mailSender.createMimeMessage();
            // multipart = true para poder adjuntar el PDF
            MimeMessageHelper estructuraMail = new MimeMessageHelper(mailConPDF, true, "UTF-8");

            estructuraMail.setTo(usuario.getEmail());
            estructuraMail.setFrom(mailFrom, mailFromName);
            estructuraMail.setSubject("Confirmación de tu pedido #" + pedido.getIdPedido());

            estructuraMail.setText(construirCuerpoHtml(usuario, productos, restaurante, pedido), true);

            byte[] pdf = generarPDF.generarComprobante(usuario, productos, restaurante, pedido);
            estructuraMail.addAttachment(
                    "comprobante-" + pedido.getIdPedido() + ".pdf",
                    new ByteArrayResource(pdf),
                    "application/pdf");

            mailSender.send(mailConPDF);
            System.out.println("Mail enviado con éxito al usuario: " + usuario.getEmail());
        } catch (Exception e) {
            System.err.println("Error al enviar mail con PDF: " + e.getMessage());
        }
    }

    // Notifica al restaurante que su solicitud de alta fue aprobada y ya está
    // habilitado para operar en la plataforma.
    @Async
    public void notificarRestauranteHabilitado(Restaurante restaurante) {
        try {
            MimeMessage mail = mailSender.createMimeMessage();
            MimeMessageHelper estructuraMail = new MimeMessageHelper(mail, false, "UTF-8");

            estructuraMail.setTo(restaurante.getEmail());
            estructuraMail.setFrom(mailFrom, mailFromName);
            estructuraMail.setSubject("Tu restaurante ya está habilitado en Trego");
            estructuraMail.setText(construirCuerpoHabilitacion(restaurante), true);

            mailSender.send(mail);
            System.out.println("Mail de habilitación enviado al restaurante: " + restaurante.getEmail());
        } catch (Exception e) {
            System.err.println("Error al enviar mail de habilitación: " + e.getMessage());
        }
    }

    private String construirCuerpoHabilitacion(Restaurante restaurante) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e0e0e0;'>");
        sb.append("<div style='background-color: #FF6600; padding: 12px; text-align: center;'>");
        sb.append("<img src='https://tu-dominio.com/images/logo.png' alt='Trego' style='height: 50px;'/>");
        sb.append("</div>");

        sb.append("<div style='padding: 32px;'>");
        sb.append("<h2 style='color: #333;'>¡").append(restaurante.getNombre())
                .append(", tu solicitud fue aprobada!</h2>");
        sb.append("<p style='color: #555; font-size: 16px;'>")
                .append("Tu restaurante ya se encuentra habilitado en Trego y es visible para los clientes. ")
                .append("Ya podés ingresar al panel para cargar tu menú, promociones y comenzar a recibir pedidos.")
                .append("</p>");
        sb.append("<hr style='border: none; border-top: 1px solid #e0e0e0; margin: 24px 0;'>");
        sb.append("<p style='color: #555;'>¡Bienvenido a bordo!</p>");
        sb.append("<p style='color: #555;'><strong>El equipo de Trego</strong></p>");
        sb.append("</div>");

        sb.append("<div style='background-color: #f5f5f5; padding: 16px; text-align: center;'>");
        sb.append("<p style='color: #999; font-size: 12px; margin: 0;'>© 2026 Trego. Todos los derechos reservados.</p>");
        sb.append("</div>");
        sb.append("</div>");
        return sb.toString();
    }

    // Notifica al restaurante que su solicitud de alta fue rechazada, incluyendo
    // el motivo informado por el administrador.
    @Async
    public void notificarRestauranteNoHabilitado(Restaurante restaurante, String motivo) {
        try {
            MimeMessage mail = mailSender.createMimeMessage();
            MimeMessageHelper estructuraMail = new MimeMessageHelper(mail, false, "UTF-8");

            estructuraMail.setTo(restaurante.getEmail());
            estructuraMail.setFrom(mailFrom, mailFromName);
            estructuraMail.setSubject("Tu solicitud de alta en Trego no fue aprobada");
            estructuraMail.setText(construirCuerpoNoHabilitacion(restaurante, motivo), true);

            mailSender.send(mail);
            System.out.println("Mail de rechazo enviado al restaurante: " + restaurante.getEmail());
        } catch (Exception e) {
            System.err.println("Error al enviar mail de rechazo: " + e.getMessage());
        }
    }

    private String construirCuerpoNoHabilitacion(Restaurante restaurante, String motivo) {
        String motivoMostrado = (motivo == null || motivo.isBlank())
                ? "No se especificó un motivo."
                : motivo;

        StringBuilder sb = new StringBuilder();
        sb.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e0e0e0;'>");
        sb.append("<div style='background-color: #FF6600; padding: 12px; text-align: center;'>");
        sb.append("<img src='https://tu-dominio.com/images/logo.png' alt='Trego' style='height: 50px;'/>");
        sb.append("</div>");

        sb.append("<div style='padding: 32px;'>");
        sb.append("<h2 style='color: #333;'>Hola ").append(restaurante.getNombre()).append(",</h2>");
        sb.append("<p style='color: #555; font-size: 16px;'>")
                .append("Lamentamos informarte que tu solicitud de alta en Trego no fue aprobada en esta instancia.")
                .append("</p>");
        sb.append("<p style='color: #555; font-size: 16px;'><strong>Motivo:</strong></p>");
        sb.append("<div style='background-color: #f9f9f9; border-left: 4px solid #FF6600; padding: 12px 16px; color: #555;'>")
                .append(motivoMostrado)
                .append("</div>");
        sb.append("<p style='color: #555; margin-top: 24px;'>")
                .append("Podés corregir la información indicada y volver a postular tu local. ")
                .append("Si tenés dudas sobre el motivo, respondé a este correo y te ayudamos.")
                .append("</p>");
        sb.append("<hr style='border: none; border-top: 1px solid #e0e0e0; margin: 24px 0;'>");
        sb.append("<p style='color: #555;'><strong>El equipo de Trego</strong></p>");
        sb.append("</div>");

        sb.append("<div style='background-color: #f5f5f5; padding: 16px; text-align: center;'>");
        sb.append("<p style='color: #999; font-size: 12px; margin: 0;'>© 2026 Trego. Todos los derechos reservados.</p>");
        sb.append("</div>");
        sb.append("</div>");
        return sb.toString();
    }

    // Envía un código de verificación por email y devuelve el código generado.
    // No es @Async: el registro del restaurante necesita el código de vuelta al instante.
    public String codigoVerificacionEmail(String email) {
        String codigoGenerado = String.valueOf(100000 + new Random().nextInt(900000));
        if (mailLogOnly) {
            System.out.println("[DEV mail-log-only] Código de verificación para " + email + ": " + codigoGenerado);
            return codigoGenerado;
        }
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom(mailFrom);
            mensaje.setTo(email);
            mensaje.setSubject("Código de Verificación - Trego");
            mensaje.setText("Tu código de verificación es: " + codigoGenerado);

            mailSender.send(mensaje);
            return codigoGenerado;
        } catch (Exception e) {
            throw new RuntimeException("Error al enviar código de verificación", e);
        }
    }

    // Arma el HTML del correo. Tolera campos faltantes: si el restaurante todavía
    // no completó su perfil o el pago no llegó con todos los datos, se imprime
    // un guión en lugar de "null".
    private String construirCuerpoHtml(Usuario usuario, List<Producto> productos, Restaurante restaurante,
            Pedido pedido) {
        String nombreCliente = textoOGuion(usuario != null ? usuario.getNombre() : null);
        String nombreRestaurante = textoOGuion(restaurante != null ? restaurante.getNombre() : null);
        String direccionRestaurante = formatearDireccion(restaurante != null ? restaurante.getDireccion() : null);

        Pago pago = pedido.getPago();
        // idTransaccion guarda el id que devuelve MercadoPago (no el id interno
        // del Pago), que es el dato útil para referenciar el pago ante MP.
        String idTransaccion = pago != null ? textoOGuion(pago.getIdTransaccion()) : "—";
        String tarjeta = pago != null ? textoOGuion(pago.getNroUltimDigTarjeta()) : "—";

        StringBuilder sb = new StringBuilder();

        sb.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e0e0e0;'>");
        sb.append("<div style='background-color: #FF6600; padding: 12px; text-align: center;'>");
        // OJO: reemplazar por una URL pública real (servidor o Firebase Storage)
        sb.append("<img src='https://tu-dominio.com/images/logo.png' alt='Trego' style='height: 50px;'/>");
        sb.append("</div>");

        sb.append("<div style='padding: 32px;'>");
        sb.append("<h2 style='color: #333;'>¡")
                .append(nombreCliente)
                .append(", gracias por tu pedido!</h2>");

        sb.append("<p style='color: #555; font-size: 16px;'>")
                .append("<strong>")
                .append(nombreRestaurante)
                .append("</strong> - ")
                .append(direccionRestaurante)
                .append(" ha recibido tu pedido #")
                .append(pedido.getIdPedido())
                .append(" con los siguientes productos:")
                .append("</p>");

        sb.append("<ul>");
        pedido.getProductos().forEach(pp -> {
            String nombreProducto = pp.getProducto() != null
                    ? textoOGuion(pp.getProducto().getNombre())
                    : "—";
            sb.append("<li>")
                    .append(nombreProducto)
                    .append(" x ")
                    .append(pp.getCantidad())
                    .append("</li>");
        });
        sb.append("</ul>");

        sb.append("<p style='color: #555; font-size: 16px;'>")
                .append("<strong>Total:</strong> $")
                .append(pedido.getTotal())
                .append("<br>")
                .append("<strong>Transacción:</strong> ")
                .append(idTransaccion)
                .append("<br>")
                .append("<strong>Tarjeta:</strong> ****")
                .append(tarjeta)
                .append("</p>");
        sb.append("</div>");

        sb.append("<p style='color: #555;'>Podrás seguir el estado de tu pedido desde la app.</p>");
        sb.append("<hr style='border: none; border-top: 1px solid #e0e0e0; margin: 24px 0;'>");
        sb.append("<p style='color: #555;'>¡Que tengas un buen día!</p>");
        sb.append("<p style='color: #555;'><strong>El equipo de Trego</strong></p>");
        sb.append("</div>");

        sb.append("<div style='background-color: #f5f5f5; padding: 16px; text-align: center;'>");
        sb.append("<p style='color: #999; font-size: 12px; margin: 0;'>© 2026 Trego. Todos los derechos reservados.</p>");
        sb.append("</div>");
        sb.append("</div>");

        return sb.toString();
    }

    private static String textoOGuion(String valor) {
        return (valor == null || valor.isBlank()) ? "—" : valor;
    }

    private static String formatearDireccion(DTODireccion direccion) {
        return direccion == null ? "—" : direccion.toString();
    }

    // Notificaciones push
    // ============================================================

    private boolean enviarPushFCM(String token, String titulo, String cuerpo, Map<String, String> data) {
        if (token == null || token.isBlank()) {
            System.err.println("[Push] Token FCM vacío, se omite el envío.");
            return false;
        }
        try {
            // Inicializamos el mapa si viene nulo
            Map<String, String> dataMap = (data != null) ? new HashMap<>(data) : new HashMap<>();

            // IMPORTANTE: Metemos el título y cuerpo dentro del mapa DATA
            dataMap.put("title", titulo);
            dataMap.put("body", cuerpo);

            // Configuración para asegurar prioridad alta (despertar al dispositivo)
            com.google.firebase.messaging.AndroidConfig androidConfig = com.google.firebase.messaging.AndroidConfig
                    .builder()
                    .setPriority(com.google.firebase.messaging.AndroidConfig.Priority.HIGH)
                    .build();

            Message message = Message.builder()
                    .setToken(token)
                    .putAllData(dataMap) // Enviamos el contenido como data y no notificacion
                    .setAndroidConfig(androidConfig)
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("[Push Data] Enviado a token=" + acortar(token)
                    + " messageId=" + response + " titulo='" + titulo + "'");
            return true;
        } catch (Exception e) {
            System.err.println("[Push Data] Error: " + e.getMessage());
            return false;
        }
    }

    // Resuelve el token FCM del cliente dueño del pedido. Devuelve null si el
    // cliente no se puede ubicar o no registró todavía un dispositivo.
    private String obtenerTokenCliente(DTOPedido pedidoDTO) {
        if (pedidoDTO == null || pedidoDTO.getIdCliente() == null) {
            return null;
        }
        Optional<Cliente> clienteOpt = usuarioRepository.findClienteById(pedidoDTO.getIdCliente());
        if (clienteOpt.isEmpty()) {
            System.err.println("[Push] Cliente " + pedidoDTO.getIdCliente()
                    + " no encontrado; pedido " + pedidoDTO.getIdPedido());
            return null;
        }
        String token = clienteOpt.get().getFcmToken();
        if (token == null || token.isBlank()) {
            System.err.println("[Push] Cliente " + pedidoDTO.getIdCliente()
                    + " sin token FCM; pedido " + pedidoDTO.getIdPedido());
            return null;
        }
        return token;
    }

    // Construye el payload data común a todas las notificaciones de cambio de
    // estado: idPedido y estado, suficientes para que la app abra el detalle.
    private Map<String, String> dataPedido(DTOPedido pedidoDTO, String estado) {
        Map<String, String> data = new HashMap<>();
        if (pedidoDTO != null && pedidoDTO.getIdPedido() != null) {
            data.put("idPedido", String.valueOf(pedidoDTO.getIdPedido()));
        }
        data.put("estado", estado);
        data.put("tipo", "ESTADO_PEDIDO");
        return data;
    }

    private static String acortar(String token) {
        if (token == null || token.length() <= 12) {
            return token;
        }
        return token.substring(0, 6) + "…" + token.substring(token.length() - 4);
    }

    // Notificacion push para que el frontend pueda escuchar que el estado del
    // pedido cambio Estado_pagado es lo que esta esperando el front (Mobile) para
    // actualizar la lista de los pedidos confirmados
    public void notificarPushPagoProcesado(DTOPedido pedidoDTO) {
        String token = obtenerTokenCliente(pedidoDTO);
        if (token == null) {
            System.err.println("[Push] No se pudo obtener el token para el pedido confirmado.");
            return;
        }

        String titulo = "¡Pago Completado!";
        String cuerpo = "Tu pago fue exitoso y el restaurante ya tiene tu orden.";

        Map<String, String> data = dataPedido(pedidoDTO, "PAGO_PROCESADO");

        enviarPushFCM(token, titulo, cuerpo, data);
    }

    public void notificarPushEnPreparacion(DTOPedido pedidoDTO, Integer tiempoEstimado) {
        String token = obtenerTokenCliente(pedidoDTO);
        if (token == null) {
            return;
        }
        String titulo = "Tu pedido está en preparación";
        String cuerpo = (tiempoEstimado != null && tiempoEstimado > 0)
                ? "El restaurante ya está preparando tu pedido. Tiempo estimado: " + tiempoEstimado + " min."
                : "El restaurante ya está preparando tu pedido.";
        enviarPushFCM(token, titulo, cuerpo, dataPedido(pedidoDTO, "EnPreparacion"));
    }

    public void notificarPushEnCamino(DTOPedido pedidoDTO, Integer tiempoViaje) {
        String token = obtenerTokenCliente(pedidoDTO);
        if (token == null) {
            return;
        }
        String titulo = "Tu pedido está en camino";
        String cuerpo = (tiempoViaje != null && tiempoViaje > 0)
                ? "Tu pedido salió del restaurante. Llega en aprox. " + tiempoViaje + " min."
                : "Tu pedido salió del restaurante.";
        enviarPushFCM(token, titulo, cuerpo, dataPedido(pedidoDTO, "EnCamino"));
    }

    public void notificarPushEntregado(DTOPedido pedidoDTO) {
        String token = obtenerTokenCliente(pedidoDTO);
        if (token == null) {
            return;
        }
        String titulo = "Tu pedido fue entregado";
        String cuerpo = "Confirmamos la entrega de tu pedido. ¡Que lo disfrutes!";
        enviarPushFCM(token, titulo, cuerpo, dataPedido(pedidoDTO, "Entregado"));
    }

    private String construirCuerpoNoHabilitacionUsuario(String nombre, String motivo) {
        String motivoMostrado = (motivo == null || motivo.isBlank())
                ? "No se especificó un motivo."
                : motivo;

        StringBuilder sb = new StringBuilder();
        sb.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e0e0e0;'>");
        sb.append("<div style='background-color: #FF6600; padding: 12px; text-align: center;'>");
        sb.append("<img src='https://tu-dominio.com/images/logo.png' alt='Trego' style='height: 50px;'/>");
        sb.append("</div>");
        sb.append("<div style='padding: 32px;'>");
        sb.append("<h2 style='color: #333;'>Hola ").append(nombre).append(",</h2>");
        sb.append("<p style='color: #555; font-size: 16px;'>")
                .append("Tu cuenta en Trego ha sido deshabilitada por un administrador.")
                .append("</p>");
        sb.append("<p style='color: #555; font-size: 16px;'><strong>Motivo:</strong></p>");
        sb.append("<div style='background-color: #f9f9f9; border-left: 4px solid #FF6600; padding: 12px 16px; color: #555;'>")
                .append(motivoMostrado)
                .append("</div>");
        sb.append("<p style='color: #555; margin-top: 24px;'>")
                .append("Si creés que esto es un error, podés contactarnos respondiendo a este correo.")
                .append("</p>");
        sb.append("<hr style='border: none; border-top: 1px solid #e0e0e0; margin: 24px 0;'>");
        sb.append("<p style='color: #555;'><strong>El equipo de Trego</strong></p>");
        sb.append("</div>");
        sb.append("<div style='background-color: #f5f5f5; padding: 16px; text-align: center;'>");
        sb.append("<p style='color: #999; font-size: 12px; margin: 0;'>© 2026 Trego. Todos los derechos reservados.</p>");
        sb.append("</div>");
        sb.append("</div>");
        return sb.toString();
    }

    @Async
    public void notificarNoHabilitacionUsuario(String emailDestino, String nombre, String motivo) {
        try {
            MimeMessage mail = mailSender.createMimeMessage();
            MimeMessageHelper estructuraMail = new MimeMessageHelper(mail, false, "UTF-8");

            estructuraMail.setTo(emailDestino);
            estructuraMail.setFrom(mailFrom, mailFromName);
            estructuraMail.setSubject("Tu cuenta en Trego ha sido deshabilitada");
            estructuraMail.setText(construirCuerpoNoHabilitacionUsuario(nombre, motivo), true);

            mailSender.send(mail);
            System.out.println("Mail de deshabilitación enviado a: " + emailDestino);
        } catch (Exception e) {
            System.err.println("Error al enviar mail de deshabilitación: " + e.getMessage());
        }
    }

    // Construye el cuerpo HTML del correo de credenciales según el tipo de envío
    private String construirCuerpoCredenciales(String email, String password, TipoCredencial tipo) {
        String titulo;
        String descripcion;

        if (tipo == TipoCredencial.NUEVO_ADMIN) {
            titulo = "¡Bienvenido al equipo de administración de Trego!";
            descripcion = "Se ha creado una cuenta de administrador para ti. A continuación, tus credenciales de acceso iniciales:";
        } else {
            titulo = "Recuperación de contraseña";
            descripcion = "Generamos una nueva contraseña temporal para tu cuenta. Podés usarla para iniciar sesión:";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e0e0e0;'>");
        sb.append("<div style='background-color: #FF6600; padding: 12px; text-align: center;'>");
        sb.append("<img src='https://www.trego.com/images/logo.png' alt='Trego' style='height: 50px;'/>");
        sb.append("</div>");
        sb.append("<div style='padding: 32px;'>");
        sb.append("<h2 style='color: #333;'>").append(titulo).append("</h2>");
        sb.append("<p style='color: #555; font-size: 16px;'>").append(descripcion).append("</p>");
        sb.append("<div style='background-color: #f9f9f9; border-left: 4px solid #FF6600; padding: 16px; margin: 20px 0;'>");
        sb.append("<p style='color: #333; margin: 0 0 10px 0;'><strong>Usuario (Email):</strong> ").append(email)
                .append("</p>");
        sb.append("<p style='color: #333; margin: 0;'><strong>Contraseña:</strong> ").append(password).append("</p>");
        sb.append("</div>");
        sb.append("<p style='color: #555; font-size: 14px;'>")
                .append("<em>Por motivos de seguridad, te recomendamos iniciar sesión y cambiar esta contraseña lo antes posible.</em>")
                .append("</p>");
        sb.append("<hr style='border: none; border-top: 1px solid #e0e0e0; margin: 24px 0;'>");
        sb.append("<p style='color: #555;'><strong>El equipo de Trego</strong></p>");
        sb.append("</div>");
        sb.append("<div style='background-color: #f5f5f5; padding: 16px; text-align: center;'>");
        sb.append("<p style='color: #999; font-size: 12px; margin: 0;'>© 2026 Trego. Todos los derechos reservados.</p>");
        sb.append("</div>");
        sb.append("</div>");

        return sb.toString();
    }

    @Async
    public void notificarCredencialesNuevoAdmin(String emailDestino, String passwordPlana) {
        try {
            MimeMessage mail = mailSender.createMimeMessage();
            MimeMessageHelper estructuraMail = new MimeMessageHelper(mail, false, "UTF-8");

            estructuraMail.setTo(emailDestino);
            estructuraMail.setFrom(mailFrom, mailFromName);
            estructuraMail.setSubject("Bienvenido a Trego - Credenciales de Administrador");
            estructuraMail.setText(construirCuerpoCredenciales(emailDestino, passwordPlana, TipoCredencial.NUEVO_ADMIN),
                    true);
            mailSender.send(mail);
            System.out.println("Mail de credenciales enviado al nuevo administrador: " + emailDestino);
        } catch (Exception e) {
            System.err.println("Error al enviar mail de credenciales a administrador: " + e.getMessage());
        }
    }

    public void notificarResolucionReclamo(Pedido pedido) {
        if (pedido == null || pedido.getReclamo() == null || pedido.getCliente() == null) {
            System.err.println("[Notificacion] Pedido, reclamo o cliente nulo; se omite notificación de reclamo.");
            return;
        }

        Cliente cliente = pedido.getCliente();
        Reclamo reclamo = pedido.getReclamo();

        // Email
        if (cliente.getEmail() != null && !cliente.getEmail().isBlank()) {
            try {
                MimeMessage mail = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mail, false, "UTF-8");
                helper.setTo(cliente.getEmail());
                helper.setFrom(mailFrom, mailFromName);
                helper.setSubject("Actualización de tu reclamo - Pedido #" + pedido.getIdPedido());
                helper.setText(construirCuerpoReclamo(pedido, reclamo), true);
                mailSender.send(mail);
                System.out.println("[Notificacion] Mail de reclamo enviado a " + cliente.getEmail()
                        + " (pedido " + pedido.getIdPedido() + ")");
            } catch (Exception e) {
                System.err.println("[Notificacion] Error al enviar mail de reclamo (pedido "
                        + pedido.getIdPedido() + "): " + e.getMessage());
            }
        }

        // Push
        String token = cliente.getFcmToken();
        if (token != null && !token.isBlank()) {
            boolean aceptado = reclamo.getEstado() == EnumEstadoReclamo.Resuelto;
            String titulo = aceptado
                    ? "Tu reclamo fue aceptado"
                    : "Tu reclamo fue rechazado";
            String cuerpo = aceptado
                    ? "Se procesó el reintegro de $" + pedido.getTotal()
                            + " por tu pedido #" + pedido.getIdPedido() + "."
                    : "Tu reclamo sobre el pedido #" + pedido.getIdPedido() + " fue rechazado.";

            Map<String, String> data = new HashMap<>();
            data.put("idPedido", String.valueOf(pedido.getIdPedido()));
            data.put("idReclamo", String.valueOf(reclamo.getIdReclamo()));
            data.put("estado", reclamo.getEstado().name());
            data.put("estadoFinal", reclamo.getEstado().name());
            data.put("motivoReclamo", textoOGuion(reclamo.getTexto()));
            if (aceptado) {
                data.put("detalleReintegro",
                        "Reintegro de $" + pedido.getTotal() + " por el pedido #" + pedido.getIdPedido());
            } else if (reclamo.getMotivoRechazo() != null) {
                data.put("motivoRechazo", reclamo.getMotivoRechazo());
            }
            data.put("tipo", "RECLAMO");
            enviarPushFCM(token, titulo, cuerpo, data);
        }
    }

    private String construirCuerpoReclamo(Pedido pedido, Reclamo reclamo) {
        boolean aceptado = reclamo.getEstado() == EnumEstadoReclamo.Resuelto;
        String nombreCliente = textoOGuion(pedido.getCliente() != null ? pedido.getCliente().getNombre() : null);
        String estadoTexto = aceptado ? "Aceptado" : "Rechazado";
        String colorEstado = aceptado ? "#2e7d32" : "#c62828";

        StringBuilder sb = new StringBuilder();
        sb.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e0e0e0;'>");
        sb.append("<div style='background-color: #FF6600; padding: 12px; text-align: center;'>");
        sb.append("<img src='https://tu-dominio.com/images/logo.png' alt='Trego' style='height: 50px;'/>");
        sb.append("</div>");

        sb.append("<div style='padding: 32px;'>");
        sb.append("<h2 style='color: #333;'>Hola ").append(nombreCliente).append(",</h2>");
        sb.append("<p style='color: #555; font-size: 16px;'>")
                .append("Tu reclamo sobre el pedido <strong>#").append(pedido.getIdPedido())
                .append("</strong> fue procesado.")
                .append("</p>");

        sb.append("<div style='background-color: #f9f9f9; border-left: 4px solid ").append(colorEstado)
                .append("; padding: 12px 16px; color: #555; margin: 16px 0;'>");
        sb.append("<p style='margin: 0;'><strong>Estado:</strong> <span style='color: ").append(colorEstado)
                .append(";'>").append(estadoTexto).append("</span></p>");

        sb.append("<p style='margin: 8px 0 0 0;'><strong>Motivo del reclamo:</strong> ")
                .append(textoOGuion(reclamo.getTexto())).append("</p>");

        if (aceptado) {
            sb.append("<p style='margin: 8px 0 0 0;'><strong>Detalle del reintegro:</strong> ")
                    .append("Se procesó un reintegro de <strong>$").append(pedido.getTotal())
                    .append("</strong> correspondiente al monto del pedido.")
                    .append("</p>");
        } else {
            sb.append("<p style='margin: 8px 0 0 0;'><strong>Motivo del rechazo:</strong> ")
                    .append(textoOGuion(reclamo.getMotivoRechazo())).append("</p>");
        }

        sb.append("</div>");
        sb.append("<p style='color: #555;'>Si tenés dudas, podés contactarnos desde la app o respondiendo este correo.</p>");
        sb.append("<hr style='border: none; border-top: 1px solid #e0e0e0; margin: 24px 0;'>");
        sb.append("<p style='color: #555;'><strong>El equipo de Trego</strong></p>");
        sb.append("</div>");

        sb.append("<div style='background-color: #f5f5f5; padding: 16px; text-align: center;'>");
        sb.append("<p style='color: #999; font-size: 12px; margin: 0;'>© 2026 Trego. Todos los derechos reservados.</p>");
        sb.append("</div>");
        sb.append("</div>");

        return sb.toString();
    }

    @Async
    public void notificarRecuperacionContraseña(String emailDestino, String passwordPlana) {
        try {
            MimeMessage mail = mailSender.createMimeMessage();
            MimeMessageHelper estructuraMail = new MimeMessageHelper(mail, false, "UTF-8");

            estructuraMail.setTo(emailDestino);
            estructuraMail.setFrom(mailFrom, mailFromName);
            estructuraMail.setSubject("Recuperación de contraseña - Trego");
            estructuraMail.setText(
                    construirCuerpoCredenciales(emailDestino, passwordPlana, TipoCredencial.RECUPERACION), true);
            mailSender.send(mail);
            System.out.println("Mail de recuperación de contraseña enviado a: " + emailDestino);
        } catch (Exception e) {
            System.err.println("Error al enviar mail de recuperación de contraseña: " + e.getMessage());
        }
    }

}
