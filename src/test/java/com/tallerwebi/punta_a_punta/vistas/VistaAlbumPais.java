package com.tallerwebi.punta_a_punta.vistas;

import com.microsoft.playwright.Page;

public class VistaAlbumPais extends VistaWeb {

  public VistaAlbumPais(Page page) {
    super(page);
  }

  public String obtenerTituloDelPais() {
    return this.obtenerTextoDelElemento("#titulo-pais");
  }

  public void pegarFigurita(Long idFigurita) {
    this.darClickEnElElemento("#figurita-" + idFigurita + " .country-sticker__plus");
  }

  public boolean laFiguritaEstaPegada(Long idFigurita) {
    return this.elElementoContieneLaClase("#figurita-" + idFigurita, "country-sticker--filled");
  }

  public String obtenerEstadoDeLaFigurita(Long idFigurita) {
    return this.obtenerTextoDelElemento("#figurita-" + idFigurita + " .country-sticker__body p");
  }
}
