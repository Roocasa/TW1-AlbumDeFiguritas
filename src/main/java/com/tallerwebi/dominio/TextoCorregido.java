package com.tallerwebi.dominio;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class TextoCorregido {

  private static final int MAXIMO_DE_PASADAS = 3;
  private static final Charset WINDOWS_1252 = Charset.forName("windows-1252");

  private TextoCorregido() {}

  public static String normalizar(String texto) {
    if (texto == null || texto.isEmpty()) {
      return texto;
    }

    String corregido = texto;

    for (int i = 0; i < MAXIMO_DE_PASADAS && pareceTextoMalCodificado(corregido); i++) {
      String siguiente = corregirConMejorCharset(corregido);

      if (siguiente.equals(corregido)) {
        break;
      }

      corregido = siguiente;
    }

    return corregido.replace('\u00A0', ' ');
  }

  private static boolean pareceTextoMalCodificado(String texto) {
    return (
      texto.contains("Ã") ||
      texto.contains("Â") ||
      texto.contains("â") ||
      texto.contains("ð") ||
      texto.contains("�")
    );
  }

  private static String corregirConMejorCharset(String texto) {
    String corregidoConIso = new String(
      texto.getBytes(StandardCharsets.ISO_8859_1),
      StandardCharsets.UTF_8
    );
    String corregidoConWindows1252 = new String(
      texto.getBytes(WINDOWS_1252),
      StandardCharsets.UTF_8
    );

    return puntajeDeMojibake(corregidoConWindows1252) < puntajeDeMojibake(corregidoConIso)
      ? corregidoConWindows1252
      : corregidoConIso;
  }

  @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
  private static int puntajeDeMojibake(String texto) {
    int puntaje = 0;

    for (char caracter : texto.toCharArray()) {
      if (
        caracter == 'Ã' || caracter == 'Â' || caracter == 'â' || caracter == 'ð' || caracter == '�'
      ) {
        puntaje++;
      }
    }

    return puntaje;
  }
}
