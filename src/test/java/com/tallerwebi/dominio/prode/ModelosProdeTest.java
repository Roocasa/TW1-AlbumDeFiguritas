package com.tallerwebi.dominio.prode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.tallerwebi.dominio.Usuario;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class ModelosProdeTest {

  @Test
  public void debeExponerLosDatosDeUnPartido() {
    LocalDateTime fecha = LocalDateTime.of(2026, 6, 11, 16, 0);
    PartidoProde partido = new PartidoProde(1001L, "Mexico", "Sudafrica", fecha);

    partido.setId(9L);
    partido.setIdApi(1002L);
    partido.setLocal("Argentina");
    partido.setVisitante("Argelia");
    partido.setFecha(fecha.plusDays(1));
    partido.setEstado(EstadoPartidoProde.PROGRAMADO);
    partido.setGolesLocal(2);
    partido.setGolesVisitante(1);

    assertThat(partido.getId(), equalTo(9L));
    assertThat(partido.getIdApi(), equalTo(1002L));
    assertThat(partido.getLocal(), equalTo("Argentina"));
    assertThat(partido.getVisitante(), equalTo("Argelia"));
    assertThat(partido.getFecha(), equalTo(fecha.plusDays(1)));
    assertThat(partido.getEstado(), equalTo(EstadoPartidoProde.PROGRAMADO));
    assertThat(partido.getGolesLocal(), equalTo(2));
    assertThat(partido.getGolesVisitante(), equalTo(1));
    assertThat(partido.estaFinalizado(), equalTo(false));
  }

  @Test
  public void debeFormatearLaFechaDelPartido() {
    PartidoProde partido = new PartidoProde();
    partido.setFecha(LocalDateTime.of(2026, 7, 2, 19, 30));

    assertThat(partido.getFechaFormateada(), equalTo("02/07/2026 19:30"));
  }

  @Test
  public void debeExponerLosDatosDeUnPronostico() {
    Usuario usuario = new Usuario();
    PartidoProde partido = new PartidoProde();
    PronosticoProde pronostico = new PronosticoProde();

    pronostico.setId(8L);
    pronostico.setUsuario(usuario);
    pronostico.setPartido(partido);
    pronostico.setGolesLocal(3);
    pronostico.setGolesVisitante(2);
    pronostico.setPuntos(6);
    pronostico.setPuntuado(true);

    assertThat(pronostico.getId(), equalTo(8L));
    assertThat(pronostico.getUsuario(), equalTo(usuario));
    assertThat(pronostico.getPartido(), equalTo(partido));
    assertThat(pronostico.getGolesLocal(), equalTo(3));
    assertThat(pronostico.getGolesVisitante(), equalTo(2));
    assertThat(pronostico.getPuntos(), equalTo(6));
    assertThat(pronostico.isPuntuado(), equalTo(true));
  }

  @Test
  public void debePuntuarConCeroSiElPartidoNoFinalizo() {
    PartidoProde partido = new PartidoProde();
    PronosticoProde pronostico = new PronosticoProde(null, partido, 1, 0);

    pronostico.puntuar();

    assertThat(pronostico.getPuntos(), equalTo(0));
    assertThat(pronostico.isPuntuado(), equalTo(true));
  }

  @Test
  public void debeExponerLosDatosDeResultadoApi() {
    LocalDateTime fecha = LocalDateTime.of(2026, 6, 16, 22, 0);
    ResultadoPartidoApi resultado = new ResultadoPartidoApi(
      99L,
      "Argentina",
      "Argelia",
      fecha,
      true,
      2,
      0
    );

    assertThat(resultado.getIdApi(), equalTo(99L));
    assertThat(resultado.getLocal(), equalTo("Argentina"));
    assertThat(resultado.getVisitante(), equalTo("Argelia"));
    assertThat(resultado.getFecha(), equalTo(fecha));
    assertThat(resultado.isFinalizado(), equalTo(true));
    assertThat(resultado.getGolesLocal(), equalTo(2));
    assertThat(resultado.getGolesVisitante(), equalTo(0));
  }
}
