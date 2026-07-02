package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.prode.ServicioProde;
import java.util.Collections;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class ControladorProdeTest {

  @Test
  public void siNoHayUsuarioDebeRedirigirALogin() {
    HttpSession session = mock(HttpSession.class);
    when(session.getAttribute("USUARIO")).thenReturn(null);

    ControladorProde controlador = new ControladorProde(mock(ServicioProde.class));

    ModelAndView mav = controlador.verProde(session);

    assertThat(mav.getViewName(), is(equalTo("redirect:/login")));
  }

  @Test
  public void debeMostrarElProdeConPartidosYPuntaje() {
    ServicioProde servicio = mock(ServicioProde.class);
    HttpSession session = mock(HttpSession.class);
    Usuario usuario = new Usuario();
    usuario.setId(5L);
    when(session.getAttribute("USUARIO")).thenReturn(usuario);
    when(servicio.obtenerPartidosConPronosticos(5L)).thenReturn(Collections.emptyList());
    when(servicio.obtenerPuntaje(5L)).thenReturn(9);

    ControladorProde controlador = new ControladorProde(servicio);

    ModelAndView mav = controlador.verProde(session);

    assertThat(mav.getViewName(), is(equalTo("prode")));
    assertThat((Integer) mav.getModel().get("puntajeProde"), is(equalTo(9)));
  }

  @Test
  public void debeGuardarPronosticoYVolverAlProde() {
    ServicioProde servicio = mock(ServicioProde.class);
    HttpSession session = mock(HttpSession.class);
    RedirectAttributes ra = mock(RedirectAttributes.class);
    Usuario usuario = new Usuario();
    usuario.setId(5L);
    when(session.getAttribute("USUARIO")).thenReturn(usuario);

    ControladorProde controlador = new ControladorProde(servicio);

    ModelAndView mav = controlador.pronosticar(2L, 1, 0, session, ra);

    assertThat(mav.getViewName(), is(equalTo("redirect:/prode")));
    verify(servicio).pronosticar(5L, 2L, 1, 0);
    verify(ra).addFlashAttribute("mensaje", "Pronostico guardado.");
  }
}
