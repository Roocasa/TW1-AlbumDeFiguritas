package com.tallerwebi.punta_a_punta;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;

import com.tallerwebi.punta_a_punta.vistas.VistaAlbum;
import com.tallerwebi.punta_a_punta.vistas.VistaHome;
import com.tallerwebi.punta_a_punta.vistas.VistaInventario;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.jupiter.api.Test;

public class FlujoCompletoE2E extends BaseE2ETest {

  private VistaHome vistaHome;
  private VistaAlbum vistaAlbum;
  private VistaInventario vistaInventario;

  @Test
  void deberiaAbrirUnSobreYPegarUnaFiguritaEnElAlbum() throws MalformedURLException {
    dadoQueElUsuarioEstaLogueado();
    cuandoElUsuarioAbreElAlbum();
    entoncesDeberiaSerRedirigidoALaVistaDeAlbum();
    entoncesDeberiaVerElAlbumVirtual();

    cuandoElUsuarioVuelveAlHome();
    cuandoElUsuarioAbreElInventario();
    cuandoElUsuarioAbreUnSobre();
    entoncesDeberiaEstarEnLaVistaDeInventario();
    entoncesDeberiaVerElSobreAbiertoConCincoFiguritas();

    cuandoElUsuarioGuardaLasFiguritas();
    cuandoElUsuarioPegaLaPrimeraFiguritaDisponible();
    entoncesDeberiaVerElMensajeDeFiguritaPegada();
  }

  private void dadoQueElUsuarioEstaLogueado() {
    vistaLogin.escribirEMAIL(ReiniciarDB.EMAIL_USUARIO_BASE);
    vistaLogin.escribirClave(ReiniciarDB.CLAVE_POR_DEFECTO);
    vistaLogin.darClickEnIniciarSesion();
  }

  private void cuandoElUsuarioAbreElAlbum() {
    vistaHome = new VistaHome(page);
    vistaHome.abrirAlbum();
  }

  private void entoncesDeberiaSerRedirigidoALaVistaDeAlbum() throws MalformedURLException {
    URL url = vistaLogin.obtenerURLActual();
    assertThat(url.getPath(), matchesPattern("^/spring/album(?:;jsessionid=[^/\\s]+)?$"));
  }

  private void entoncesDeberiaVerElAlbumVirtual() {
    vistaAlbum = new VistaAlbum(page);
    assertThat(vistaAlbum.obtenerTitulo().trim(), equalToIgnoringCase("Album virtual"));
    assertThat(vistaAlbum.obtenerTituloDelGrupoA().trim(), equalToIgnoringCase("Grupo A"));
  }

  private void cuandoElUsuarioVuelveAlHome() {
    vistaHome = new VistaHome(page);
    vistaHome.volverAlHome();
  }

  private void cuandoElUsuarioAbreElInventario() {
    vistaHome = new VistaHome(page);
    vistaHome.abrirInventario();
  }

  private void cuandoElUsuarioAbreUnSobre() {
    vistaInventario = new VistaInventario(page);
    vistaInventario.abrirSobre();
  }

  private void entoncesDeberiaEstarEnLaVistaDeInventario() throws MalformedURLException {
    URL urlActual = obtenerURLActual();
    assertThat(
      urlActual.getPath(),
      matchesPattern("^/spring/inventario(?:;jsessionid=[^/\\s]+)?$")
    );
  }

  private void entoncesDeberiaVerElSobreAbiertoConCincoFiguritas() {
    assertThat(
      vistaInventario.obtenerTituloDelModalAbierto().trim(),
      equalToIgnoringCase("SOBRE ABIERTO")
    );
    assertThat(vistaInventario.obtenerCantidadDeFiguritasDelSobreAbierto(), is(5));
  }

  private void cuandoElUsuarioGuardaLasFiguritas() {
    vistaInventario.guardarFiguritasEnElInventario();
  }

  private void cuandoElUsuarioPegaLaPrimeraFiguritaDisponible() {
    vistaInventario = new VistaInventario(page);
    vistaInventario.pegarPrimeraFiguritaDisponible();
  }

  private void entoncesDeberiaVerElMensajeDeFiguritaPegada() {
    assertThat(
      vistaInventario.obtenerMensajeExito().trim(),
      equalToIgnoringCase("Figurita pegada con exito.")
    );
  }
}
