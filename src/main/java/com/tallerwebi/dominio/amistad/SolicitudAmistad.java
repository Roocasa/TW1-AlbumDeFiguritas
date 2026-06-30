package com.tallerwebi.dominio.amistad;

import com.tallerwebi.dominio.Usuario;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@SuppressWarnings("PMD.NullAssignment")
public class SolicitudAmistad {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER)
  private Usuario solicitante;

  @ManyToOne(fetch = FetchType.EAGER)
  private Usuario receptor;

  @Enumerated(EnumType.STRING)
  private EstadoSolicitudAmistad estado = EstadoSolicitudAmistad.PENDIENTE;

  private LocalDateTime fechaCreacion = LocalDateTime.now();
  private LocalDateTime fechaRespuesta;

  public SolicitudAmistad() {}

  public SolicitudAmistad(Usuario solicitante, Usuario receptor) {
    this.solicitante = solicitante;
    this.receptor = receptor;
    this.estado = EstadoSolicitudAmistad.PENDIENTE;
    this.fechaCreacion = LocalDateTime.now();
  }

  public void aceptar() {
    this.estado = EstadoSolicitudAmistad.ACEPTADA;
    this.fechaRespuesta = LocalDateTime.now();
  }

  public void rechazar() {
    this.estado = EstadoSolicitudAmistad.RECHAZADA;
    this.fechaRespuesta = LocalDateTime.now();
  }

  public void reactivar(Usuario nuevoSolicitante, Usuario nuevoReceptor) {
    this.solicitante = nuevoSolicitante;
    this.receptor = nuevoReceptor;
    this.estado = EstadoSolicitudAmistad.PENDIENTE;
    this.fechaCreacion = LocalDateTime.now();
    this.fechaRespuesta = null;
  }

  public boolean isPendiente() {
    return EstadoSolicitudAmistad.PENDIENTE.equals(estado);
  }

  public boolean isAceptada() {
    return EstadoSolicitudAmistad.ACEPTADA.equals(estado);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Usuario getSolicitante() {
    return solicitante;
  }

  public void setSolicitante(Usuario solicitante) {
    this.solicitante = solicitante;
  }

  public Usuario getReceptor() {
    return receptor;
  }

  public void setReceptor(Usuario receptor) {
    this.receptor = receptor;
  }

  public EstadoSolicitudAmistad getEstado() {
    return estado;
  }

  public void setEstado(EstadoSolicitudAmistad estado) {
    this.estado = estado;
  }

  public LocalDateTime getFechaCreacion() {
    return fechaCreacion;
  }

  public void setFechaCreacion(LocalDateTime fechaCreacion) {
    this.fechaCreacion = fechaCreacion;
  }

  public LocalDateTime getFechaRespuesta() {
    return fechaRespuesta;
  }

  public void setFechaRespuesta(LocalDateTime fechaRespuesta) {
    this.fechaRespuesta = fechaRespuesta;
  }
}
