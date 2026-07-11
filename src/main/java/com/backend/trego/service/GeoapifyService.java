package com.backend.trego.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.backend.trego.entity.DTOs.DTODireccion;
import com.backend.trego.entity.DTOs.DTOGeoapifyProperties;
import com.backend.trego.entity.DTOs.DTOGeoapifyResponse;

@Service
public class GeoapifyService {

    @Value("${geoapify.api.key}")
    private String apiKey;

    // Modo de ruteo por defecto (delivery -> drive).
    private static final String MODO_RUTEO = "drive";

    private final RestTemplate restTemplate = new RestTemplate();

    public DTODireccion obtenerDireccion(double latitud, double longitud) {
        // Sin API key real (perfil dev) no se consulta el servicio.
        if (apiKey == null || apiKey.isBlank() || "dev".equalsIgnoreCase(apiKey.trim())) {
            return null;
        }
        try {
            String url = String.format(
                    "https://api.geoapify.com/v1/geocode/reverse?lat=%s&lon=%s&lang=es&apiKey=%s",
                    latitud,
                    longitud,
                    apiKey
            );

            DTOGeoapifyResponse response =
                    restTemplate.getForObject(url, DTOGeoapifyResponse.class);

            if (response == null ||
                response.getCaracteristicas() == null ||
                response.getCaracteristicas().isEmpty()) {
                return null;
            }

            DTOGeoapifyProperties props =
                    response.getCaracteristicas().get(0).getPropiedades();

            if (props == null) {
                return null;
            }

            String calle = props.getCalle();
            String numero = props.getNumeroPuerta();
            String apartamento = "";
            String esquina = props.getBarrio();
            String etiqueta = props.getFormatted() != null && !props.getFormatted().isBlank()
                    ? props.getFormatted()
                    : props.getTag();

            return new DTODireccion(etiqueta, calle, numero, apartamento, esquina, latitud, longitud);
        } catch (Exception e) {
            System.err.println(">>> [Geoapify] Reverse geocode: " + e.getMessage());
            return null;
        }
    }

    // Distancia por ruta entre dos coordenadas, en kilometros.
    // Usa Geoapify Routing API (respeta calles, no es linea recta).
    public double calcularDistanciaKm(
            double lat1,
            double lon1,
            double lat2,
            double lon2) {

        try {
            DTOGeoapifyProperties props = consultarRuta(lat1, lon1, lat2, lon2);
            if (props != null && props.getDistancia() != null) {
                // Geoapify devuelve la distancia en metros
                return props.getDistancia() / 1000.0;
            }
        } catch (Exception e) {
            System.err.println(">>> [Geoapify] Routing no disponible: " + e.getMessage());
        }
        // Desarrollo / API key inválida: distancia en línea recta (Haversine)
        return distanciaHaversineKm(lat1, lon1, lat2, lon2);
    }

    /** Distancia aproximada en km cuando Geoapify no responde. */
    private static double distanciaHaversineKm(
            double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    // Tiempo estimado de llegada entre dos coordenadas, en minutos.
    public Integer calcularTiempoLlegadaMinutos(DTODireccion origen, DTODireccion destino) {
        double lat1 = origen.getLatitud();
        double lon1 = origen.getLongitud();
        double lat2 = destino.getLatitud();
        double lon2 = destino.getLongitud();

        DTOGeoapifyProperties props = consultarRuta(lat1, lon1, lat2, lon2);
        if (props == null || props.getTiempo() == null) {
            // Fallback cuando la API no responde
            return 30;
        }
        // Geoapify devuelve el tiempo en segundos -> redondeo a minutos
        return (int) Math.round(props.getTiempo() / 60.0);
    }

    // Llamada al endpoint /v1/routing. Devuelve las propiedades de la primer
    // ruta o null si no se obtuvo respuesta.
    private DTOGeoapifyProperties consultarRuta(
            double lat1,
            double lon1,
            double lat2,
            double lon2) {

        String url = String.format(
                java.util.Locale.US,
                "https://api.geoapify.com/v1/routing?waypoints=%f,%f|%f,%f&mode=%s&apiKey=%s",
                lat1,
                lon1,
                lat2,
                lon2,
                MODO_RUTEO,
                apiKey
        );

        DTOGeoapifyResponse response =
                restTemplate.getForObject(url, DTOGeoapifyResponse.class);

        if (response == null ||
            response.getCaracteristicas() == null ||
            response.getCaracteristicas().isEmpty()) {
            return null;
        }

        return response.getCaracteristicas().get(0).getPropiedades();
    }
}
