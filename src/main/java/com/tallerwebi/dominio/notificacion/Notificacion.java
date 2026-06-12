package com.tallerwebi.dominio.notificacion;

import com.tallerwebi.dominio.Usuario;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Notificacion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private Usuario usuario;

  private String titulo;
  private String mensaje;
  private String destino;
  private boolean leida;

  @Temporal(TemporalType.TIMESTAMP)
  private Date fechaCreacion = new Date();

  public Notificacion() {}

  public Notificacion(Usuario usuario, String titulo, String mensaje, String destino) {
    this.usuario = usuario;
    this.titulo = titulo;
    this.mensaje = mensaje;
    this.destino = destino;
  }

  public Long getId() {
    return id;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public String getTitulo() {
    return titulo;
  }

  public String getMensaje() {
    return mensaje;
  }

  public String getDestino() {
    return destino;
  }

  public boolean isLeida() {
    return leida;
  }

  public Date getFechaCreacion() {
    return fechaCreacion;
  }

  public void marcarComoLeida() {
    this.leida = true;
  }
}
