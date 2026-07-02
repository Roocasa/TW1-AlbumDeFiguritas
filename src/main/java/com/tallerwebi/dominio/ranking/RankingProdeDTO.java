package com.tallerwebi.dominio.ranking;

import com.tallerwebi.dominio.Usuario;

public class RankingProdeDTO {

  private final int posicion;
  private final Usuario usuario;
  private final int puntaje;

  public RankingProdeDTO(int posicion, Usuario usuario, int puntaje) {
    this.posicion = posicion;
    this.usuario = usuario;
    this.puntaje = puntaje;
  }

  public int getPosicion() {
    return posicion;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public int getPuntaje() {
    return puntaje;
  }

  public boolean isPodio() {
    return posicion <= 3;
  }
}
