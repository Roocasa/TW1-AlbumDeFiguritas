package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.album.PaqueteServicio;
import com.tallerwebi.dominio.album.PaqueteServicioImpl;
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
    //Given
    Usuario usuarioMock = new Usuario(); // por defecto se instancia con 0 paquetes
    usuarioMock.setId(1L);
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioMock);

    when(paqueteServicioMock.abrirPaquete(usuarioMock.getId(), false))
      .thenThrow(new PaquetesInsuficientesException("No tenés paquetes disponibles."));

    //When el controlador intenta abrir el paquete
    ModelAndView modelAndView = controladorInventario.abrirUnPaquete(
      false,
      sessionMock,
      redirectAttributesMock
    );

    //Then verificamos el comportamiento del controlador ante el error

    assertThat(modelAndView.getViewName(), is(equalTo("redirect:/inventario"))); //verifico que se redirija al inventario

    // Se verifivs que el controlador haya llamado a ese metodo pasandole exactamente esos dos textos una sola vez
    verify(redirectAttributesMock, times(1))
      .addFlashAttribute("error", "No tenés paquetes disponibles.");
    // Se verifica que nunca se haya llamado a paqueteAbierto con ningun valor booleano
    verify(redirectAttributesMock, never()).addFlashAttribute(eq("paqueteAbierto"), anyBoolean());
  }

  @Test
  public void dadoQueUnUsuarioTienePaquetesCuandoAbroPaqueteSeLanzanCincoFiguritasEnElModal() {}
}
