package com.tallerwebi.punta_a_punta;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;

import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.tallerwebi.punta_a_punta.vistas.VistaAlbum;
import com.tallerwebi.punta_a_punta.vistas.VistaHome;
import com.tallerwebi.punta_a_punta.vistas.VistaLogin;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VistaAlbumE2E {

  static Playwright playwright;
  static Browser browser;
  BrowserContext context;
  VistaLogin vistaLogin;
  Page page;

  @BeforeAll
  static void abrirNavegador() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch(
            new BrowserType.LaunchOptions()
                    .setHeadless(false)
                    .setSlowMo(500)
    );
  }

  @AfterAll
  static void cerrarNavegador() {
    playwright.close();
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
    context.close();
  }

  @Test
  void deberiaAbrirElAlbumDesdeLaVistaHome() throws MalformedURLException {
    dadoQueElUsuarioInicioSesion();
    cuandoElUsuarioAbreElAlbum();
    entoncesDeberiaSerRedirigidoALaVistaDeAlbum();
    entoncesDeberiaVerElAlbumVirtual();
  }

  private void dadoQueElUsuarioInicioSesion() {
    vistaLogin.escribirEMAIL("eze@test.com");
    vistaLogin.escribirClave("123456789");
    vistaLogin.darClickEnIniciarSesion();
  }

  private void cuandoElUsuarioAbreElAlbum() {
    VistaHome vistaHome = new VistaHome(page);
    vistaHome.abrirAlbum();
  }

  private void entoncesDeberiaSerRedirigidoALaVistaDeAlbum() throws MalformedURLException {
    URL url = vistaLogin.obtenerURLActual();
    assertThat(url.getPath(), matchesPattern("^/spring/album(?:;jsessionid=[^/\\s]+)?$"));
  }

  private void entoncesDeberiaVerElAlbumVirtual() {
    VistaAlbum vistaAlbum = new VistaAlbum(page);
    assertThat("Album virtual", equalToIgnoringCase(vistaAlbum.obtenerTitulo()));
    assertThat("Grupo A", equalToIgnoringCase(vistaAlbum.obtenerTituloDelGrupoA()));
  }
}
