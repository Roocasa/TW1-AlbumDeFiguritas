package com.tallerwebi.punta_a_punta;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;

import com.tallerwebi.punta_a_punta.vistas.VistaHome;
import com.tallerwebi.punta_a_punta.vistas.VistaInventario;
import org.junit.jupiter.api.Test;

public class VistaFiguritaInventarioE2E extends BaseE2ETest {

  @Test
  void deberiaPegarUnaFiguritaDesdeElInventario() {
    iniciarSesionComo(ReiniciarDB.EMAIL_USUARIO_PEGADO, ReiniciarDB.CLAVE_POR_DEFECTO);

    VistaHome vistaHome = new VistaHome(page);
    vistaHome.abrirInventario();

    VistaInventario vistaInventario = new VistaInventario(page);
    vistaInventario.pegarPrimeraFiguritaDisponible();

    assertThat(
      "Figurita pegada con exito.",
      equalToIgnoringCase(vistaInventario.obtenerMensajeExito())
    );
  }
}
