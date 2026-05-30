package com.tallerwebi.punta_a_punta;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.tallerwebi.punta_a_punta.vistas.VistaLogin;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseE2ETest {

  protected static Playwright playwright;
  protected static Browser browser;

  protected BrowserContext context;
  protected Page page;
  protected VistaLogin vistaLogin;

  @BeforeAll
  static void abrirNavegador() {
    boolean headless = Boolean.parseBoolean(System.getProperty("e2e.headless", "true"));
    double slowMo = Double.parseDouble(System.getProperty("e2e.slowMo", "0"));

    playwright = Playwright.create();
    browser =
      playwright
        .chromium()
        .launch(new BrowserType.LaunchOptions().setHeadless(headless).setSlowMo(slowMo));
  }

  @AfterAll
  static void cerrarNavegador() {
    if (playwright != null) {
      playwright.close();
    }
  }

  @BeforeEach
  void crearContextoYPagina() {
    ReiniciarDB.limpiarBaseDeDatos();

    context = browser.newContext();
    page = context.newPage();
    vistaLogin = new VistaLogin(page);
  }

  @AfterEach
  void cerrarContexto() {
    if (context != null) {
      context.close();
    }
  }

  protected void iniciarSesionComo(String email, String clave) {
    vistaLogin.escribirEMAIL(email);
    vistaLogin.escribirClave(clave);
    vistaLogin.darClickEnIniciarSesion();
  }

  protected URL obtenerURLActual() throws MalformedURLException {
    return new URL(page.url());
  }
}
