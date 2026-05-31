package com.backend.trego.service;

import com.backend.trego.entity.Pago;
import com.backend.trego.entity.Pedido;
import com.backend.trego.entity.Producto;
import com.backend.trego.entity.Restaurante;
import com.backend.trego.entity.Usuario;
import com.backend.trego.entity.DTOs.DTODireccion;
// import com.backend.trego.entity.DTOs.DTOPedido;
// import com.backend.trego.entity.DTOs.DTORestaurante;

import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

// Envío de notificaciones a clientes y restaurantes (email, y a futuro push/SMS).
@Service
public class NotificacionesService {

    private final JavaMailSender mailSender;
    private final ManejadorPDFService generarPDF;

    @Value("${mail.from}")
    private String mailFrom;

    @Value("${mail.from.name}")
    private String mailFromName;

    public NotificacionesService(JavaMailSender mailSender, ManejadorPDFService generarPDF) {
        this.mailSender = mailSender;
        this.generarPDF = generarPDF;
    }

    // /**
    // * Notifica al cliente que su pedido fue confirmado, con el tiempo estimado.
    // */
    // public void notificarConfirmacionPedido(DTOPedido pedidoDTO, Integer
    // tiempoEstimado) {
    // // TODO: implementar
    // }

    // /**
    // * Notifica al cliente que su pedido ya está en camino.
    // */
    // public void notificarPedidoEnCamino(DTOPedido pedidoDTO, Integer tiempoViaje)
    // {
    // // TODO: implementar
    // }

    // /**
    // * Notifica al restaurante que su alta fue aprobada con éxito.
    // */
    // public void notificarAltaExitosa(DTORestaurante restauranteDTO) {
    // // TODO: implementar
    // }

    // public void solicitudRechazada(DTORestaurante restauranteDTO, String motivo)
    // {
    // // TODO: implementar
    // }

    // Manda el correo de confirmación con el cuerpo HTML y el comprobante en PDF.
    public void notificarConfirmacionPedidoConPDF(Usuario usuario, List<Producto> productos, Restaurante restaurante,
            Pedido pedido) {
        try {
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
        sb.append("<h2 style='color: #333;'>¡").append(restaurante.getNombre()).append(", tu solicitud fue aprobada!</h2>");
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
    public String codigoVerificacionEmail(String email) {
        String codigoGenerado = String.valueOf(100000 + new Random().nextInt(900000));
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

        sb.append(
                "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e0e0e0;'>");
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
        sb.append(
                "<p style='color: #999; font-size: 12px; margin: 0;'>© 2026 Trego. Todos los derechos reservados.</p>");
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
}
