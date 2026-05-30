package com.tallerwebi.punta_a_punta;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;

import com.tallerwebi.punta_a_punta.vistas.VistaAlbum;
import com.tallerwebi.punta_a_punta.vistas.VistaAlbumPais;
import com.tallerwebi.punta_a_punta.vistas.VistaHome;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.jupiter.api.Test;

public class VistaAlbumPaisE2E extends BaseE2ETest {

  private static final Long ID_FIGURITA_ARGENTINA = 433L;

  @Test
  void deberiaPegarUnaFiguritaDesdeLaVistaDePaisDelAlbum() throws MalformedURLException {
    iniciarSesionComo(ReiniciarDB.EMAIL_USUARIO_PEGADO, ReiniciarDB.CLAVE_POR_DEFECTO);

    VistaHome vistaHome = new VistaHome(page);
    vistaHome.abrirAlbum();

    VistaAlbum vistaAlbum = new VistaAlbum(page);
    vistaAlbum.abrirPais("Argentina");

    VistaAlbumPais vistaAlbumPais = new VistaAlbumPais(page);
    vistaAlbumPais.pegarFigurita(ID_FIGURITA_ARGENTINA);

    URL urlActual = obtenerURLActual();
    assertThat(
      urlActual.getPath(),
      matchesPattern("^/spring/album/pais/ARG(?:;jsessionid=[^/\\s]+)?$")
    );
    assertThat("Argentina", equalToIgnoringCase(vistaAlbumPais.obtenerTituloDelPais()));
    assertThat(vistaAlbumPais.laFiguritaEstaPegada(ID_FIGURITA_ARGENTINA), is(true));
    assertThat(
      "Pegada en el album",
      equalToIgnoringCase(vistaAlbumPais.obtenerEstadoDeLaFigurita(ID_FIGURITA_ARGENTINA))
    );
  }
}
