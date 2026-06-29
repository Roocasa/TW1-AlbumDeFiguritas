package com.tallerwebi.dominio.album;

import com.tallerwebi.dominio.Usuario;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class PropuestaIntercambio {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private Usuario solicitante;

  @ManyToOne
  private Usuario receptor;

  @ManyToOne
  private Figurita figuritaOfrecida;

  @ManyToOne
  private Figurita figuritaSolicitada;

  @Enumerated(EnumType.STRING)
  private EstadoPropuestaIntercambio estado = EstadoPropuestaIntercambio.PENDIENTE;

  @Temporal(TemporalType.TIMESTAMP)
  private Date fechaCreacion = new Date();

  @Temporal(TemporalType.TIMESTAMP)
  private Date fechaRespuesta;

  public PropuestaIntercambio() {}

  public PropuestaIntercambio(
    Usuario solicitante,
    Usuario receptor,
    Figurita figuritaOfrecida,
    Figurita figuritaSolicitada
  ) {
    this.solicitante = solicitante;
    this.receptor = receptor;
    this.figuritaOfrecida = figuritaOfrecida;
    this.figuritaSolicitada = figuritaSolicitada;
  }

  public Long getId() {
    return id;
  }

  public Usuario getSolicitante() {
    return solicitante;
  }

  public Usuario getReceptor() {
    return receptor;
  }

  public Figurita getFiguritaOfrecida() {
    return figuritaOfrecida;
  }

  public Figurita getFiguritaSolicitada() {
    return figuritaSolicitada;
  }

  public EstadoPropuestaIntercambio getEstado() {
    return estado;
  }

  public Date getFechaCreacion() {
    return fechaCreacion;
  }

  public Date getFechaRespuesta() {
    return fechaRespuesta;
  }

  public Date getFechaMovimiento() {
    return fechaRespuesta != null ? fechaRespuesta : fechaCreacion;
  }

  public void aceptar() {
    this.estado = EstadoPropuestaIntercambio.ACEPTADA;
    this.fechaRespuesta = new Date();
  }

  public void rechazar() {
    this.estado = EstadoPropuestaIntercambio.RECHAZADA;
    this.fechaRespuesta = new Date();
  }

  public void cancelar() {
    this.estado = EstadoPropuestaIntercambio.CANCELADA;
    this.fechaRespuesta = new Date();
  }

  public boolean isPendiente() {
    return EstadoPropuestaIntercambio.PENDIENTE.equals(estado);
  }
}
