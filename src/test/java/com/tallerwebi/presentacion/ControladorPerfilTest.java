package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.ServicioFotoPerfil;
import com.tallerwebi.dominio.ServicioPerfil;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.servlet.ModelAndView;

public class ControladorPerfilTest {

  private ControladorPerfil controladorPerfil;
  private ServicioPerfil servicioPerfil;
  private ServicioFotoPerfil servicioFotoPerfil;
  private HttpServletRequest request;
  private HttpSession session;

  @BeforeEach
  public void init() {
    servicioPerfil = mock(ServicioPerfil.class);
    servicioFotoPerfil = mock(ServicioFotoPerfil.class);
    request = mock(HttpServletRequest.class);
    session = mock(HttpSession.class);
    when(request.getSession()).thenReturn(session);
    controladorPerfil = new ControladorPerfil(servicioPerfil, servicioFotoPerfil);
  }

  @Test
  public void siNoHayUsuarioEnSesionDeberiaRedirigirALogin() {
    when(session.getAttribute("EMAIL")).thenReturn(null);

    ModelAndView modelAndView = controladorPerfil.verPerfil(request);

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/login"));
    verify(servicioPerfil, never()).buscarUsuarioPorEmail(anyString());
  }

  @Test
  public void siElUsuarioDeSesionNoExisteDeberiaRedirigirALogin() {
    when(session.getAttribute("EMAIL")).thenReturn("test@unlam.edu.ar");
    when(servicioPerfil.buscarUsuarioPorEmail("test@unlam.edu.ar")).thenReturn(null);

    ModelAndView modelAndView = controladorPerfil.verPerfil(request);

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/login"));
  }

  @Test
  public void siHayUsuarioEnSesionDeberiaMostrarElPerfil() {
    Usuario usuario = new Usuario();
    usuario.setId(3L);
    usuario.setEmail("test@unlam.edu.ar");
    when(session.getAttribute("EMAIL")).thenReturn("test@unlam.edu.ar");
    when(servicioPerfil.buscarUsuarioPorEmail("test@unlam.edu.ar")).thenReturn(usuario);
    when(servicioPerfil.otorgarPaquetesDiariosSiCorresponde(3L)).thenReturn(usuario);

    ModelAndView modelAndView = controladorPerfil.verPerfil(request);

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("perfil"));
    assertThat(modelAndView.getModel().get("usuario"), is(usuario));
  }

  @Test
  public void deberiaActualizarElEmailDelUsuarioYLaSesionCuandoLosDatosSonValidos()
    throws UsuarioExistente {
    Usuario usuario = new Usuario();
    usuario.setId(9L);
    usuario.setEmail("actual@test.com");

    Usuario usuarioActualizado = new Usuario();
    usuarioActualizado.setId(9L);
    usuarioActualizado.setEmail("nuevo@test.com");

    when(session.getAttribute("USUARIO")).thenReturn(usuario);
    when(servicioPerfil.buscarUsuarioPorId(9L)).thenReturn(usuario);
    when(servicioPerfil.actualizarEmail(9L, "nuevo@test.com", "123456"))
      .thenReturn(usuarioActualizado);

    ModelAndView modelAndView = controladorPerfil.actualizarEmail(
      "nuevo@test.com",
      "123456",
      request
    );

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("perfil"));
    assertThat(
      modelAndView.getModel().get("mensajeEmail"),
      equalTo("Tu email se actualizo correctamente.")
    );
    verify(session).setAttribute("USUARIO", usuarioActualizado);
    verify(session).setAttribute("EMAIL", "nuevo@test.com");
  }

  @Test
  public void deberiaMostrarErrorSiElEmailNuevoYaExiste() throws UsuarioExistente {
    Usuario usuario = new Usuario();
    usuario.setId(9L);
    usuario.setEmail("actual@test.com");

    when(session.getAttribute("USUARIO")).thenReturn(usuario);
    when(servicioPerfil.buscarUsuarioPorId(9L)).thenReturn(usuario);
    doThrow(new UsuarioExistente())
      .when(servicioPerfil)
      .actualizarEmail(9L, "nuevo@test.com", "123456");

    ModelAndView modelAndView = controladorPerfil.actualizarEmail(
      "nuevo@test.com",
      "123456",
      request
    );

    assertThat(
      modelAndView.getModel().get("errorEmail"),
      equalTo("Ese email ya esta registrado por otra cuenta.")
    );
  }

  @Test
  public void deberiaActualizarLaFotoDePerfil() throws Exception {
    Usuario usuario = new Usuario();
    usuario.setId(5L);
    usuario.setEmail("foto@test.com");

    Usuario usuarioActualizado = new Usuario();
    usuarioActualizado.setId(5L);
    usuarioActualizado.setEmail("foto@test.com");
    usuarioActualizado.setFotoPerfil("/uploads/perfiles/usuario-5.png");

    MockMultipartFile foto = new MockMultipartFile(
      "fotoPerfil",
      "avatar.png",
      "image/png",
      "hola".getBytes()
    );

    when(session.getAttribute("USUARIO")).thenReturn(usuario);
    when(servicioPerfil.buscarUsuarioPorId(5L)).thenReturn(usuario);
    when(
      servicioFotoPerfil.guardarFoto(
        eq(5L),
        eq("avatar.png"),
        eq("image/png"),
        any(byte[].class),
        isNull()
      )
    )
      .thenReturn("/uploads/perfiles/usuario-5.png");
    when(servicioPerfil.actualizarFotoPerfil(5L, "/uploads/perfiles/usuario-5.png"))
      .thenReturn(usuarioActualizado);

    ModelAndView modelAndView = controladorPerfil.actualizarFotoPerfil(foto, request);

    assertThat(
      modelAndView.getModel().get("mensajeFoto"),
      equalTo("La foto de perfil se actualizo correctamente.")
    );
    verify(session).setAttribute("USUARIO", usuarioActualizado);
  }

  @Test
  public void deberiaEliminarLaFotoDePerfilYActualizarLaSesion() throws Exception {
    Usuario usuario = new Usuario();
    usuario.setId(6L);
    usuario.setEmail("foto@test.com");
    usuario.setFotoPerfil("/uploads/perfiles/anterior.png");

    Usuario usuarioActualizado = new Usuario();
    usuarioActualizado.setId(6L);
    usuarioActualizado.setEmail("foto@test.com");
    usuarioActualizado.setFotoPerfil(null);

    when(session.getAttribute("USUARIO")).thenReturn(usuario);
    when(servicioPerfil.buscarUsuarioPorId(6L)).thenReturn(usuario);
    when(servicioPerfil.eliminarFotoPerfil(6L)).thenReturn(usuarioActualizado);

    ModelAndView modelAndView = controladorPerfil.eliminarFotoPerfil(request);

    assertThat(
      modelAndView.getModel().get("mensajeFoto"),
      equalTo("La foto de perfil se elimino correctamente.")
    );
    verify(servicioFotoPerfil).eliminarFoto("/uploads/perfiles/anterior.png");
    verify(session).setAttribute("USUARIO", usuarioActualizado);
  }

  @Test
  public void deberiaActualizarLaContrasenaYMostrarMensajeDeExito() {
    Usuario usuario = new Usuario();
    usuario.setId(11L);
    usuario.setEmail("clave@test.com");

    Usuario usuarioActualizado = new Usuario();
    usuarioActualizado.setId(11L);
    usuarioActualizado.setEmail("clave@test.com");

    when(session.getAttribute("USUARIO")).thenReturn(usuario);
    when(servicioPerfil.buscarUsuarioPorId(11L)).thenReturn(usuario);
    when(servicioPerfil.actualizarPassword(11L, "anterior", "nueva123", "nueva123"))
      .thenReturn(usuarioActualizado);

    ModelAndView modelAndView = controladorPerfil.actualizarPassword(
      "anterior",
      "nueva123",
      "nueva123",
      request
    );

    assertThat(
      modelAndView.getModel().get("mensajePassword"),
      equalTo("Tu contrasena se actualizo correctamente.")
    );
    verify(session).setAttribute("USUARIO", usuarioActualizado);
  }
}
