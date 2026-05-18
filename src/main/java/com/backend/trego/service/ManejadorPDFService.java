package com.backend.trego.service;

import com.backend.trego.entity.Pedido;
import com.backend.trego.entity.Producto;
import com.backend.trego.entity.ProductoPedido;
import com.backend.trego.entity.Restaurante;
import com.backend.trego.entity.Usuario;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Service
public class ManejadorPDFService {
    // Colores
    private static final java.awt.Color NARANJA = new java.awt.Color(0xFF, 0x66, 0x00); // #FF6600
    private static final java.awt.Color BLANCO = java.awt.Color.WHITE;
    private static final java.awt.Color NEGRO = java.awt.Color.BLACK;

    // Fuentes
    private static final Font FUENTE_TITULO = new Font(Font.HELVETICA, 9, Font.BOLD, BLANCO);
    private static final Font FUENTE_CELDA = new Font(Font.HELVETICA, 9, Font.NORMAL, NEGRO);
    private static final Font FUENTE_TOTAL = new Font(Font.HELVETICA, 9, Font.BOLD, NEGRO);
    private static final Font FUENTE_EMPRESA = new Font(Font.HELVETICA, 9, Font.NORMAL, NEGRO);

    public byte[] generarComprobante(Usuario datos, java.util.List<Producto> productos,
            Restaurante restaurante, Pedido pedido) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            // SECCIÓN SUPERIOR: logo + tablas con informacion del pedido
            PdfPTable seccionSuperior = new PdfPTable(2);
            seccionSuperior.setWidthPercentage(100);
            seccionSuperior.setWidths(new float[] { 40f, 60f });

            // Columna izquierda: logo + datos empresa
            PdfPCell celdaIzquierda = new PdfPCell();
            celdaIzquierda.setBorder(Rectangle.NO_BORDER);
            celdaIzquierda.setPadding(4);

            try {
                ClassPathResource logoResource = new ClassPathResource("static/images/logo.png"); // Ajusta tu ruta
                                                                                                  // interna
                Image logo = Image.getInstance(logoResource.getURL());
                logo.scaleToFit(120, 120);
                celdaIzquierda.addElement(logo);
            } catch (Exception e) {
                celdaIzquierda.addElement(new Paragraph("TREGO", new Font(Font.HELVETICA, 14, Font.BOLD, NARANJA)));
            }

            celdaIzquierda.addElement(new Paragraph("TREGO S.A.", FUENTE_EMPRESA));
            celdaIzquierda.addElement(new Paragraph("Av. Gral. Rivera N° 3729", FUENTE_EMPRESA));
            celdaIzquierda.addElement(new Paragraph("Montevideo", FUENTE_EMPRESA));
            seccionSuperior.addCell(celdaIzquierda);

            // Columna derecha: tablas con informacion del pedido
            PdfPCell celdaDerecha = new PdfPCell();
            celdaDerecha.setBorder(Rectangle.NO_BORDER);
            celdaDerecha.setPadding(4);

            celdaDerecha.addElement(
                    crearTablaDobleColumna("RUT EMISOR", "TIPO DE DOCUMENTO", restaurante.getRut(), "e-Ticket"));
            celdaDerecha.addElement(crearTablaUnaColumna("NOMBRE", datos.getNombre()));
            celdaDerecha.addElement(crearTablaUnaColumna("DOMICILIO", pedido.getDireccionEntrega().toString()));
            celdaDerecha.addElement(crearTablaDobleColumna(
                    "FECHA", "MONEDA",
                    pedido.getHorarioEntrega().toString(), "UYU"));
            seccionSuperior.addCell(celdaDerecha);
            doc.add(seccionSuperior);
            doc.add(new Paragraph(" "));

            // TABLA DE PRODUCTOS
            PdfPTable tablaProductos = new PdfPTable(3);
            tablaProductos.setWidthPercentage(100);
            tablaProductos.setWidths(new float[] { 60f, 15f, 25f });

            tablaProductos.addCell(celdaTitulo("PRODUCTO"));
            tablaProductos.addCell(celdaTitulo("CANTIDAD"));
            tablaProductos.addCell(celdaTitulo("PRECIO UNITARIO"));

            BigDecimal total = BigDecimal.ZERO;
            for (ProductoPedido pp : pedido.getProductos()) {
                Producto p = pp.getProducto();
                int cantidad = pp.getCantidad();
                BigDecimal precioUnitario = new BigDecimal(String.valueOf(p.getPrecio()));
                BigDecimal subtotal = new BigDecimal(String.valueOf(pp.getPrecioSuma()));
                total = total.add(subtotal);

                tablaProductos.addCell(celdaNormal(p.getDescripcion()));
                tablaProductos.addCell(celdaNormal(String.valueOf(cantidad)));
                tablaProductos.addCell(celdaNormal("$" + precioUnitario));
            }

            PdfPCell celdaLabelTotal = new PdfPCell(new Phrase("TOTAL", FUENTE_TOTAL));
            celdaLabelTotal.setColspan(2);
            celdaLabelTotal.setPadding(6);
            tablaProductos.addCell(celdaLabelTotal);
            tablaProductos.addCell(celdaNormal("$" + total));
            doc.add(tablaProductos);

            doc.add(new Paragraph(" "));

            // TABLA NRO. DE PEDIDO / MÉTODO DE PAGO (Corrección del punto y coma aquí)
            doc.add(crearTablaDobleColumna(
                    "NRO. DE PEDIDO", "METODO DE PAGO",
                    String.valueOf(pedido.getIdPedido()), pedido.getPago().getMetodoDePago().name()));

            doc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF", e);
        }
    }

    private PdfPTable crearTablaUnaColumna(String titulo, String valor) {
        PdfPTable tabla = new PdfPTable(1);
        tabla.setWidthPercentage(100);
        tabla.setSpacingAfter(4);
        tabla.addCell(celdaTitulo(titulo));
        tabla.addCell(celdaNormal(valor));
        return tabla;
    }

    private PdfPTable crearTablaDobleColumna(String titulo1, String titulo2, String valor1, String valor2) {
        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100);
        tabla.setSpacingAfter(4);
        tabla.addCell(celdaTitulo(titulo1));
        tabla.addCell(celdaTitulo(titulo2));
        tabla.addCell(celdaNormal(valor1));
        tabla.addCell(celdaNormal(valor2));
        return tabla;
    }

    private PdfPCell celdaTitulo(String texto) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, FUENTE_TITULO));
        celda.setBackgroundColor(NARANJA);
        celda.setPadding(6);
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        return celda;
    }

    private PdfPCell celdaNormal(String texto) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, FUENTE_CELDA));
        celda.setPadding(6);
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        return celda;
    }
}