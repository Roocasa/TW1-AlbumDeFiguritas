package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.ServicioPerfil;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.album.Album;
import com.tallerwebi.dominio.album.Figurita;
import com.tallerwebi.dominio.album.PaqueteServicio;
import com.tallerwebi.dominio.album.ServicioAlbum;
import com.tallerwebi.dominio.excepcion.CanjeFiguritasException;
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
  private ServicioPerfil servicioPerfilMock;
  private ServicioAlbum servicioAlbumMock;
  private HttpSession sessionMock;
  private HttpServletRequest requestMock;
  private RedirectAttributes redirectAttributesMock;

  @BeforeEach
  public void init() {
    paqueteServicioMock = mock(PaqueteServicio.class);
    servicioPerfilMock = mock(ServicioPerfil.class);
    servicioAlbumMock = mock(ServicioAlbum.class);
    sessionMock = mock(HttpSession.class);
    requestMock = mock(HttpServletRequest.class);
    redirectAttributesMock = mock(RedirectAttributes.class);

    when(requestMock.getSession()).thenReturn(sessionMock);

    controladorInventario =
      new ControladorInventario(paqueteServicioMock, servicioPerfilMock, servicioAlbumMock);
  }

  @Test
  public void dadoQueUnUsuarioNoTienePaquetesCuandoIntentaAbrirUnSobreEntoncesRecibeUnMensajeDeError()
    throws PaquetesInsuficientesException {
    Usuario usuarioMock = new Usuario();
    usuarioMock.setId(1L);
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioMock);
    when(servicioPerfilMock.otorgarPaquetesDiariosSiCorresponde(1L)).thenReturn(usuarioMock);

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

  @Test
  public void dadoQueElUsuarioNoTieneSobresCuandoCierraElAnuncioEntoncesRecibeUnSobre() {
    Usuario usuarioMock = new Usuario();
    usuarioMock.setId(1L);
    usuarioMock.setPaquetesDisponibles(0);

    Usuario usuarioActualizado = new Usuario();
    usuarioActualizado.setId(1L);
    usuarioActualizado.setPaquetesDisponibles(1);

    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioMock);
    when(servicioPerfilMock.otorgarSobrePorAnuncio(1L)).thenReturn(usuarioActualizado);

    ModelAndView modelAndView = controladorInventario.otorgarRecompensaPorAnuncio(
      sessionMock,
      redirectAttributesMock
    );

    assertThat(modelAndView.getViewName(), is(equalTo("redirect:/inventario")));
    verify(sessionMock, times(1)).setAttribute("USUARIO", usuarioActualizado);
    verify(redirectAttributesMock, times(1))
      .addFlashAttribute("mensajeSobre", "Cerraste el anuncio y te dimos 1 sobre comun.");
  }

  @Test
  public void cuandoCanjeaRepetidasPorPaqueteEntoncesSeMuestraElPopupDelSobreGanado()
    throws CanjeFiguritasException {
    Usuario usuarioMock = new Usuario();
    usuarioMock.setId(1L);
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioMock);

    ModelAndView modelAndView = controladorInventario.canjearRepetidasPorPaquete(
      sessionMock,
      redirectAttributesMock
    );

    assertThat(modelAndView.getViewName(), is(equalTo("redirect:/inventario?soloRepetidas=true")));
    verify(paqueteServicioMock, times(1)).canjearRepetidasPorPaquete(1L);
    verify(redirectAttributesMock, times(1)).addFlashAttribute("canjePaqueteExitoso", true);
  }

  @Test
  public void cuandoCanjeaRepetidasPorEscudoEntoncesSeMuestraElEscudoGanado()
    throws CanjeFiguritasException {
    Usuario usuarioMock = new Usuario();
    usuarioMock.setId(1L);
    Figurita escudo = new Figurita("Escudo de Argentina", "Argentina");

    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioMock);
    when(paqueteServicioMock.canjearRepetidasPorEscudo(1L)).thenReturn(escudo);

    ModelAndView modelAndView = controladorInventario.canjearRepetidasPorEscudo(
      sessionMock,
      redirectAttributesMock
    );

    assertThat(modelAndView.getViewName(), is(equalTo("redirect:/inventario?soloRepetidas=true")));
    verify(redirectAttributesMock, times(1)).addFlashAttribute("escudoCanjeado", escudo);
  }

  @Test
  public void cuandoPegaLaUltimaFiguritaEntoncesSeMuestraElCartelDeAlbumCompletado() {
    Usuario usuarioMock = new Usuario();
    usuarioMock.setId(1L);
    Album albumCompleto = new Album(usuarioMock);
    albumCompleto.setFiguritasFaltantes(0);

    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioMock);
    when(servicioAlbumMock.obtenerAlbumActualizado(1L)).thenReturn(albumCompleto);

    ModelAndView modelAndView = controladorInventario.pegarFigurita(
      576L,
      sessionMock,
      redirectAttributesMock
    );

    assertThat(modelAndView.getViewName(), is(equalTo("redirect:/inventario")));
    verify(paqueteServicioMock, times(1)).pegarFigurita(1L, 576L);
    verify(redirectAttributesMock, times(1)).addFlashAttribute("albumCompletado", true);
  }

  @Test
  public void cuandoCompraUnSobreConMonedasEntoncesActualizaLaSesion() {
    Usuario usuarioMock = new Usuario();
    usuarioMock.setId(1L);
    Usuario usuarioActualizado = new Usuario();
    usuarioActualizado.setId(1L);
    usuarioActualizado.setMonedas(10);
    usuarioActualizado.setPaquetesDisponibles(1);

    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioMock);
    when(servicioPerfilMock.comprarSobreConMonedas(1L)).thenReturn(usuarioActualizado);

    ModelAndView modelAndView = controladorInventario.comprarSobreConMonedas(
      sessionMock,
      redirectAttributesMock
    );

    assertThat(modelAndView.getViewName(), is(equalTo("redirect:/tienda")));
    verify(sessionMock).setAttribute("USUARIO", usuarioActualizado);
    verify(redirectAttributesMock)
      .addFlashAttribute("mensajeSobre", "Compraste 1 sobre comun con monedas.");
  }
}
