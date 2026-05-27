package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

public class TextoCorregidoTest {

  @Test
  public void deberiaCorregirTextosConMojibake() {
    String sudafricaMojibake = generarMojibake("Sudáfrica");
    String paisesBajosMojibake = generarMojibake("Países Bajos");

    assertThat(TextoCorregido.normalizar(sudafricaMojibake), equalTo("Sudáfrica"));
    assertThat(TextoCorregido.normalizar(paisesBajosMojibake), equalTo("Países Bajos"));
  }

  private String generarMojibake(String textoOriginal) {
    String unaPasada = new String(
      textoOriginal.getBytes(StandardCharsets.UTF_8),
      StandardCharsets.ISO_8859_1
    );

    return new String(unaPasada.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
  }
}
