package com.tallerwebi.dominio;

public interface ServicioMercadoPago {
  String crearUrlDePago(PaqueteMonedas paquete, Usuario usuario, String baseUrl);
}
