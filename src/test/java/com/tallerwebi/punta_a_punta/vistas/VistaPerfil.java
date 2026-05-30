package com.tallerwebi.punta_a_punta.vistas;

import com.microsoft.playwright.Page;

public class VistaPerfil extends VistaWeb {

  public VistaPerfil(Page page) {
    super(page);
  }

  public String obtenerTitulo() {
    return this.obtenerTextoDelElemento(".profile-hero h1");
  }

  public String obtenerEmailPrincipal() {
    return this.obtenerTextoDelElemento(".profile-card h2");
  }
}
