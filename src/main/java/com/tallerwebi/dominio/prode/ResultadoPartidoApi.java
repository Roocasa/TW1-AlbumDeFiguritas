package com.tallerwebi.dominio.prode;

import java.time.LocalDateTime;

public class ResultadoPartidoApi {

  private final Long idApi;
  private final String local;
  private final String visitante;
  private final LocalDateTime fecha;
  private final boolean finalizado;
  private final Integer golesLocal;
  private final Integer golesVisitante;

  public ResultadoPartidoApi(
    Long idApi,
    String local,
    String visitante,
    LocalDateTime fecha,
    boolean finalizado,
    Integer golesLocal,
    Integer golesVisitante
  ) {
    this.idApi = idApi;
    this.local = local;
    this.visitante = visitante;
    this.fecha = fecha;
    this.finalizado = finalizado;
    this.golesLocal = golesLocal;
    this.golesVisitante = golesVisitante;
  }

  public Long getIdApi() {
    return idApi;
  }

  public String getLocal() {
    return local;
  }

  public String getVisitante() {
    return visitante;
  }

  public LocalDateTime getFecha() {
    return fecha;
  }

  public boolean isFinalizado() {
    return finalizado;
  }

  public Integer getGolesLocal() {
    return golesLocal;
  }

  public Integer getGolesVisitante() {
    return golesVisitante;
  }
}
