package com.tallerwebi.dominio.prode;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class PartidoProde {

  private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern(
    "dd/MM/yyyy HH:mm"
  );

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  private Long idApi;

  private String local;
  private String visitante;
  private LocalDateTime fecha;

  @Enumerated(EnumType.STRING)
  private EstadoPartidoProde estado = EstadoPartidoProde.PROGRAMADO;

  private Integer golesLocal;
  private Integer golesVisitante;

  public PartidoProde() {}

  public PartidoProde(Long idApi, String local, String visitante, LocalDateTime fecha) {
    this.idApi = idApi;
    this.local = local;
    this.visitante = visitante;
    this.fecha = fecha;
  }

  public boolean estaFinalizado() {
    return EstadoPartidoProde.FINALIZADO.equals(estado);
  }

  public void actualizarResultado(Integer golesLocal, Integer golesVisitante) {
    this.golesLocal = golesLocal;
    this.golesVisitante = golesVisitante;
    this.estado = EstadoPartidoProde.FINALIZADO;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getIdApi() {
    return idApi;
  }

  public void setIdApi(Long idApi) {
    this.idApi = idApi;
  }

  public String getLocal() {
    return local;
  }

  public void setLocal(String local) {
    this.local = local;
  }

  public String getVisitante() {
    return visitante;
  }

  public void setVisitante(String visitante) {
    this.visitante = visitante;
  }

  public LocalDateTime getFecha() {
    return fecha;
  }

  public String getFechaFormateada() {
    return fecha == null ? "" : fecha.format(FORMATO_FECHA);
  }

  public void setFecha(LocalDateTime fecha) {
    this.fecha = fecha;
  }

  public EstadoPartidoProde getEstado() {
    return estado;
  }

  public void setEstado(EstadoPartidoProde estado) {
    this.estado = estado;
  }

  public Integer getGolesLocal() {
    return golesLocal;
  }

  public void setGolesLocal(Integer golesLocal) {
    this.golesLocal = golesLocal;
  }

  public Integer getGolesVisitante() {
    return golesVisitante;
  }

  public void setGolesVisitante(Integer golesVisitante) {
    this.golesVisitante = golesVisitante;
  }
}
