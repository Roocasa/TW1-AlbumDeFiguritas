package com.tallerwebi.punta_a_punta;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;

import com.tallerwebi.punta_a_punta.vistas.VistaHome;
import com.tallerwebi.punta_a_punta.vistas.VistaInventario;
import org.junit.jupiter.api.Test;

public class VistaCanjeEscudoE2E extends BaseE2ETest {

  @Test
  void deberiaCanjearRepetidasPorUnEscudoAleatorio() {
    iniciarSesionComo(ReiniciarDB.EMAIL_USUARIO_CANJE, ReiniciarDB.CLAVE_POR_DEFECTO);

    VistaHome vistaHome = new VistaHome(page);
    vistaHome.abrirRepetidas();

    VistaInventario vistaInventario = new VistaInventario(page);
    vistaInventario.canjearRepetidasPorEscudo();

    assertThat(
      vistaInventario.obtenerTituloDelModalAbierto().trim(),
      equalToIgnoringCase("ESCUDO GANADO")
    );
    assertThat(
      vistaInventario.obtenerTextoDelModalDeRecompensa().trim(),
      equalToIgnoringCase("Canjeaste tus repetidas y este fue el escudo que te toco.")
    );
    assertThat(vistaInventario.obtenerCantidadDeFiguritasDelSobreAbierto(), is(1));
  }
}
