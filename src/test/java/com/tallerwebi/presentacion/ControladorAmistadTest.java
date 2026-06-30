package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.amistad.ServicioAmistad;
import com.tallerwebi.dominio.amistad.SolicitudAmistad;
import java.util.Arrays;
import java.util.Collections;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class ControladorAmistadTest {

  private ServicioAmistad servicioAmistad;
  private HttpSession session;
  private RedirectAttributes redirectAttributes;
  private ControladorAmistad controladorAmistad;

  @BeforeEach
  public void init() {
    servicioAmistad = mock(ServicioAmistad.class);
    session = mock(HttpSession.class);
    redirectAttributes = mock(RedirectAttributes.class);
    controladorAmistad = new ControladorAmistad(servicioAmistad);
  }

  @Test
  public void siNoHayUsuarioEnSesionDebeRedirigirALogin() {
    when(session.getAttribute("USUARIO")).thenReturn(null);

    ModelAndView modelAndView = controladorAmistad.verAmigos(1, 1, 1, 1, session);

    assertThat(modelAndView.getViewName(), is(equalTo("redirect:/login")));
    verify(servicioAmistad, never()).obtenerAmigos(anyLong());
  }

  @Test
  public void siHayUsuarioDebeMostrarLaPantallaDeAmigos() {
    Usuario usuario = usuarioConId(1L);
    when(session.getAttribute("USUARIO")).thenReturn(usuario);
    when(servicioAmistad.obtenerSolicitudesRecibidas(1L)).thenReturn(Collections.emptyList());
    when(servicioAmistad.obtenerSolicitudesEnviadas(1L)).thenReturn(Collections.emptyList());
    when(servicioAmistad.obtenerAmigos(1L)).thenReturn(Collections.emptyList());
    when(servicioAmistad.obtenerUsuariosParaAgregar(1L)).thenReturn(Collections.emptyList());

    ModelAndView modelAndView = controladorAmistad.verAmigos(1, 1, 1, 1, session);

    assertThat(modelAndView.getViewName(), is(equalTo("amigos")));
    assertThat(
      modelAndView.getModel().get("solicitudesRecibidas"),
      equalTo(Collections.emptyList())
    );
    assertThat(
      modelAndView.getModel().get("solicitudesEnviadas"),
      equalTo(Collections.emptyList())
    );
    assertThat(modelAndView.getModel().get("amigos"), equalTo(Collections.emptyList()));
    assertThat(
      modelAndView.getModel().get("usuariosParaAgregar"),
      equalTo(Collections.emptyList())
    );
    assertThat(modelAndView.getModel().get("paginaSolicitudesRecibidas"), equalTo(1));
    assertThat(modelAndView.getModel().get("totalPaginasSolicitudesRecibidas"), equalTo(1));
    assertThat(modelAndView.getModel().get("paginaAmigos"), equalTo(1));
    assertThat(modelAndView.getModel().get("totalPaginasAmigos"), equalTo(1));
    assertThat(modelAndView.getModel().get("paginaUsuarios"), equalTo(1));
    assertThat(modelAndView.getModel().get("totalPaginasUsuarios"), equalTo(1));
    assertThat(modelAndView.getModel().get("paginaSolicitudesEnviadas"), equalTo(1));
    assertThat(modelAndView.getModel().get("totalPaginasSolicitudesEnviadas"), equalTo(1));
  }

  @Test
  public void deberiaPaginarCadaPanelDeAmigosDeFormaIndependiente() {
    Usuario usuario = usuarioConId(1L);
    when(session.getAttribute("USUARIO")).thenReturn(usuario);
    when(servicioAmistad.obtenerSolicitudesRecibidas(1L))
      .thenReturn(
        Arrays.asList(
          solicitudConId(1L),
          solicitudConId(2L),
          solicitudConId(3L),
          solicitudConId(4L)
        )
      );
    when(servicioAmistad.obtenerAmigos(1L))
      .thenReturn(
        Arrays.asList(usuarioConId(10L), usuarioConId(11L), usuarioConId(12L), usuarioConId(13L))
      );
    when(servicioAmistad.obtenerUsuariosParaAgregar(1L))
      .thenReturn(
        Arrays.asList(usuarioConId(20L), usuarioConId(21L), usuarioConId(22L), usuarioConId(23L))
      );
    when(servicioAmistad.obtenerSolicitudesEnviadas(1L))
      .thenReturn(
        Arrays.asList(
          solicitudConId(30L),
          solicitudConId(31L),
          solicitudConId(32L),
          solicitudConId(33L)
        )
      );

    ModelAndView modelAndView = controladorAmistad.verAmigos(2, 2, 2, 2, session);

    assertThat(
      ((java.util.List<?>) modelAndView.getModel().get("solicitudesRecibidas")).size(),
      equalTo(1)
    );
    assertThat(((java.util.List<?>) modelAndView.getModel().get("amigos")).size(), equalTo(1));
    assertThat(
      ((java.util.List<?>) modelAndView.getModel().get("usuariosParaAgregar")).size(),
      equalTo(1)
    );
    assertThat(
      ((java.util.List<?>) modelAndView.getModel().get("solicitudesEnviadas")).size(),
      equalTo(1)
    );
    assertThat(modelAndView.getModel().get("paginaSolicitudesRecibidas"), equalTo(2));
    assertThat(modelAndView.getModel().get("totalPaginasSolicitudesRecibidas"), equalTo(2));
    assertThat(modelAndView.getModel().get("paginaAmigos"), equalTo(2));
    assertThat(modelAndView.getModel().get("totalPaginasAmigos"), equalTo(2));
    assertThat(modelAndView.getModel().get("paginaUsuarios"), equalTo(2));
    assertThat(modelAndView.getModel().get("totalPaginasUsuarios"), equalTo(2));
    assertThat(modelAndView.getModel().get("paginaSolicitudesEnviadas"), equalTo(2));
    assertThat(modelAndView.getModel().get("totalPaginasSolicitudesEnviadas"), equalTo(2));
  }

  @Test
  public void alEnviarSolicitudDesdeAmigosDebeVolverAAmigos() {
    Usuario usuario = usuarioConId(1L);
    when(session.getAttribute("USUARIO")).thenReturn(usuario);

    ModelAndView modelAndView = controladorAmistad.enviarSolicitud(
      2L,
      "amigos",
      1,
      2,
      3,
      4,
      session,
      redirectAttributes
    );

    assertThat(modelAndView.getViewName(), is(equalTo("redirect:/amigos")));
    verify(servicioAmistad).enviarSolicitud(1L, 2L);
    verify(redirectAttributes).addFlashAttribute("mensajeExito", "Solicitud enviada.");
    verify(redirectAttributes).addAttribute("paginaSolicitudesRecibidas", 1);
    verify(redirectAttributes).addAttribute("paginaAmigos", 2);
    verify(redirectAttributes).addAttribute("paginaUsuarios", 3);
    verify(redirectAttributes).addAttribute("paginaSolicitudesEnviadas", 4);
  }

  @Test
  public void alEnviarSolicitudDesdeForoDebeVolverAlForo() {
    Usuario usuario = usuarioConId(1L);
    when(session.getAttribute("USUARIO")).thenReturn(usuario);

    ModelAndView modelAndView = controladorAmistad.enviarSolicitud(
      2L,
      "foro",
      1,
      1,
      1,
      1,
      session,
      redirectAttributes
    );

    assertThat(modelAndView.getViewName(), is(equalTo("redirect:/foro")));
  }

  @Test
  public void siFallaEnviarSolicitudDebeMostrarError() {
    Usuario usuario = usuarioConId(1L);
    when(session.getAttribute("USUARIO")).thenReturn(usuario);
    doThrow(new IllegalArgumentException("Ya hay una solicitud pendiente."))
      .when(servicioAmistad)
      .enviarSolicitud(1L, 2L);

    ModelAndView modelAndView = controladorAmistad.enviarSolicitud(
      2L,
      "amigos",
      1,
      1,
      1,
      1,
      session,
      redirectAttributes
    );

    assertThat(modelAndView.getViewName(), is(equalTo("redirect:/amigos")));
    verify(redirectAttributes).addFlashAttribute("error", "Ya hay una solicitud pendiente.");
  }

  @Test
  public void alAceptarSolicitudDebeDelegarEnElServicio() {
    Usuario usuario = usuarioConId(1L);
    when(session.getAttribute("USUARIO")).thenReturn(usuario);

    ModelAndView modelAndView = controladorAmistad.aceptarSolicitud(
      7L,
      2,
      1,
      1,
      1,
      session,
      redirectAttributes
    );

    assertThat(modelAndView.getViewName(), is(equalTo("redirect:/amigos")));
    verify(servicioAmistad).aceptarSolicitud(1L, 7L);
    verify(redirectAttributes).addFlashAttribute("mensajeExito", "Solicitud aceptada.");
    verify(redirectAttributes).addAttribute("paginaSolicitudesRecibidas", 2);
  }

  @Test
  public void alRechazarSolicitudDebeDelegarEnElServicio() {
    Usuario usuario = usuarioConId(1L);
    when(session.getAttribute("USUARIO")).thenReturn(usuario);

    ModelAndView modelAndView = controladorAmistad.rechazarSolicitud(
      8L,
      1,
      2,
      1,
      1,
      session,
      redirectAttributes
    );

    assertThat(modelAndView.getViewName(), is(equalTo("redirect:/amigos")));
    verify(servicioAmistad).rechazarSolicitud(1L, 8L);
    verify(redirectAttributes).addFlashAttribute("mensajeExito", "Solicitud rechazada.");
    verify(redirectAttributes).addAttribute("paginaAmigos", 2);
  }

  private Usuario usuarioConId(Long id) {
    Usuario usuario = new Usuario();
    usuario.setId(id);
    return usuario;
  }

  private SolicitudAmistad solicitudConId(Long id) {
    SolicitudAmistad solicitud = new SolicitudAmistad(usuarioConId(id), usuarioConId(id + 100L));
    solicitud.setId(id);
    return solicitud;
  }
}
