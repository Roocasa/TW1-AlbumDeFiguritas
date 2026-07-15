package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.ServicioPerfil;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.album.InventarioItemDTO;
import com.tallerwebi.dominio.album.OfertaIntercambioDTO;
import com.tallerwebi.dominio.album.PropuestaIntercambio;
import com.tallerwebi.dominio.album.ServicioIntercambio;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorIntercambioTest {

  private ServicioIntercambio servicioIntercambio;
  private ServicioPerfil servicioPerfil;
  private HttpSession session;
  private ControladorIntercambio controladorIntercambio;

  @BeforeEach
  public void init() {
    servicioIntercambio = mock(ServicioIntercambio.class);
    servicioPerfil = mock(ServicioPerfil.class);
    session = mock(HttpSession.class);
    controladorIntercambio = new ControladorIntercambio(servicioIntercambio, servicioPerfil);
  }

  @Test
  public void siNoHayUsuarioEnSesionDebeRedirigirALogin() {
    when(session.getAttribute("USUARIO")).thenReturn(null);

    ModelAndView modelAndView = controladorIntercambio.verIntercambios(1, 1, 1, 1, session);

    assertThat(modelAndView.getViewName(), is(equalTo("redirect:/login")));
    verify(servicioIntercambio, never()).obtenerPropuestasRecibidas(anyLong());
  }

  @Test
  public void deberiaPaginarLosCuatroBloquesDeIntercambiosDeFormaIndependiente() {
    Usuario usuario = usuarioConId(1L);
    when(session.getAttribute("USUARIO")).thenReturn(usuario);
    when(servicioPerfil.buscarUsuarioPorId(1L)).thenReturn(usuario);
    when(servicioIntercambio.obtenerPropuestasRecibidas(1L))
      .thenReturn(
        Arrays.asList(
          new PropuestaIntercambio(),
          new PropuestaIntercambio(),
          new PropuestaIntercambio(),
          new PropuestaIntercambio()
        )
      );
    when(servicioIntercambio.obtenerPropuestasEnviadas(1L))
      .thenReturn(
        Arrays.asList(
          new PropuestaIntercambio(),
          new PropuestaIntercambio(),
          new PropuestaIntercambio(),
          new PropuestaIntercambio()
        )
      );
    when(servicioIntercambio.obtenerFiguritasPropiasParaIntercambiar(1L))
      .thenReturn(
        Arrays.asList(inventarioItem(), inventarioItem(), inventarioItem(), inventarioItem())
      );
    when(servicioIntercambio.obtenerOfertasDeOtrosUsuarios(1L))
      .thenReturn(
        Collections.singletonList(
          new OfertaIntercambioDTO(
            usuarioConId(2L),
            Arrays.asList(inventarioItem(), inventarioItem(), inventarioItem(), inventarioItem())
          )
        )
      );

    ModelAndView modelAndView = controladorIntercambio.verIntercambios(2, 2, 2, 2, session);

    assertThat(((List<?>) modelAndView.getModel().get("propuestasRecibidas")).size(), equalTo(1));
    assertThat(((List<?>) modelAndView.getModel().get("propuestasEnviadas")).size(), equalTo(1));
    assertThat(((List<?>) modelAndView.getModel().get("misFiguritas")).size(), equalTo(1));
    assertThat(((List<?>) modelAndView.getModel().get("ofertas")).size(), equalTo(1));
    assertThat(modelAndView.getModel().get("paginaPropuestasRecibidas"), equalTo(2));
    assertThat(modelAndView.getModel().get("totalPaginasPropuestasRecibidas"), equalTo(2));
    assertThat(modelAndView.getModel().get("paginaPropuestasEnviadas"), equalTo(2));
    assertThat(modelAndView.getModel().get("totalPaginasPropuestasEnviadas"), equalTo(2));
    assertThat(modelAndView.getModel().get("paginaMisFiguritas"), equalTo(2));
    assertThat(modelAndView.getModel().get("totalPaginasMisFiguritas"), equalTo(2));
    assertThat(modelAndView.getModel().get("paginaOfertas"), equalTo(2));
    assertThat(modelAndView.getModel().get("totalPaginasOfertas"), equalTo(2));
  }

  @Test
  public void deberiaNormalizarPaginasFueraDeRango() {
    Usuario usuario = usuarioConId(1L);
    when(session.getAttribute("USUARIO")).thenReturn(usuario);
    when(servicioPerfil.buscarUsuarioPorId(1L)).thenReturn(usuario);
    when(servicioIntercambio.obtenerPropuestasRecibidas(1L)).thenReturn(Collections.emptyList());
    when(servicioIntercambio.obtenerPropuestasEnviadas(1L)).thenReturn(Collections.emptyList());
    when(servicioIntercambio.obtenerFiguritasPropiasParaIntercambiar(1L))
      .thenReturn(Collections.emptyList());
    when(servicioIntercambio.obtenerOfertasDeOtrosUsuarios(1L)).thenReturn(Collections.emptyList());

    ModelAndView modelAndView = controladorIntercambio.verIntercambios(0, 8, 0, 8, session);

    assertThat(modelAndView.getModel().get("paginaPropuestasRecibidas"), equalTo(1));
    assertThat(modelAndView.getModel().get("paginaPropuestasEnviadas"), equalTo(1));
    assertThat(modelAndView.getModel().get("paginaMisFiguritas"), equalTo(1));
    assertThat(modelAndView.getModel().get("paginaOfertas"), equalTo(1));
  }

  private Usuario usuarioConId(Long id) {
    Usuario usuario = new Usuario();
    usuario.setId(id);
    return usuario;
  }

  private InventarioItemDTO inventarioItem() {
    return new InventarioItemDTO(null, 2, false);
  }
}
