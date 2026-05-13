package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.album.Figurita;
import com.tallerwebi.dominio.album.PaqueteServicio;
import com.tallerwebi.dominio.album.Rareza;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class ControladorPaqueteTest {

  private PaqueteServicio paqueteServicioMock;
  private RedirectAttributes redirectAttributesMock;
  private ControladorPaquete controladorPaquete;

  @BeforeEach
  public void init() {
    paqueteServicioMock = mock(PaqueteServicio.class);
    redirectAttributesMock = mock(RedirectAttributes.class);
    controladorPaquete = new ControladorPaquete(paqueteServicioMock);
  }

  @Test
  public void abrirUnPaqueteDeberiaRedirigirAInventarioConLasFiguritas() {
    List<Figurita> figuritas = List.of(
      new Figurita("Messi", "Argentina", Rareza.LEYENDA),
      new Figurita("Dibu", "Argentina", Rareza.ORO)
    );
    when(paqueteServicioMock.abrirPaquete()).thenReturn(figuritas);

    ModelAndView modelAndView = controladorPaquete.abrirUnPaquete(redirectAttributesMock);

    assertThat(modelAndView.getViewName(), equalTo("redirect:/inventario"));
    verify(redirectAttributesMock).addFlashAttribute("figuritasNuevas", figuritas);
    verify(redirectAttributesMock).addFlashAttribute("paqueteAbierto", true);
  }

  @Test
  public void siAbrirUnPaqueteFallaDeberiaRedirigirAInventarioConError() {
    when(paqueteServicioMock.abrirPaquete()).thenThrow(new RuntimeException("fallo"));

    ModelAndView modelAndView = controladorPaquete.abrirUnPaquete(redirectAttributesMock);

    assertThat(modelAndView.getViewName(), equalTo("redirect:/inventario"));
    verify(redirectAttributesMock)
      .addFlashAttribute(
        eq("error"),
        argThat(mensaje -> mensaje.toString().startsWith("Hubo un problema al abrir el sobre."))
      );
  }
}
