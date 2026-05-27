package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.album.Album;
import com.tallerwebi.dominio.album.AlbumSlotDTO;
import com.tallerwebi.dominio.album.Figurita;
import com.tallerwebi.dominio.album.Pais;
import com.tallerwebi.dominio.album.PaqueteServicio;
import com.tallerwebi.dominio.album.ServicioAlbum;
import com.tallerwebi.dominio.album.ServicioPais;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class ControladorAlbumTest {

  private ControladorAlbum controladorAlbum;
  private ServicioPais servicioPais;
  private ServicioAlbum servicioAlbum;
  private PaqueteServicio paqueteServicio;
  private HttpSession session;
  private RedirectAttributes redirectAttributes;

  @BeforeEach
  public void init() {
    servicioPais = mock(ServicioPais.class);
    servicioAlbum = mock(ServicioAlbum.class);
    paqueteServicio = mock(PaqueteServicio.class);
    session = mock(HttpSession.class);
    redirectAttributes = mock(RedirectAttributes.class);

    controladorAlbum = new ControladorAlbum(servicioPais, servicioAlbum, paqueteServicio);
  }

  @Test
  public void cuandoPegaDesdeAlbumPaisEntoncesVuelveAlMismoSlotSinMensajeDeExito() {
    Usuario usuario = new Usuario();
    usuario.setId(1L);
    when(session.getAttribute("USUARIO")).thenReturn(usuario);

    ModelAndView modelAndView = controladorAlbum.pegarDesdeElAlbum(
      "ARG",
      442L,
      session,
      redirectAttributes
    );

    assertThat(modelAndView.getViewName(), is(equalTo("redirect:/album/pais/ARG#figurita-442")));
    verify(paqueteServicio, times(1)).pegarFigurita(1L, 442L);
    verify(redirectAttributes, never()).addFlashAttribute(eq("mensajeExito"), anyString());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void cuandoAbreElAlbumEntoncesIncluyeSlotsPorPaisYAlbumCompletadoEnElModelo() {
    Usuario usuario = new Usuario();
    usuario.setId(2L);
    Pais argentina = new Pais("ARG", "Argentina", "J", 37, "ar");
    Album albumCompleto = new Album(usuario);
    albumCompleto.setTotalFiguritas(576);
    albumCompleto.setFiguritasPegadas(576);
    albumCompleto.setFiguritasFaltantes(0);
    Figurita figurita = new Figurita("Lionel Messi", "Argentina");
    figurita.setId(442L);

    when(session.getAttribute("USUARIO")).thenReturn(usuario);
    when(servicioPais.buscarPaises(null, null)).thenReturn(List.of(argentina));
    when(servicioPais.agruparPorGrupo(List.of(argentina)))
      .thenReturn(Map.of("J", List.of(argentina)));
    when(servicioPais.listarGrupos()).thenReturn(List.of("J"));
    when(servicioAlbum.obtenerAlbumActualizado(2L)).thenReturn(albumCompleto);
    when(servicioAlbum.obtenerPegadasPorPais(2L)).thenReturn(Map.of("ARG", 12));
    when(servicioAlbum.obtenerPendientesPorPais(2L)).thenReturn(Map.of());
    when(servicioAlbum.obtenerSlotsPorPais(2L, "ARG"))
      .thenReturn(List.of(new AlbumSlotDTO(figurita, true, false)));

    ModelAndView modelAndView = controladorAlbum.irAAlbum(session, null, null);

    assertThat(modelAndView.getViewName(), is(equalTo("album")));
    assertThat((Album) modelAndView.getModel().get("album"), is(albumCompleto));
    Map<String, List<AlbumSlotDTO>> slotsPorPais = (Map<String, List<AlbumSlotDTO>>) modelAndView
      .getModel()
      .get("albumSlotsPorPais");
    assertThat(slotsPorPais, hasKey("ARG"));
  }
}
