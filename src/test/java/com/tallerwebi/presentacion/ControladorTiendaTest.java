package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.PaqueteMonedas;
import com.tallerwebi.dominio.ServicioMercadoPago;
import com.tallerwebi.dominio.ServicioPerfil;
import com.tallerwebi.dominio.Usuario;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
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
  public void alComprarMonedasDebeRedirigirAMercadoPago() {
    ServicioPerfil servicioPerfil = mock(ServicioPerfil.class);
    ServicioMercadoPago servicioMercadoPago = mock(ServicioMercadoPago.class);
    HttpSession session = mock(HttpSession.class);
    HttpServletRequest request = mock(HttpServletRequest.class);
    RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
    Usuario usuario = new Usuario();
    usuario.setId(2L);
    PaqueteMonedas paquete = new PaqueteMonedas("monedas-500", "Pack inicial", 500, 500);

    when(session.getAttribute("USUARIO")).thenReturn(usuario);
    when(servicioPerfil.obtenerPaqueteMonedas("monedas-500")).thenReturn(paquete);
    when(request.getRequestURL())
      .thenReturn(new StringBuffer("http://localhost:8080/spring/comprar-monedas"));
    when(request.getRequestURI()).thenReturn("/spring/comprar-monedas");
    when(request.getContextPath()).thenReturn("/spring");
    when(servicioMercadoPago.crearUrlDePago(paquete, usuario, "http://localhost:8080/spring"))
      .thenReturn("https://www.mercadopago.com/checkout/v1/redirect?pref_id=123");

    ControladorTienda controladorTienda = new ControladorTienda(
      servicioPerfil,
      servicioMercadoPago
    );

    ModelAndView modelAndView = controladorTienda.comprarMonedas(
      "monedas-500",
      session,
      request,
      redirectAttributes
    );

    assertThat(
      modelAndView.getViewName(),
      is(equalTo("redirect:https://www.mercadopago.com/checkout/v1/redirect?pref_id=123"))
    );
    verify(session).setAttribute("PAQUETE_MERCADO_PAGO_PENDIENTE", "monedas-500");
    verify(servicioPerfil, never()).comprarMonedas(2L, "monedas-500");
  }

  @Test
  public void alVolverDeMercadoPagoAprobadoDebeAcreditarLasMonedas() {
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

    ModelAndView modelAndView = controladorTienda.retornoMercadoPago(
      "approved",
      null,
      "2:monedas-500",
      null,
      "123",
      null,
      session,
      redirectAttributes
    );

    assertThat(modelAndView.getViewName(), is(equalTo("redirect:/tienda")));
    verify(session).setAttribute("USUARIO", usuarioActualizado);
    verify(redirectAttributes)
      .addFlashAttribute("mensajeSobre", "Pago aprobado: se acreditaron tus monedas.");
    verify(redirectAttributes).addFlashAttribute("mostrarConfettiPago", true);
  }

  @Test
  public void alVolverDeMercadoPagoConCollectionStatusAprobadoDebeAcreditarLasMonedas() {
    ServicioPerfil servicioPerfil = mock(ServicioPerfil.class);
    HttpSession session = mock(HttpSession.class);
    RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
    Usuario usuario = new Usuario();
    usuario.setId(2L);
    Usuario usuarioActualizado = new Usuario();
    usuarioActualizado.setId(2L);
    usuarioActualizado.setMonedas(1000);

    when(session.getAttribute("USUARIO")).thenReturn(usuario);
    when(servicioPerfil.comprarMonedas(2L, "monedas-1000")).thenReturn(usuarioActualizado);

    ControladorTienda controladorTienda = new ControladorTienda(servicioPerfil);

    ModelAndView modelAndView = controladorTienda.retornoMercadoPago(
      null,
      "approved",
      "2:monedas-1000",
      null,
      "456",
      null,
      session,
      redirectAttributes
    );

    assertThat(modelAndView.getViewName(), is(equalTo("redirect:/tienda")));
    verify(session).setAttribute("USUARIO", usuarioActualizado);
    verify(redirectAttributes)
      .addFlashAttribute("mensajeSobre", "Pago aprobado: se acreditaron tus monedas.");
    verify(redirectAttributes).addFlashAttribute("mostrarConfettiPago", true);
  }

  @Test
  public void alVolverSinExternalReferenceDebeUsarElPaqueteDeLaUrl() {
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

    ModelAndView modelAndView = controladorTienda.retornoMercadoPago(
      "approved",
      null,
      null,
      "monedas-500",
      "789",
      null,
      session,
      redirectAttributes
    );

    assertThat(modelAndView.getViewName(), is(equalTo("redirect:/tienda")));
    verify(session).setAttribute("USUARIO", usuarioActualizado);
    verify(redirectAttributes)
      .addFlashAttribute("mensajeSobre", "Pago aprobado: se acreditaron tus monedas.");
    verify(redirectAttributes).addFlashAttribute("mostrarConfettiPago", true);
  }

  @Test
  public void alAcreditarPagoPendienteAprobadoDebeSumarLasMonedas() {
    ServicioPerfil servicioPerfil = mock(ServicioPerfil.class);
    ServicioMercadoPago servicioMercadoPago = mock(ServicioMercadoPago.class);
    HttpSession session = mock(HttpSession.class);
    RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
    Usuario usuario = new Usuario();
    usuario.setId(2L);
    Usuario usuarioActualizado = new Usuario();
    usuarioActualizado.setId(2L);
    usuarioActualizado.setMonedas(500);

    when(session.getAttribute("USUARIO")).thenReturn(usuario);
    when(servicioMercadoPago.existePagoAprobado(2L, "monedas-500")).thenReturn(true);
    when(servicioPerfil.comprarMonedas(2L, "monedas-500")).thenReturn(usuarioActualizado);

    ControladorTienda controladorTienda = new ControladorTienda(
      servicioPerfil,
      servicioMercadoPago
    );

    ModelAndView modelAndView = controladorTienda.acreditarPagoPendiente(
      "monedas-500",
      session,
      redirectAttributes
    );

    assertThat(modelAndView.getViewName(), is(equalTo("redirect:/tienda")));
    verify(session).setAttribute("USUARIO", usuarioActualizado);
    verify(session).removeAttribute("PAQUETE_MERCADO_PAGO_PENDIENTE");
    verify(redirectAttributes)
      .addFlashAttribute("mensajeSobre", "Pago aprobado: se acreditaron tus monedas.");
    verify(redirectAttributes).addFlashAttribute("mostrarConfettiPago", true);
  }
}
