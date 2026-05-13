package com.tallerwebi.dominio.album;

import org.springframework.stereotype.Component;

@Component
public class RuletaFiguritas {

  //    1 a 60 -> COMUN
  //    61 a 85 -> PLATA
  //    86 a 95 -> ORO
  //    96 a 100 -> LEYENDA
  private static final int MIN_LEYENDA = 96;
  private static final int MIN_ORO = 86;
  private static final int MIN_PLATA = 61;

  //    PAQUETE PREMIUM
  //    1 a 50 -> PLATA
  //    51 a 85 -> ORO
  //    86 a 100 -> LEYENDA
  private static final int MIN_ORO_PREMIUM = 51;
  private static final int MIN_LEYENDA_PREMIUM = 86;

  public Rareza calcularRareza(int numeroRandom) {
    if (numeroRandom >= MIN_LEYENDA) {
      return Rareza.LEYENDA;
    } else if (numeroRandom >= MIN_ORO) {
      return Rareza.ORO;
    } else if (numeroRandom >= MIN_PLATA) {
      return Rareza.PLATA;
    } else {
      return Rareza.COMUN;
    }
  }

  public Rareza calcularRarezaPremium(int numeroRandom) {
    if (numeroRandom >= MIN_LEYENDA_PREMIUM) {
      return Rareza.LEYENDA;
    } else if (numeroRandom >= MIN_ORO_PREMIUM) {
      return Rareza.ORO;
    } else {
      return Rareza.PLATA;
    }
  }
}
