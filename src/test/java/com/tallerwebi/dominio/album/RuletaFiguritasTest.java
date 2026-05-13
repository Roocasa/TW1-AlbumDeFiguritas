package com.tallerwebi.dominio.album;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

public class RuletaFiguritasTest {

  @Test
  public void deberiaDevolverRarezaLeyendaSiLaTiradaEsMayorQue95() {
    //Given > Dado que tengo mi ruleta
    RuletaFiguritas ruleta = new RuletaFiguritas();

    //When > Cuando la ruleta saca un 96, que esta dentro del 5% de probabilidad
    Rareza resultado = ruleta.calcularRareza(96);

    //Then > El algoritmo me tiene que entregar la categoria LEYENDA
    assertThat(resultado, is(Rareza.LEYENDA));
  }

  @Test
  public void deberiaDevolverRarezaOroSiLaTiradaEsMayorQue85YMenorQue96() {
    //Given > Dado que tengo mi ruleta
    RuletaFiguritas ruleta = new RuletaFiguritas();

    //When > Cuando la ruleta saca un 86, que esta dentro del 10% de probabilidad
    Rareza resultado = ruleta.calcularRareza(86);

    //Then > El algoritmo me tiene que entregar la categoria ORO
    assertThat(resultado, is(Rareza.ORO));
  }

  @Test
  public void deberiaDevolverRarezaPlataSiLaTiradaEsMayorQue60YMenorQue86() {
    //Given > Dado que tengo mi ruleta
    RuletaFiguritas ruleta = new RuletaFiguritas();

    //When > Cuando la ruleta saca un 86, que esta dentro del 25% de probabilidad
    Rareza resultado = ruleta.calcularRareza(61);

    //Then > El algoritmo me tiene que entregar la categoria PLATA
    assertThat(resultado, is(Rareza.PLATA));
  }

  @Test
  public void deberiaDevolverRarezaComunSiLaTiradaEsMenorQue61() {
    //Given > Dado que tengo mi ruleta
    RuletaFiguritas ruleta = new RuletaFiguritas();

    //When > Cuando la ruleta saca un 60, que esta dentro del 60% de probabilidad
    Rareza resultado = ruleta.calcularRareza(60);

    //Then > El algoritmo me tiene que entregar la categoria COMUN
    assertThat(resultado, is(Rareza.COMUN));
  }

  @Test
  public void deberiaDevolverRarezaLeyendaEnPaquetePremiumSiLaTiradaEsMayorQue85() {
    RuletaFiguritas ruleta = new RuletaFiguritas();

    Rareza resultado = ruleta.calcularRarezaPremium(86);

    assertThat(resultado, is(Rareza.LEYENDA));
  }

  @Test
  public void deberiaDevolverRarezaOroEnPaquetePremiumSiLaTiradaEsMayorQue50YMenorQue86() {
    RuletaFiguritas ruleta = new RuletaFiguritas();

    Rareza resultado = ruleta.calcularRarezaPremium(51);

    assertThat(resultado, is(Rareza.ORO));
  }

  @Test
  public void deberiaDevolverRarezaPlataEnPaquetePremiumSiLaTiradaEsMenorQue51() {
    RuletaFiguritas ruleta = new RuletaFiguritas();

    Rareza resultado = ruleta.calcularRarezaPremium(50);

    assertThat(resultado, is(Rareza.PLATA));
  }
}
