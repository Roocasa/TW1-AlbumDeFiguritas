package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.album.PaqueteServicio;
import com.tallerwebi.dominio.excepcion.PaquetesInsuficientesException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class ControladorInventarioTest {

  private ControladorInventario controladorInventario;
  private PaqueteServicio paqueteServicioMock;
  private HttpSession sessionMock;
  private HttpServletRequest requestMock;
  private RedirectAttributes redirectAttributesMock;

  @BeforeEach
  public void init() {
    paqueteServicioMock = mock(PaqueteServicio.class);
    sessionMock = mock(HttpSession.class);
    requestMock = mock(HttpServletRequest.class);
    redirectAttributesMock = mock(RedirectAttributes.class);

    when(requestMock.getSession()).thenReturn(sessionMock);

    controladorInventario = new ControladorInventario(paqueteServicioMock);
  }

  @Test
  public void dadoQueUnUsuarioNoTienePaquetesCuandoIntentaAbrirUnSobreEntoncesRecibeUnMensajeDeError()
    throws PaquetesInsuficientesException {
    Usuario usuarioMock = new Usuario();
    usuarioMock.setId(1L);
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioMock);

    when(paqueteServicioMock.abrirPaquete(usuarioMock.getId()))
      .thenThrow(new PaquetesInsuficientesException("No tenes paquetes disponibles."));

    ModelAndView modelAndView = controladorInventario.abrirUnPaquete(
      sessionMock,
      redirectAttributesMock
    );

    assertThat(modelAndView.getViewName(), is(equalTo("redirect:/inventario")));
    verify(redirectAttributesMock, times(1))
      .addFlashAttribute("error", "No tenes paquetes disponibles.");
    verify(redirectAttributesMock, never()).addFlashAttribute(eq("paqueteAbierto"), anyBoolean());
  }
}
