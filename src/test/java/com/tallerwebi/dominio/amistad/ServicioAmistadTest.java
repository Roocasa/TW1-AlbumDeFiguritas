package com.tallerwebi.dominio.amistad;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.Usuario;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class ServicioAmistadTest {

  private RepositorioAmistad repositorioAmistad;
  private RepositorioUsuario repositorioUsuario;
  private ServicioAmistad servicioAmistad;

  @BeforeEach
  public void init() {
    repositorioAmistad = mock(RepositorioAmistad.class);
    repositorioUsuario = mock(RepositorioUsuario.class);
    servicioAmistad = new ServicioAmistadImpl(repositorioAmistad, repositorioUsuario);
  }

  @Test
  public void deberiaEnviarUnaSolicitudSiNoExisteRelacionPrevia() {
    Usuario solicitante = usuarioConId(1L);
    Usuario receptor = usuarioConId(2L);
    when(repositorioUsuario.buscarPorId(1L)).thenReturn(solicitante);
    when(repositorioUsuario.buscarPorId(2L)).thenReturn(receptor);
    when(repositorioAmistad.buscarEntreUsuarios(1L, 2L)).thenReturn(null);

    servicioAmistad.enviarSolicitud(1L, 2L);

    ArgumentCaptor<SolicitudAmistad> captor = ArgumentCaptor.forClass(SolicitudAmistad.class);
    verify(repositorioAmistad).guardar(captor.capture());
    assertThat(captor.getValue().getSolicitante(), is(solicitante));
    assertThat(captor.getValue().getReceptor(), is(receptor));
    assertThat(captor.getValue().getEstado(), is(EstadoSolicitudAmistad.PENDIENTE));
  }

  @Test
  public void noDeberiaEnviarSolicitudSiYaSonAmigos() {
    Usuario solicitante = usuarioConId(1L);
    Usuario receptor = usuarioConId(2L);
    SolicitudAmistad existente = new SolicitudAmistad(solicitante, receptor);
    existente.aceptar();
    when(repositorioUsuario.buscarPorId(1L)).thenReturn(solicitante);
    when(repositorioUsuario.buscarPorId(2L)).thenReturn(receptor);
    when(repositorioAmistad.buscarEntreUsuarios(1L, 2L)).thenReturn(existente);

    Assertions.assertThrows(
      IllegalArgumentException.class,
      () -> servicioAmistad.enviarSolicitud(1L, 2L)
    );

    verify(repositorioAmistad, never()).guardar(any(SolicitudAmistad.class));
  }

  @Test
  public void deberiaReactivarUnaSolicitudRechazada() {
    Usuario solicitante = usuarioConId(1L);
    Usuario receptor = usuarioConId(2L);
    SolicitudAmistad existente = new SolicitudAmistad(receptor, solicitante);
    existente.rechazar();
    when(repositorioUsuario.buscarPorId(1L)).thenReturn(solicitante);
    when(repositorioUsuario.buscarPorId(2L)).thenReturn(receptor);
    when(repositorioAmistad.buscarEntreUsuarios(1L, 2L)).thenReturn(existente);

    servicioAmistad.enviarSolicitud(1L, 2L);

    assertThat(existente.getSolicitante(), is(solicitante));
    assertThat(existente.getReceptor(), is(receptor));
    assertThat(existente.getEstado(), is(EstadoSolicitudAmistad.PENDIENTE));
    verify(repositorioAmistad).modificar(existente);
  }

  @Test
  public void deberiaAceptarUnaSolicitudPendienteRecibida() {
    Usuario solicitante = usuarioConId(1L);
    Usuario receptor = usuarioConId(2L);
    SolicitudAmistad solicitud = new SolicitudAmistad(solicitante, receptor);
    when(repositorioAmistad.buscarPorId(10L)).thenReturn(solicitud);

    servicioAmistad.aceptarSolicitud(2L, 10L);

    assertThat(solicitud.getEstado(), is(EstadoSolicitudAmistad.ACEPTADA));
    verify(repositorioAmistad).modificar(solicitud);
  }

  @Test
  public void noDeberiaResponderUnaSolicitudDeOtroUsuario() {
    SolicitudAmistad solicitud = new SolicitudAmistad(usuarioConId(1L), usuarioConId(2L));
    when(repositorioAmistad.buscarPorId(10L)).thenReturn(solicitud);

    Assertions.assertThrows(
      IllegalArgumentException.class,
      () -> servicioAmistad.rechazarSolicitud(3L, 10L)
    );

    verify(repositorioAmistad, never()).modificar(solicitud);
  }

  @Test
  public void deberiaObtenerAmigosDesdeSolicitudesAceptadas() {
    Usuario usuario = usuarioConId(1L);
    Usuario amigoUno = usuarioConId(2L);
    Usuario amigoDos = usuarioConId(3L);
    SolicitudAmistad enviada = new SolicitudAmistad(usuario, amigoUno);
    SolicitudAmistad recibida = new SolicitudAmistad(amigoDos, usuario);
    enviada.aceptar();
    recibida.aceptar();
    when(repositorioAmistad.buscarAceptadas(1L)).thenReturn(Arrays.asList(enviada, recibida));

    assertThat(servicioAmistad.obtenerAmigos(1L), containsInAnyOrder(amigoUno, amigoDos));
  }

  @Test
  public void deberiaExcluirUsuariosConRelacionPendienteOAceptadaAlAgregar() {
    Usuario usuarioDisponible = usuarioConId(4L);
    SolicitudAmistad pendiente = new SolicitudAmistad(usuarioConId(1L), usuarioConId(2L));
    SolicitudAmistad aceptada = new SolicitudAmistad(usuarioConId(3L), usuarioConId(1L));
    aceptada.aceptar();
    when(repositorioAmistad.buscarRelacionadas(1L)).thenReturn(Arrays.asList(pendiente, aceptada));
    when(repositorioUsuario.buscarTodosExcepto(1L))
      .thenReturn(Arrays.asList(usuarioConId(2L), usuarioConId(3L), usuarioDisponible));

    assertThat(servicioAmistad.obtenerUsuariosParaAgregar(1L), contains(usuarioDisponible));
  }

  @Test
  public void noDeberiaEnviarSolicitudAlMismoUsuario() {
    Assertions.assertThrows(
      IllegalArgumentException.class,
      () -> servicioAmistad.enviarSolicitud(1L, 1L)
    );

    verify(repositorioUsuario, never()).buscarPorId(anyLong());
    verify(repositorioAmistad, never()).guardar(any(SolicitudAmistad.class));
  }

  private Usuario usuarioConId(Long id) {
    Usuario usuario = new Usuario();
    usuario.setId(id);
    usuario.setEmail("usuario" + id + "@test.com");
    return usuario;
  }
}
