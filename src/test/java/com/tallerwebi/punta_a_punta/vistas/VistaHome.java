package com.tallerwebi.punta_a_punta.vistas;

import com.microsoft.playwright.Page;

public class VistaHome extends VistaWeb {

  public VistaHome(Page page) {
    super(page);
  }

  public void abrirAlbum() {
    this.darClickEnElElemento(".home-panel--album");
  }

  public void abrirInventario() {
    this.darClickEnElElemento(".home-panel--packs");
  }

  public void abrirRepetidas() {
    this.darClickEnElElemento(".home-panel--repe");
  }

  public void abrirPerfil() {
    this.darClickEnElElemento(".home-topbar__actions a[href$='/perfil']");
  }
}
