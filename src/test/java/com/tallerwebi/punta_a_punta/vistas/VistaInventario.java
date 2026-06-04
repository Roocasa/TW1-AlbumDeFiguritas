package com.tallerwebi.punta_a_punta.vistas;

import com.microsoft.playwright.Page;

public class VistaInventario extends VistaWeb {

  public VistaInventario(Page page) {
    super(page);
  }

  public void abrirSobre() {
    this.darClickEnElElemento(".sobres .btn");
  }

  public void pegarPrimeraFiguritaDisponible() {
    page.locator(".js-pegar-figurita").first().click();
  }

  public void canjearRepetidasPorPaquete() {
    this.darClickEnElElemento("form[action$='/canjear-repetidas/paquete'] button");
  }

  public void canjearRepetidasPorEscudo() {
    this.darClickEnElElemento("form[action$='/canjear-repetidas/escudo'] button");
  }

  public void abrirSobreGanado() {
    this.darClickEnElElemento(".inventory-reward-modal__footer a[href$='/abrir-paquete']");
  }

  public String obtenerMensajeExito() {
    return this.obtenerTextoDelElemento(".alert-success");
  }

  public String obtenerTituloDelModalAbierto() {
    return this.obtenerTextoDelElemento(".modal.show .modal-title");
  }

  public String obtenerTextoDelModalDeRecompensa() {
    return this.obtenerTextoDelElemento(".inventory-reward-modal__copy");
  }

  public int obtenerCantidadDeFiguritasDelSobreAbierto() {
    return this.contarElementos(".pack-result-card");
  }

  public void guardarFiguritasEnElInventario() {
    darClickEnElElemento(".modal.show .modal-footer a[href$='/inventario']");
  }
}
