package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.ServicioPerfil;
import com.tallerwebi.dominio.Usuario;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorTiendaTest {

  @Test
  public void siNoHayUsuarioEnSesionDebeRedirigirALogin() {
    ServicioPerfil servicioPerfil = mock(ServicioPerfil.class);
    HttpSession session = mock(HttpSession.class);
    when(session.getAttribute("USUARIO")).thenReturn(null);

    ControladorTienda controladorTienda = new ControladorTienda(servicioPerfil);

    ModelAndView modelAndView = controladorTienda.verTienda(session);

    assertThat(modelAndView.getViewName(), is(equalTo("redirect:/login")));
  }

  @Test
  public void siHayUsuarioEnSesionDebeMostrarLaTiendaConElCostoDelSobre() {
    ServicioPerfil servicioPerfil = mock(ServicioPerfil.class);
    HttpSession session = mock(HttpSession.class);
    Usuario usuario = new Usuario();
    usuario.setId(1L);
    when(session.getAttribute("USUARIO")).thenReturn(usuario);
    when(servicioPerfil.obtenerCostoSobreEnMonedas()).thenReturn(50);

    ControladorTienda controladorTienda = new ControladorTienda(servicioPerfil);

    ModelAndView modelAndView = controladorTienda.verTienda(session);

    assertThat(modelAndView.getViewName(), is(equalTo("tienda")));
    assertThat((Integer) modelAndView.getModel().get("costoSobreMonedas"), is(equalTo(50)));
  }
}
