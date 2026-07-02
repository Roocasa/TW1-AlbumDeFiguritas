package com.tallerwebi.dominio.prode;

import com.tallerwebi.dominio.Usuario;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "usuario_id", "partido_id" }) })
public class PronosticoProde {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "usuario_id")
  private Usuario usuario;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "partido_id")
  private PartidoProde partido;

  private Integer golesLocal;
  private Integer golesVisitante;
  private Integer puntos = 0;
  private boolean puntuado;

  public PronosticoProde() {}

  public PronosticoProde(
    Usuario usuario,
    PartidoProde partido,
    Integer golesLocal,
    Integer golesVisitante
  ) {
    this.usuario = usuario;
    this.partido = partido;
    this.golesLocal = golesLocal;
    this.golesVisitante = golesVisitante;
  }

  public void actualizarMarcador(Integer golesLocal, Integer golesVisitante) {
    this.golesLocal = golesLocal;
    this.golesVisitante = golesVisitante;
    this.puntuado = false;
    this.puntos = 0;
  }

  public int calcularPuntos() {
    if (!partido.estaFinalizado()) {
      return 0;
    }

    if (
      golesLocal.equals(partido.getGolesLocal()) &&
      golesVisitante.equals(partido.getGolesVisitante())
    ) {
      return 6;
    }

    return (
        resultado(golesLocal, golesVisitante) ==
        resultado(partido.getGolesLocal(), partido.getGolesVisitante())
      )
      ? 3
      : 0;
  }

  public void puntuar() {
    puntos = calcularPuntos();
    puntuado = true;
  }

  private int resultado(Integer golesLocal, Integer golesVisitante) {
    return Integer.compare(golesLocal, golesVisitante);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public PartidoProde getPartido() {
    return partido;
  }

  public void setPartido(PartidoProde partido) {
    this.partido = partido;
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

  public Integer getPuntos() {
    return puntos;
  }

  public void setPuntos(Integer puntos) {
    this.puntos = puntos;
  }

  public boolean isPuntuado() {
    return puntuado;
  }

  public void setPuntuado(boolean puntuado) {
    this.puntuado = puntuado;
  }
}
