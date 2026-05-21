package com.tallerwebi.punta_a_punta.vistas;

import com.microsoft.playwright.Page;

public class VistaAlbum extends VistaWeb {

  public VistaAlbum(Page page) {
    super(page);
  }

  public String obtenerTitulo() {
    return this.obtenerTextoDelElemento("#titulo-album");
  }

  public String obtenerTituloDelGrupoA() {
    return this.obtenerTextoDelElemento("#titulo-grupo-a");
  }
}
