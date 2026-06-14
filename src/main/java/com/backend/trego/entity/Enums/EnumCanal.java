package com.backend.trego.entity.Enums;

// Canal desde el que el cliente inicia el pago. Lo envía el cliente al crear la
// preferencia y determina qué set de back_urls usa MercadoPago para redirigir
// tras el checkout: WEB apunta al front React; MOBILE a un deep link de la app.
public enum EnumCanal {
    WEB,
    MOBILE
}
