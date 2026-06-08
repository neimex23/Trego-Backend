package com.backend.trego.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.backend.trego.entity.DTOs.DTOFirma;
import com.cloudinary.Cloudinary;

// Genera firmas para que el frontend suba archivos directamente a Cloudinary
// usando el modo "signed upload" (la api_secret nunca sale del backend).
@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    // Firma la subida de un archivo identificado por su nombre y tipo (resource_type).
    // tipo admite: "image", "video" o "raw"; si es null o desconocido se asume "image".
    public DTOFirma firmar(String nombreArchivo, String tipo) {
        String resourceType = normalizarTipo(tipo);
        String publicId = construirPublicId(nombreArchivo);
        long timestamp = System.currentTimeMillis() / 1000L;

        // Parametros que el frontend debera reenviar junto con la firma.
        // El resource_type forma parte de la URL, no de los parametros firmados.
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("timestamp", timestamp);
        parametros.put("public_id", publicId);

        String firma = cloudinary.apiSignRequest(parametros, cloudinary.config.apiSecret);

        String uploadUrl = String.format(
                "https://api.cloudinary.com/v1_1/%s/%s/upload",
                cloudName,
                resourceType);

        return new DTOFirma(firma, timestamp, apiKey, cloudName, uploadUrl, publicId);
    }

    private String normalizarTipo(String tipo) {
        if (tipo == null) {
            return "image";
        }
        String t = tipo.trim().toLowerCase();
        return switch (t) {
            case "image", "video", "raw" -> t;
            default -> "image";
        };
    }

    // Deriva un public_id estable a partir del nombre, sin la extension.
    private String construirPublicId(String nombreArchivo) {
        if (nombreArchivo == null || nombreArchivo.isBlank()) {
            return "archivo_" + System.currentTimeMillis();
        }
        String nombre = nombreArchivo.trim();
        int punto = nombre.lastIndexOf('.');
        if (punto > 0) {
            nombre = nombre.substring(0, punto);
        }
        return nombre;
    }
}
