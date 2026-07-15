package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.album.HistorialIntercambioDTO;
import com.tallerwebi.dominio.album.HistorialSobre;
import com.tallerwebi.dominio.album.PaqueteServicio;
import com.tallerwebi.dominio.album.ServicioIntercambio;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorHistorialTest {

  private PaqueteServicio paqueteServicio;
  private ServicioIntercambio servicioIntercambio;
  private HttpSession session;
  private ControladorHistorial controladorHistorial;

  @BeforeEach
  public void init() {
    paqueteServicio = mock(PaqueteServicio.class);
    servicioIntercambio = mock(ServicioIntercambio.class);
    session = mock(HttpSession.class);
    controladorHistorial = new ControladorHistorial(paqueteServicio, servicioIntercambio);
  }

  @Test
  public void siNoHayUsuarioEnSesionDebeRedirigirALogin() {
    when(session.getAttribute("USUARIO")).thenReturn(null);

    ModelAndView modelAndView = controladorHistorial.verHistorial(1, 1, session);

    assertThat(modelAndView.getViewName(), is(equalTo("redirect:/login")));
    verify(paqueteServicio, never()).obtenerHistorialSobres(org.mockito.ArgumentMatchers.anyLong());
    verify(servicioIntercambio, never())
      .obtenerHistorialIntercambios(org.mockito.ArgumentMatchers.anyLong());
  }

  @Test
  public void deberiaMostrarLaPantallaDeHistorialSinRegistros() {
    Usuario usuario = usuarioConId(1L);
    when(session.getAttribute("USUARIO")).thenReturn(usuario);
    when(paqueteServicio.obtenerHistorialSobres(1L)).thenReturn(Collections.emptyList());
    when(servicioIntercambio.obtenerHistorialIntercambios(1L)).thenReturn(Collections.emptyList());

    ModelAndView modelAndView = controladorHistorial.verHistorial(1, 1, session);

    assertThat(modelAndView.getViewName(), is(equalTo("historial")));
    assertThat(modelAndView.getModel().get("historialSobres"), equalTo(Collections.emptyList()));
    assertThat(
      modelAndView.getModel().get("historialIntercambios"),
      equalTo(Collections.emptyList())
    );
    assertThat(modelAndView.getModel().get("paginaSobres"), equalTo(1));
    assertThat(modelAndView.getModel().get("totalPaginasSobres"), equalTo(1));
    assertThat(modelAndView.getModel().get("paginaIntercambios"), equalTo(1));
    assertThat(modelAndView.getModel().get("totalPaginasIntercambios"), equalTo(1));
  }

  @Test
  public void deberiaPaginarSobresEIntercambiosConDosFilasPorPagina() {
    Usuario usuario = usuarioConId(1L);
    when(session.getAttribute("USUARIO")).thenReturn(usuario);
    when(paqueteServicio.obtenerHistorialSobres(1L))
      .thenReturn(
        Arrays.asList(
          new HistorialSobre(usuario),
          new HistorialSobre(usuario),
          new HistorialSobre(usuario),
          new HistorialSobre(usuario),
          new HistorialSobre(usuario)
        )
      );
    when(servicioIntercambio.obtenerHistorialIntercambios(1L))
      .thenReturn(
        Arrays.asList(
          historialIntercambio(),
          historialIntercambio(),
          historialIntercambio(),
          historialIntercambio()
        )
      );

    ModelAndView modelAndView = controladorHistorial.verHistorial(2, 2, session);

    assertThat(((List<?>) modelAndView.getModel().get("historialSobres")).size(), equalTo(2));
    assertThat(((List<?>) modelAndView.getModel().get("historialIntercambios")).size(), equalTo(2));
    assertThat(modelAndView.getModel().get("paginaSobres"), equalTo(2));
    assertThat(modelAndView.getModel().get("totalPaginasSobres"), equalTo(3));
    assertThat(modelAndView.getModel().get("paginaIntercambios"), equalTo(2));
    assertThat(modelAndView.getModel().get("totalPaginasIntercambios"), equalTo(2));
  }

  @Test
  public void deberiaNormalizarPaginasFueraDeRango() {
    Usuario usuario = usuarioConId(1L);
    when(session.getAttribute("USUARIO")).thenReturn(usuario);
    when(paqueteServicio.obtenerHistorialSobres(1L))
      .thenReturn(Arrays.asList(new HistorialSobre(usuario), new HistorialSobre(usuario)));
    when(servicioIntercambio.obtenerHistorialIntercambios(1L))
      .thenReturn(Arrays.asList(historialIntercambio()));

    ModelAndView modelAndView = controladorHistorial.verHistorial(0, 8, session);

    assertThat(modelAndView.getModel().get("paginaSobres"), equalTo(1));
    assertThat(modelAndView.getModel().get("paginaIntercambios"), equalTo(1));
  }

  private Usuario usuarioConId(Long id) {
    Usuario usuario = new Usuario();
    usuario.setId(id);
    return usuario;
  }

  private HistorialIntercambioDTO historialIntercambio() {
    return new HistorialIntercambioDTO(null, null, null, null, null);
  }
}
