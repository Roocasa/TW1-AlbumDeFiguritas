package com.tallerwebi.punta_a_punta;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;

import com.tallerwebi.punta_a_punta.vistas.VistaHome;
import com.tallerwebi.punta_a_punta.vistas.VistaPerfil;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.jupiter.api.Test;

public class VistaPerfilE2E extends BaseE2ETest {

  @Test
  void deberiaEntrarAMiPerfilDesdeElHome() throws MalformedURLException {
    iniciarSesionComo(ReiniciarDB.EMAIL_USUARIO_BASE, ReiniciarDB.CLAVE_POR_DEFECTO);

    VistaHome vistaHome = new VistaHome(page);
    vistaHome.abrirPerfil();

    VistaPerfil vistaPerfil = new VistaPerfil(page);
    URL urlActual = obtenerURLActual();

    assertThat(urlActual.getPath(), matchesPattern("^/spring/perfil(?:;jsessionid=[^/\\s]+)?$"));
    assertThat(vistaPerfil.obtenerTitulo().trim(), equalToIgnoringCase("Mi perfil"));
    assertThat(
      ReiniciarDB.EMAIL_USUARIO_BASE,
      equalToIgnoringCase(vistaPerfil.obtenerEmailPrincipal())
    );
  }
}
