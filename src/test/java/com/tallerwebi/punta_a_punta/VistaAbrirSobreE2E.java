package com.tallerwebi.punta_a_punta;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;

import com.tallerwebi.punta_a_punta.vistas.VistaHome;
import com.tallerwebi.punta_a_punta.vistas.VistaInventario;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.jupiter.api.Test;

public class VistaAbrirSobreE2E extends BaseE2ETest {

  @Test
  void deberiaAbrirUnSobreDesdeElInventario() throws MalformedURLException {
    iniciarSesionComo(ReiniciarDB.EMAIL_USUARIO_BASE, ReiniciarDB.CLAVE_POR_DEFECTO);

    VistaHome vistaHome = new VistaHome(page);
    vistaHome.abrirInventario();

    VistaInventario vistaInventario = new VistaInventario(page);
    vistaInventario.abrirSobre();

    URL urlActual = obtenerURLActual();
    assertThat(
      urlActual.getPath(),
      matchesPattern("^/spring/inventario(?:;jsessionid=[^/\\s]+)?$")
    );
    assertThat(
      vistaInventario.obtenerTituloDelModalAbierto().trim(),
      equalToIgnoringCase("SOBRE ABIERTO")
    );
    assertThat(vistaInventario.obtenerCantidadDeFiguritasDelSobreAbierto(), is(5));
  }
}
