package com.tallerwebi.punta_a_punta;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;

import com.tallerwebi.punta_a_punta.vistas.VistaAlbum;
import com.tallerwebi.punta_a_punta.vistas.VistaHome;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.jupiter.api.Test;

public class VistaAlbumE2E extends BaseE2ETest {

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
    assertThat(vistaAlbum.obtenerTitulo().trim(), equalToIgnoringCase("Album virtual"));
    assertThat(vistaAlbum.obtenerTituloDelGrupoA().trim(), equalToIgnoringCase("Grupo A"));
  }
}
