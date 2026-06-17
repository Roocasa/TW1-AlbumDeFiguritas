package com.tallerwebi.dominio.ranking;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.album.Album;

public class RankingColeccionistaDTO {

  private final int posicion;
  private final Usuario usuario;
  private final Album album;
  private final int porcentajeCompletado;

  public RankingColeccionistaDTO(int posicion, Usuario usuario, Album album) {
    this.posicion = posicion;
    this.usuario = usuario;
    this.album = album;
    this.porcentajeCompletado = calcularPorcentaje(album);
  }

  public int getPosicion() {
    return posicion;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public Album getAlbum() {
    return album;
  }

  public int getPorcentajeCompletado() {
    return porcentajeCompletado;
  }

  public boolean isPodio() {
    return posicion <= 3;
  }

  private int calcularPorcentaje(Album album) {
    if (album == null || album.getTotalFiguritas() == null || album.getTotalFiguritas() <= 0) {
      return 0;
    }

    return (album.getFiguritasPegadas() * 100) / album.getTotalFiguritas();
  }
}
