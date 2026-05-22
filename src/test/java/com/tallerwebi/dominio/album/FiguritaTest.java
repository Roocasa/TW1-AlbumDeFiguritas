package com.tallerwebi.dominio.album;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

public class FiguritaTest {

  @Test
  public void deberiaPermitirSetearYObtenerLosDatosDeLaFigurita() {
    Figurita figurita = new Figurita();

    figurita.setId(22L);
    figurita.setNombre("Lionel Messi");
    figurita.setSeleccion("Argentina");
    figurita.setScore(99);
    figurita.setRareza(Rareza.LEYENDA);
    figurita.setOrdenAlbum(11);
    figurita.setClub("Inter Miami (USA 1)");
    figurita.setTipo(TipoFigurita.TITULAR);
    figurita.getPais().setCodigoBandera("ar");

    assertThat(figurita.getId(), equalTo(22L));
    assertThat(figurita.getNombre(), equalTo("Lionel Messi"));
    assertThat(figurita.getNombreJugador(), equalTo("Lionel Messi"));
    assertThat(figurita.getSeleccion(), equalTo("Argentina"));
    assertThat(figurita.getPais().getNombre(), equalTo("Argentina"));
    assertThat(figurita.getScore(), equalTo(99));
    assertThat(figurita.getNumeroDentroDelPais(), equalTo(11));
    assertThat(figurita.getOrdenAlbum(), equalTo(11));
    assertThat(figurita.getClub(), equalTo("Inter Miami (USA 1)"));
    assertThat(figurita.getRareza(), equalTo(Rareza.LEYENDA));
    assertThat(figurita.getTipo(), equalTo(TipoFigurita.TITULAR));
    assertThat(figurita.getBanderaUrl(), equalTo("https://flagcdn.com/ar.svg"));
  }

  @Test
  public void constructorConTipoDeberiaPermitirCrearUnaFiguritaEscudo() {
    Figurita figurita = new Figurita(
      "Escudo de Argentina",
      "Argentina",
      Rareza.ORO,
      TipoFigurita.ESCUDO
    );

    assertThat(figurita.getNombre(), equalTo("Escudo de Argentina"));
    assertThat(figurita.getSeleccion(), equalTo("Argentina"));
    assertThat(figurita.getRareza(), equalTo(Rareza.ORO));
    assertThat(figurita.getTipo(), equalTo(TipoFigurita.ESCUDO));
  }
}
