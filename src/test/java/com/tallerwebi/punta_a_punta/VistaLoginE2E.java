package com.tallerwebi.punta_a_punta;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;

import com.tallerwebi.punta_a_punta.vistas.VistaNuevoUsuario;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.jupiter.api.Test;

public class VistaLoginE2E extends BaseE2ETest {

  @Test
  void deberiaDecirUNLAMEnElNavbar() throws MalformedURLException {
    dadoQueElUsuarioEstaEnLaVistaDeLogin();
    entoncesDeberiaVerUNLAMEnElNavbar();
  }

  @Test
  void deberiaDarUnErrorAlIntentarIniciarSesionConUnUsuarioQueNoExiste() {
    dadoQueElUsuarioCargaSusDatosDeLoginCon("damian@unlam.edu.ar", "123456");
    cuandoElUsuarioTocaElBotonDeLogin();
    entoncesDeberiaVerUnMensajeDeError();
  }

  @Test
  void deberiaNavegarAlHomeSiElUsuarioExiste() throws MalformedURLException {
    dadoQueElUsuarioCargaSusDatosDeLoginCon("eze@test.com", "123456789");
    cuandoElUsuarioTocaElBotonDeLogin();
    entoncesDeberiaSerRedirigidoALaVistaDeHome();
  }

  @Test
  void deberiaRegistrarUnUsuarioEIniciarSesionExistosamente() throws MalformedURLException {
    dadoQueElUsuarioNavegaALaVistaDeRegistro();
    dadoQueElUsuarioSeRegistraCon("juan@unlam.edu.ar", "Argentina", "123456");
    dadoQueElUsuarioEstaEnLaVistaDeLogin();
    dadoQueElUsuarioCargaSusDatosDeLoginCon("juan@unlam.edu.ar", "123456");
    cuandoElUsuarioTocaElBotonDeLogin();
    entoncesDeberiaSerRedirigidoALaVistaDeHome();
  }

  private void entoncesDeberiaVerUNLAMEnElNavbar() {
    String texto = vistaLogin.obtenerTextoDeLaBarraDeNavegacion();
    assertThat("UNLAM", equalToIgnoringCase(texto));
  }

  private void dadoQueElUsuarioEstaEnLaVistaDeLogin() throws MalformedURLException {
    URL urlLogin = vistaLogin.obtenerURLActual();
    assertThat(urlLogin.getPath(), matchesPattern("^/spring/login(?:;jsessionid=[^/\\s]+)?$"));
  }

  private void cuandoElUsuarioTocaElBotonDeLogin() {
    vistaLogin.darClickEnIniciarSesion();
  }

  private void entoncesDeberiaSerRedirigidoALaVistaDeHome() throws MalformedURLException {
    URL url = vistaLogin.obtenerURLActual();
    assertThat(url.getPath(), matchesPattern("^/spring/home(?:;jsessionid=[^/\\s]+)?$"));
  }

  private void entoncesDeberiaVerUnMensajeDeError() {
    String texto = vistaLogin.obtenerMensajeDeError();
    assertThat("Error: Usuario o clave incorrecta", equalToIgnoringCase(texto));
  }

  private void dadoQueElUsuarioCargaSusDatosDeLoginCon(String email, String clave) {
    vistaLogin.escribirEMAIL(email);
    vistaLogin.escribirClave(clave);
  }

  private void dadoQueElUsuarioNavegaALaVistaDeRegistro() {
    vistaLogin.darClickEnRegistrarse();
  }

  private void dadoQueElUsuarioSeRegistraCon(String email, String pais, String clave) {
    VistaNuevoUsuario vistaNuevoUsuario = new VistaNuevoUsuario(context.pages().get(0));
    vistaNuevoUsuario.escribirEMAIL(email);
    vistaNuevoUsuario.seleccionarPais(pais);
    vistaNuevoUsuario.escribirClave(clave);
    vistaNuevoUsuario.escribirConfirmacionDeClave(clave);
    vistaNuevoUsuario.darClickEnRegistrarme();
  }
}
