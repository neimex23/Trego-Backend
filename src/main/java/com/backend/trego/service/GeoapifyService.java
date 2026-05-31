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

        String url = String.format(
                "https://api.geoapify.com/v1/geocode/reverse?lat=%s&lon=%s&apiKey=%s",
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

        // el numero de puerta viene como string
        int numero;
        try {
            numero = Integer.parseInt(props.getNumeroPuerta());
        } catch (Exception e) {
            numero = 0;
        }

        // Geoapify normalmente no devuelve apartamento
        int apartamento = 0;

        // esquina aproximada
        String esquina = props.getBarrio();

        return new DTODireccion(calle, numero, apartamento, esquina, latitud, longitud);
    }

    // Distancia por ruta entre dos coordenadas, en kilometros.
    // Usa Geoapify Routing API (respeta calles, no es linea recta).
    public double calcularDistanciaKm(
            double lat1,
            double lon1,
            double lat2,
            double lon2) {

        DTOGeoapifyProperties props = consultarRuta(lat1, lon1, lat2, lon2);
        if (props == null || props.getDistancia() == null) {
            return -1;
        }
        // Geoapify devuelve la distancia en metros
        return props.getDistancia() / 1000.0;
    }

    // Tiempo estimado de llegada entre dos coordenadas, en minutos.
    public int calcularTiempoLlegadaMinutos(DTODireccion origen, DTODireccion destino) {
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
