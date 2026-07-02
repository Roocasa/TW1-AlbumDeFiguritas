package com.tallerwebi.dominio.prode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.Usuario;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ServicioProdeTest {

  @Test
  public void debeGuardarUnPronosticoNuevo() {
    RepositorioProde repositorioProde = mock(RepositorioProde.class);
    RepositorioUsuario repositorioUsuario = mock(RepositorioUsuario.class);
    Usuario usuario = usuario(1L);
    PartidoProde partido = partido(2L);
    when(repositorioUsuario.buscarPorId(1L)).thenReturn(usuario);
    when(repositorioProde.buscarPartido(2L)).thenReturn(partido);
    when(repositorioProde.buscarPronostico(usuario, partido)).thenReturn(null);

    ServicioProde servicio = new ServicioProdeImpl(
      repositorioProde,
      repositorioUsuario,
      () -> Collections.emptyList()
    );

    servicio.pronosticar(1L, 2L, 2, 1);

    verify(repositorioProde).guardarPronostico(any(PronosticoProde.class));
  }

  @Test
  public void debeActualizarUnPronosticoExistente() {
    RepositorioProde repositorioProde = mock(RepositorioProde.class);
    RepositorioUsuario repositorioUsuario = mock(RepositorioUsuario.class);
    Usuario usuario = usuario(1L);
    PartidoProde partido = partido(2L);
    PronosticoProde pronostico = new PronosticoProde(usuario, partido, 0, 0);
    pronostico.setPuntos(6);
    pronostico.setPuntuado(true);
    when(repositorioUsuario.buscarPorId(1L)).thenReturn(usuario);
    when(repositorioProde.buscarPartido(2L)).thenReturn(partido);
    when(repositorioProde.buscarPronostico(usuario, partido)).thenReturn(pronostico);

    ServicioProde servicio = new ServicioProdeImpl(
      repositorioProde,
      repositorioUsuario,
      () -> Collections.emptyList()
    );

    servicio.pronosticar(1L, 2L, 2, 1);

    assertThat(pronostico.getGolesLocal(), equalTo(2));
    assertThat(pronostico.getGolesVisitante(), equalTo(1));
    assertThat(pronostico.getPuntos(), equalTo(0));
    assertThat(pronostico.isPuntuado(), equalTo(false));
    verify(repositorioProde).modificarPronostico(pronostico);
  }

  @Test
  public void noDebePronosticarConGolesInvalidos() {
    ServicioProde servicio = new ServicioProdeImpl(
      mock(RepositorioProde.class),
      mock(RepositorioUsuario.class),
      () -> Collections.emptyList()
    );

    Assertions.assertThrows(
      IllegalArgumentException.class,
      () -> servicio.pronosticar(1L, 2L, -1, 0)
    );
  }

  @Test
  public void noDebePronosticarSiNoExisteUsuario() {
    RepositorioUsuario repositorioUsuario = mock(RepositorioUsuario.class);
    when(repositorioUsuario.buscarPorId(1L)).thenReturn(null);

    ServicioProde servicio = new ServicioProdeImpl(
      mock(RepositorioProde.class),
      repositorioUsuario,
      () -> Collections.emptyList()
    );

    Assertions.assertThrows(
      IllegalArgumentException.class,
      () -> servicio.pronosticar(1L, 2L, 1, 0)
    );
  }

  @Test
  public void noDebePronosticarSiNoExistePartido() {
    RepositorioProde repositorioProde = mock(RepositorioProde.class);
    RepositorioUsuario repositorioUsuario = mock(RepositorioUsuario.class);
    when(repositorioUsuario.buscarPorId(1L)).thenReturn(usuario(1L));
    when(repositorioProde.buscarPartido(2L)).thenReturn(null);

    ServicioProde servicio = new ServicioProdeImpl(
      repositorioProde,
      repositorioUsuario,
      () -> Collections.emptyList()
    );

    Assertions.assertThrows(
      IllegalArgumentException.class,
      () -> servicio.pronosticar(1L, 2L, 1, 0)
    );
  }

  @Test
  public void noDebePronosticarUnPartidoFinalizado() {
    RepositorioProde repositorioProde = mock(RepositorioProde.class);
    RepositorioUsuario repositorioUsuario = mock(RepositorioUsuario.class);
    Usuario usuario = usuario(1L);
    PartidoProde partido = partido(2L);
    partido.actualizarResultado(1, 0);
    when(repositorioUsuario.buscarPorId(1L)).thenReturn(usuario);
    when(repositorioProde.buscarPartido(2L)).thenReturn(partido);

    ServicioProde servicio = new ServicioProdeImpl(
      repositorioProde,
      repositorioUsuario,
      () -> Collections.emptyList()
    );

    Assertions.assertThrows(
      IllegalArgumentException.class,
      () -> servicio.pronosticar(1L, 2L, 2, 1)
    );
  }

  @Test
  public void debeListarPartidosConPronosticoDelUsuario() {
    RepositorioProde repositorioProde = mock(RepositorioProde.class);
    RepositorioUsuario repositorioUsuario = mock(RepositorioUsuario.class);
    Usuario usuario = usuario(1L);
    PartidoProde partido = partido(2L);
    PronosticoProde pronostico = new PronosticoProde(usuario, partido, 1, 1);
    when(repositorioUsuario.buscarPorId(1L)).thenReturn(usuario);
    when(repositorioProde.buscarPartidos()).thenReturn(Collections.singletonList(partido));
    when(repositorioProde.buscarPronostico(usuario, partido)).thenReturn(pronostico);

    ServicioProde servicio = new ServicioProdeImpl(
      repositorioProde,
      repositorioUsuario,
      () -> Collections.emptyList()
    );

    PartidoProdeDTO item = servicio.obtenerPartidosConPronosticos(1L).get(0);

    assertThat(item.getPartido(), equalTo(partido));
    assertThat(item.getPronostico(), equalTo(pronostico));
  }

  @Test
  public void debeActualizarResultadoYPuntuarPronosticos() {
    RepositorioProde repositorioProde = mock(RepositorioProde.class);
    RepositorioUsuario repositorioUsuario = mock(RepositorioUsuario.class);
    PartidoProde partido = partido(2L);
    Usuario usuario = usuario(1L);
    PronosticoProde exacto = new PronosticoProde(usuario, partido, 3, 1);
    PronosticoProde ganador = new PronosticoProde(usuario, partido, 2, 0);
    ResultadoPartidoApi resultado = new ResultadoPartidoApi(
      99L,
      "Argentina",
      "Argelia",
      LocalDateTime.now(),
      true,
      3,
      1
    );

    when(repositorioProde.buscarPartidoPorIdApi(99L)).thenReturn(partido);
    when(repositorioProde.buscarPronosticosPorPartido(partido))
      .thenReturn(Arrays.asList(exacto, ganador));

    ServicioProde servicio = new ServicioProdeImpl(
      repositorioProde,
      repositorioUsuario,
      () -> Collections.singletonList(resultado)
    );

    int actualizados = servicio.actualizarResultados();

    assertThat(actualizados, equalTo(1));
    assertThat(exacto.getPuntos(), equalTo(6));
    assertThat(ganador.getPuntos(), equalTo(3));
    verify(repositorioProde).modificarPartido(partido);
    verify(repositorioProde, times(2)).modificarPronostico(any(PronosticoProde.class));
  }

  @Test
  public void debeCrearPartidoDeApiPeroNoPuntuarSiNoFinalizo() {
    RepositorioProde repositorioProde = mock(RepositorioProde.class);
    ResultadoPartidoApi resultado = new ResultadoPartidoApi(
      100L,
      "Mexico",
      "Sudafrica",
      LocalDateTime.now(),
      false,
      null,
      null
    );
    when(repositorioProde.buscarPartidoPorIdApi(100L)).thenReturn(null);

    ServicioProde servicio = new ServicioProdeImpl(
      repositorioProde,
      mock(RepositorioUsuario.class),
      () -> Collections.singletonList(resultado)
    );

    int actualizados = servicio.actualizarResultados();

    assertThat(actualizados, equalTo(0));
    verify(repositorioProde).guardarPartido(any(PartidoProde.class));
    verify(repositorioProde, never()).modificarPartido(any(PartidoProde.class));
  }

  @Test
  public void debeSumarElPuntajeDelUsuario() {
    RepositorioProde repositorioProde = mock(RepositorioProde.class);
    PronosticoProde uno = new PronosticoProde();
    uno.setPuntos(6);
    PronosticoProde dos = new PronosticoProde();
    dos.setPuntos(3);
    when(repositorioProde.buscarPronosticosPorUsuario(1L)).thenReturn(Arrays.asList(uno, dos));

    ServicioProde servicio = new ServicioProdeImpl(
      repositorioProde,
      mock(RepositorioUsuario.class),
      () -> Collections.emptyList()
    );

    assertThat(servicio.obtenerPuntaje(1L), equalTo(9));
  }

  private Usuario usuario(Long id) {
    Usuario usuario = new Usuario();
    usuario.setId(id);
    return usuario;
  }

  private PartidoProde partido(Long id) {
    PartidoProde partido = new PartidoProde();
    partido.setId(id);
    partido.setLocal("Argentina");
    partido.setVisitante("Argelia");
    return partido;
  }
}
