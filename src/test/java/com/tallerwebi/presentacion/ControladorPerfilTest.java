package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.ServicioPerfil;
import com.tallerwebi.dominio.Usuario;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorPerfilTest {

  private ControladorPerfil controladorPerfil;
  private ServicioPerfil servicioPerfil;
  private HttpServletRequest request;
  private HttpSession session;

  @BeforeEach
  public void init() {
    servicioPerfil = mock(ServicioPerfil.class);
    request = mock(HttpServletRequest.class);
    session = mock(HttpSession.class);
    when(request.getSession()).thenReturn(session);
    controladorPerfil = new ControladorPerfil(servicioPerfil);
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
    usuario.setEmail("test@unlam.edu.ar");
    when(session.getAttribute("EMAIL")).thenReturn("test@unlam.edu.ar");
    when(servicioPerfil.buscarUsuarioPorEmail("test@unlam.edu.ar")).thenReturn(usuario);

    ModelAndView modelAndView = controladorPerfil.verPerfil(request);

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("perfil"));
    assertThat(modelAndView.getModel().get("usuario"), is(usuario));
  }
}
