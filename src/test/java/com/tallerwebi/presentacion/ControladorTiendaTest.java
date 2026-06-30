package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.PaqueteMonedas;
import com.tallerwebi.dominio.ServicioPerfil;
import com.tallerwebi.dominio.Usuario;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    List<PaqueteMonedas> paquetesMonedas = Arrays.asList(
      new PaqueteMonedas("monedas-500", "Pack inicial", 500, 500)
    );
    when(session.getAttribute("USUARIO")).thenReturn(usuario);
    when(servicioPerfil.obtenerCostoSobreEnMonedas()).thenReturn(50);
    when(servicioPerfil.obtenerPaquetesMonedas()).thenReturn(paquetesMonedas);

    ControladorTienda controladorTienda = new ControladorTienda(servicioPerfil);

    ModelAndView modelAndView = controladorTienda.verTienda(session);

    assertThat(modelAndView.getViewName(), is(equalTo("tienda")));
    assertThat((Integer) modelAndView.getModel().get("costoSobreMonedas"), is(equalTo(50)));
    assertThat(modelAndView.getModel().get("paquetesMonedas"), is(equalTo(paquetesMonedas)));
  }

  @Test
  public void alComprarMonedasDebeActualizarElUsuarioEnSesionYVolverALaTienda() {
    ServicioPerfil servicioPerfil = mock(ServicioPerfil.class);
    HttpSession session = mock(HttpSession.class);
    RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
    Usuario usuario = new Usuario();
    usuario.setId(2L);
    Usuario usuarioActualizado = new Usuario();
    usuarioActualizado.setId(2L);
    usuarioActualizado.setMonedas(500);

    when(session.getAttribute("USUARIO")).thenReturn(usuario);
    when(servicioPerfil.comprarMonedas(2L, "monedas-500")).thenReturn(usuarioActualizado);

    ControladorTienda controladorTienda = new ControladorTienda(servicioPerfil);

    ModelAndView modelAndView = controladorTienda.comprarMonedas(
      "monedas-500",
      session,
      redirectAttributes
    );

    assertThat(modelAndView.getViewName(), is(equalTo("redirect:/tienda")));
    verify(session).setAttribute("USUARIO", usuarioActualizado);
    verify(redirectAttributes)
      .addFlashAttribute("mensajeSobre", "Compra simulada: se acreditaron tus monedas.");
  }
}
