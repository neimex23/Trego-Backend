package com.backend.trego.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Se lanza cuando MercadoPago informa que un pago fue rechazado.
// Se mapea a HTTP 402 (Payment Required), también gestionado en
// GlobalExceptionHandler para devolver el idPedido en el cuerpo.
@ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
public class PagoRechazadoException extends RuntimeException {

    private final Integer idPedido;

    public PagoRechazadoException(Integer idPedido, String motivo) {
        super(motivo);
        this.idPedido = idPedido;
    }

    public Integer getIdPedido() {
        return idPedido;
    }
}
