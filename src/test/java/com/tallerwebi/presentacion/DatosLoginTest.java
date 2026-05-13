package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

public class DatosLoginTest {

  @Test
  public void deberiaPermitirSetearElEmailYLaPassword() {
    DatosLogin datosLogin = new DatosLogin();

    datosLogin.setEmail("nuevo@unlam.edu.ar");
    datosLogin.setPassword("123456");

    assertThat(datosLogin.getEmail(), equalTo("nuevo@unlam.edu.ar"));
    assertThat(datosLogin.getPassword(), equalTo("123456"));
  }
}
