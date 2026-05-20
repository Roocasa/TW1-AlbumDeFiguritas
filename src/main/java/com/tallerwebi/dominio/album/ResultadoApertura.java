package com.tallerwebi.dominio.album;

import com.tallerwebi.dominio.Usuario;
import java.util.List;

public class ResultadoApertura {

  private List<Figurita> figuritasNuevas;
  private Usuario usuarioActualizado;

  public ResultadoApertura(List<Figurita> figuritasNuevas, Usuario usuarioActualizado) {
    this.figuritasNuevas = figuritasNuevas;
    this.usuarioActualizado = usuarioActualizado;
  }

  public List<Figurita> getFiguritasNuevas() {
    return figuritasNuevas;
  }

  public Usuario getUsuarioActualizado() {
    return usuarioActualizado;
  }
}
