package com.backend.trego.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.backend.trego.entity.DTOs.DTDireccion;
import com.backend.trego.entity.DTOs.DTOGeoapifyProperties;
import com.backend.trego.entity.DTOs.DTOGeoapifyResponse;

@Service
public class GeoapifyService {

    @Value("${geoapify.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public DTDireccion obtenerDireccion(double latitud, double longitud) {

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

        return new DTDireccion(calle, numero, apartamento, esquina, latitud, longitud);
    }
}