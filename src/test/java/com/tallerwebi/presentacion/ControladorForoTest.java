package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.ServicioPerfil;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.album.InventarioItemDTO;
import com.tallerwebi.dominio.excepcion.IntercambioFiguritasException;
import com.tallerwebi.dominio.foro.DonacionSolidaria;
import com.tallerwebi.dominio.foro.ForoPublicacionDTO;
import com.tallerwebi.dominio.foro.ServicioForo;
import java.nio.file.Path;
import java.util.Collections;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class ControladorForoTest {

  private ServicioForo servicioForo;
  private ServicioPerfil servicioPerfil;
  private HttpSession session;
  private RedirectAttributes redirectAttributes;
  private ControladorForo controladorForo;

  @BeforeEach
  public void init() {
    servicioForo = mock(ServicioForo.class);
    servicioPerfil = mock(ServicioPerfil.class);
    session = mock(HttpSession.class);
    redirectAttributes = mock(RedirectAttributes.class);
    controladorForo =
      new ControladorForo(servicioForo, servicioPerfil, Path.of("target", "foro-test-uploads"));
  }

  @Test
  public void siNoHayUsuarioEnSesionDebeRedirigirALogin() {
    when(session.getAttribute("USUARIO")).thenReturn(null);

    ModelAndView modelAndView = controladorForo.verForo(session);

    assertThat(modelAndView.getViewName(), is(equalTo("redirect:/login")));
    verify(servicioForo, never()).obtenerPublicaciones();
  }

  @Test
  public void siHayUsuarioEnSesionDebeMostrarElForoConSusDatos() {
    Usuario usuario = usuarioConId(1L);
    Usuario usuarioActualizado = usuarioConId(1L);
    when(session.getAttribute("USUARIO")).thenReturn(usuario);
    when(servicioPerfil.buscarUsuarioPorId(1L)).thenReturn(usuarioActualizado);
    when(servicioForo.obtenerPublicaciones())
      .thenReturn(Collections.<ForoPublicacionDTO>emptyList());
    when(servicioForo.obtenerFiguritasRepetidasParaDonar(1L))
      .thenReturn(Collections.<InventarioItemDTO>emptyList());
    when(servicioForo.obtenerDonacionesDisponibles(1L))
      .thenReturn(Collections.<DonacionSolidaria>emptyList());

    ModelAndView modelAndView = controladorForo.verForo(session);

    assertThat(modelAndView.getViewName(), is(equalTo("foro")));
    assertThat(modelAndView.getModel().get("publicaciones"), equalTo(Collections.emptyList()));
    assertThat(modelAndView.getModel().get("figuritasParaDonar"), equalTo(Collections.emptyList()));
    assertThat(modelAndView.getModel().get("donaciones"), equalTo(Collections.emptyList()));
    verify(session).setAttribute("USUARIO", usuarioActualizado);
  }

  @Test
  public void alPublicarDebeDelegarEnElServicioYVolverAlForo() {
    Usuario usuario = usuarioConId(3L);
    MockMultipartFile fotoVacia = new MockMultipartFile("foto", "", "image/png", new byte[0]);
    when(session.getAttribute("USUARIO")).thenReturn(usuario);

    ModelAndView modelAndView = controladorForo.publicar(
      "Hola comunidad",
      fotoVacia,
      session,
      redirectAttributes
    );

    assertThat(modelAndView.getViewName(), is(equalTo("redirect:/foro")));
    verify(servicioForo).publicar(3L, "Hola comunidad", null);
    verify(redirectAttributes).addFlashAttribute("mensajeExito", "Publicacion creada.");
  }

  @Test
  public void alComentarDebeDelegarEnElServicioYVolverAlForo() {
    Usuario usuario = usuarioConId(4L);
    when(session.getAttribute("USUARIO")).thenReturn(usuario);

    ModelAndView modelAndView = controladorForo.comentar(
      9L,
      "Buen dato",
      session,
      redirectAttributes
    );

    assertThat(modelAndView.getViewName(), is(equalTo("redirect:/foro")));
    verify(servicioForo).comentar(4L, 9L, "Buen dato");
    verify(redirectAttributes).addFlashAttribute("mensajeExito", "Comentario publicado.");
  }

  @Test
  public void alDonarUnaFiguritaDebeDelegarEnElServicioYVolverAlForo() throws Exception {
    Usuario usuario = usuarioConId(5L);
    when(session.getAttribute("USUARIO")).thenReturn(usuario);

    ModelAndView modelAndView = controladorForo.donarFigurita(12L, session, redirectAttributes);

    assertThat(modelAndView.getViewName(), is(equalTo("redirect:/foro")));
    verify(servicioForo).donarFigurita(5L, 12L);
    verify(redirectAttributes)
      .addFlashAttribute(
        "mensajeExito",
        "Tu figurita repetida quedo disponible para la comunidad."
      );
  }

  @Test
  public void siFallaLaDonacionDebeMostrarElErrorDelServicio() throws Exception {
    Usuario usuario = usuarioConId(5L);
    when(session.getAttribute("USUARIO")).thenReturn(usuario);
    doThrow(new IntercambioFiguritasException("Solo podes donar una figurita repetida."))
      .when(servicioForo)
      .donarFigurita(5L, 12L);

    ModelAndView modelAndView = controladorForo.donarFigurita(12L, session, redirectAttributes);

    assertThat(modelAndView.getViewName(), is(equalTo("redirect:/foro")));
    verify(redirectAttributes)
      .addFlashAttribute("error", "Solo podes donar una figurita repetida.");
  }

  @Test
  public void alReclamarUnaDonacionDebeActualizarLaSesionYVolverAlForo() throws Exception {
    Usuario usuario = usuarioConId(6L);
    Usuario usuarioActualizado = usuarioConId(6L);
    when(session.getAttribute("USUARIO")).thenReturn(usuario);
    when(servicioPerfil.buscarUsuarioPorId(6L)).thenReturn(usuarioActualizado);

    ModelAndView modelAndView = controladorForo.reclamarDonacion(50L, session, redirectAttributes);

    assertThat(modelAndView.getViewName(), is(equalTo("redirect:/foro")));
    verify(servicioForo).reclamarDonacion(6L, 50L);
    verify(session).setAttribute("USUARIO", usuarioActualizado);
    verify(redirectAttributes)
      .addFlashAttribute(
        "mensajeExito",
        "Reclamaste la figurita solidaria. Ya esta en tu inventario."
      );
  }

  private Usuario usuarioConId(Long id) {
    Usuario usuario = new Usuario();
    usuario.setId(id);
    return usuario;
  }
}
