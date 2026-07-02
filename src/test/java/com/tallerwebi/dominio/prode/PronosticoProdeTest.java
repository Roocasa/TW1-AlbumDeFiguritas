package com.tallerwebi.dominio.prode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

public class PronosticoProdeTest {

  @Test
  public void debeDarSeisPuntosSiAciertaElMarcadorExacto() {
    PartidoProde partido = partidoFinalizado(2, 1);
    PronosticoProde pronostico = new PronosticoProde(null, partido, 2, 1);

    assertThat(pronostico.calcularPuntos(), equalTo(6));
  }

  @Test
  public void debeDarTresPuntosSiAciertaElGanador() {
    PartidoProde partido = partidoFinalizado(2, 1);
    PronosticoProde pronostico = new PronosticoProde(null, partido, 3, 0);

    assertThat(pronostico.calcularPuntos(), equalTo(3));
  }

  @Test
  public void debeDarTresPuntosSiAciertaEmpateConOtroMarcador() {
    PartidoProde partido = partidoFinalizado(1, 1);
    PronosticoProde pronostico = new PronosticoProde(null, partido, 2, 2);

    assertThat(pronostico.calcularPuntos(), equalTo(3));
  }

  @Test
  public void debeDarCeroPuntosSiElResultadoEsContrario() {
    PartidoProde partido = partidoFinalizado(0, 2);
    PronosticoProde pronostico = new PronosticoProde(null, partido, 1, 0);

    assertThat(pronostico.calcularPuntos(), equalTo(0));
  }

  private PartidoProde partidoFinalizado(Integer golesLocal, Integer golesVisitante) {
    PartidoProde partido = new PartidoProde();
    partido.actualizarResultado(golesLocal, golesVisitante);
    return partido;
  }
}
