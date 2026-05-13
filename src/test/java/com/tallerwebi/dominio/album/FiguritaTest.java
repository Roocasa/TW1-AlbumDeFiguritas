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

    assertThat(figurita.getId(), equalTo(22L));
    assertThat(figurita.getNombre(), equalTo("Lionel Messi"));
    assertThat(figurita.getSeleccion(), equalTo("Argentina"));
    assertThat(figurita.getScore(), equalTo(99));
    assertThat(figurita.getRareza(), equalTo(Rareza.LEYENDA));
  }

  @Test
  public void pegarDeberiaMarcarLaFiguritaComoPegada() {
    Figurita figurita = new Figurita("Alexis Mac Allister", "Argentina", Rareza.ORO);

    figurita.pegar();

    assertThat(figurita.isPegada(), is(true));
  }
}
