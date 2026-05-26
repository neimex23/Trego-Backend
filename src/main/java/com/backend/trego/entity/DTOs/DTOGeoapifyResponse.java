package com.backend.trego.entity.DTOs;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DTOGeoapifyResponse {

    @JsonProperty("features")
    private List<Caracteristica> caracteristicas;

    public List<Caracteristica> getCaracteristicas() {
        return caracteristicas;
    }

    public static class Caracteristica {

        @JsonProperty("properties")
        private DTOGeoapifyProperties propiedades;

        public DTOGeoapifyProperties getPropiedades() {
            return propiedades;
        }
    }
}
