package com.tallerwebi.punta_a_punta;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;

import com.tallerwebi.punta_a_punta.vistas.VistaHome;
import com.tallerwebi.punta_a_punta.vistas.VistaInventario;
import org.junit.jupiter.api.Test;

public class VistaCanjeSobreE2E extends BaseE2ETest {

  @Test
  void deberiaCanjearRepetidasPorUnSobreYAbrirlo() {
    iniciarSesionComo(ReiniciarDB.EMAIL_USUARIO_CANJE, ReiniciarDB.CLAVE_POR_DEFECTO);

    VistaHome vistaHome = new VistaHome(page);
    vistaHome.abrirRepetidas();

    VistaInventario vistaInventario = new VistaInventario(page);
    vistaInventario.canjearRepetidasPorPaquete();

    assertThat(
      vistaInventario.obtenerTituloDelModalAbierto().trim(),
      equalToIgnoringCase("SOBRE GANADO")
    );

    vistaInventario.abrirSobreGanado();

    assertThat(
      vistaInventario.obtenerTituloDelModalAbierto().trim(),
      equalToIgnoringCase("SOBRE ABIERTO")
    );
    assertThat(vistaInventario.obtenerCantidadDeFiguritasDelSobreAbierto(), is(5));
  }
}
