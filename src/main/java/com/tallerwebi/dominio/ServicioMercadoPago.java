package com.tallerwebi.dominio;

public interface ServicioMercadoPago {
  String crearUrlDePago(PaqueteMonedas paquete, Usuario usuario, String baseUrl);
  String obtenerUrlFinalDespuesDelPago();
  boolean existePagoAprobado(Long idUsuario, String codigoPaquete);
}
