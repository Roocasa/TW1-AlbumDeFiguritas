package com.tallerwebi.dominio.album;

import java.util.Date;

public class HistorialIntercambioDTO {

  private final PropuestaIntercambio propuesta;
  private final Figurita figuritaEntregada;
  private final Figurita figuritaRecibida;
  private final String usuarioContraparte;
  private final Date fecha;

  public HistorialIntercambioDTO(
    PropuestaIntercambio propuesta,
    Figurita figuritaEntregada,
    Figurita figuritaRecibida,
    String usuarioContraparte,
    Date fecha
  ) {
    this.propuesta = propuesta;
    this.figuritaEntregada = figuritaEntregada;
    this.figuritaRecibida = figuritaRecibida;
    this.usuarioContraparte = usuarioContraparte;
    this.fecha = fecha;
  }

  public PropuestaIntercambio getPropuesta() {
    return propuesta;
  }

  public Figurita getFiguritaEntregada() {
    return figuritaEntregada;
  }

  public Figurita getFiguritaRecibida() {
    return figuritaRecibida;
  }

  public String getUsuarioContraparte() {
    return usuarioContraparte;
  }

  public Date getFecha() {
    return fecha;
  }
}
