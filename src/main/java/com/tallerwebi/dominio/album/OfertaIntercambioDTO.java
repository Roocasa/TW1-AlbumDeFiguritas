package com.tallerwebi.dominio.album;

import com.tallerwebi.dominio.Usuario;
import java.util.List;

public class OfertaIntercambioDTO {

  private Usuario usuario;
  private List<InventarioItemDTO> figuritasRepetidas;

  public OfertaIntercambioDTO(Usuario usuario, List<InventarioItemDTO> figuritasRepetidas) {
    this.usuario = usuario;
    this.figuritasRepetidas = figuritasRepetidas;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public List<InventarioItemDTO> getFiguritasRepetidas() {
    return figuritasRepetidas;
  }

  public boolean isTieneFiguritasParaIntercambiar() {
    return figuritasRepetidas != null && !figuritasRepetidas.isEmpty();
  }
}
