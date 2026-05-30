package com.tallerwebi.punta_a_punta.vistas;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;

public class VistaLogin extends VistaWeb {

  private static final double TIEMPO_MAXIMO_DE_ESPERA_MS = Double.parseDouble(
    System.getProperty("e2e.appStartupTimeoutMs", "60000")
  );
  private static final double TIEMPO_DE_ESPERA_ENTRE_REINTENTOS_MS = 1000;

  public VistaLogin(Page page) {
    super(page);
    this.abrirPantallaDeLogin();
  }

  public String obtenerTextoDeLaBarraDeNavegacion() {
    return this.obtenerTextoDelElemento("nav a.navbar-brand");
  }

  public String obtenerMensajeDeError() {
    return this.obtenerTextoDelElemento(".auth-alert");
  }

  public void escribirEMAIL(String email) {
    this.escribirEnElElemento("#email", email);
  }

  public void escribirClave(String clave) {
    this.escribirEnElElemento("#password", clave);
  }

  public void darClickEnIniciarSesion() {
    this.darClickEnElElemento("#btn-login");
  }

  public void darClickEnRegistrarse() {
    this.darClickEnElElemento("#btn-register");
  }

  private void abrirPantallaDeLogin() {
    long inicio = System.currentTimeMillis();
    String urlLogin = construirURL("/login");
    String ultimoError = "No se pudo cargar la pantalla de login.";

    while (System.currentTimeMillis() - inicio < TIEMPO_MAXIMO_DE_ESPERA_MS) {
      Response respuesta = page.navigate(urlLogin);
      int status = respuesta != null ? respuesta.status() : -1;

      if (status < 500 && page.locator("#email").count() > 0) {
        return;
      }

      ultimoError =
        "La app todavia no dejo disponible la pantalla de login. " +
        "HTTP=" +
        status +
        ", URL actual=" +
        page.url() +
        ", titulo=" +
        page.title();

      page.waitForTimeout(TIEMPO_DE_ESPERA_ENTRE_REINTENTOS_MS);
    }

    throw new IllegalStateException(ultimoError);
  }
}
