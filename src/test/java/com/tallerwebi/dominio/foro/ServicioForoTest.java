package com.tallerwebi.dominio.foro;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.album.Figurita;
import com.tallerwebi.dominio.album.InventarioItemDTO;
import com.tallerwebi.dominio.album.PaqueteServicio;
import com.tallerwebi.dominio.album.RelacionFiguritaUsuario;
import com.tallerwebi.dominio.album.RepositorioInventario;
import com.tallerwebi.dominio.album.ServicioAlbum;
import com.tallerwebi.dominio.excepcion.IntercambioFiguritasException;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class ServicioForoTest {

  private RepositorioForo repositorioForo;
  private RepositorioUsuario repositorioUsuario;
  private RepositorioInventario repositorioInventario;
  private PaqueteServicio paqueteServicio;
  private ServicioAlbum servicioAlbum;
  private ServicioForo servicioForo;

  @BeforeEach
  public void init() {
    repositorioForo = mock(RepositorioForo.class);
    repositorioUsuario = mock(RepositorioUsuario.class);
    repositorioInventario = mock(RepositorioInventario.class);
    paqueteServicio = mock(PaqueteServicio.class);
    servicioAlbum = mock(ServicioAlbum.class);
    servicioForo =
      new ServicioForoImpl(
        repositorioForo,
        repositorioUsuario,
        repositorioInventario,
        paqueteServicio,
        servicioAlbum
      );
  }

  @Test
  public void deberiaGuardarUnaPublicacionConElContenidoLimpioYLaImagen() {
    Usuario autor = usuarioConId(1L);
    when(repositorioUsuario.buscarPorId(1L)).thenReturn(autor);

    servicioForo.publicar(1L, "  Hola foro  ", "/uploads/foro/foto.png");

    ArgumentCaptor<ForoPublicacion> captor = ArgumentCaptor.forClass(ForoPublicacion.class);
    verify(repositorioForo).guardarPublicacion(captor.capture());
    assertThat(captor.getValue().getAutor(), is(autor));
    assertThat(captor.getValue().getContenido(), equalTo("Hola foro"));
    assertThat(captor.getValue().getImagenUrl(), equalTo("/uploads/foro/foto.png"));
  }

  @Test
  public void noDeberiaPublicarSiNoHayTextoNiImagen() {
    Usuario autor = usuarioConId(1L);
    when(repositorioUsuario.buscarPorId(1L)).thenReturn(autor);

    Assertions.assertThrows(
      IllegalArgumentException.class,
      () -> servicioForo.publicar(1L, "   ", null)
    );

    verify(repositorioForo, never()).guardarPublicacion(any(ForoPublicacion.class));
  }

  @Test
  public void deberiaGuardarUnComentarioCuandoLaPublicacionExiste() {
    Usuario autor = usuarioConId(2L);
    ForoPublicacion publicacion = new ForoPublicacion(usuarioConId(1L), "Post", null);
    publicacion.setId(9L);
    when(repositorioUsuario.buscarPorId(2L)).thenReturn(autor);
    when(repositorioForo.buscarPublicacionPorId(9L)).thenReturn(publicacion);

    servicioForo.comentar(2L, 9L, "  Me sirve  ");

    ArgumentCaptor<ForoComentario> captor = ArgumentCaptor.forClass(ForoComentario.class);
    verify(repositorioForo).guardarComentario(captor.capture());
    assertThat(captor.getValue().getAutor(), is(autor));
    assertThat(captor.getValue().getPublicacion(), is(publicacion));
    assertThat(captor.getValue().getContenido(), equalTo("Me sirve"));
  }

  @Test
  public void deberiaGuardarUnaDonacionCuandoLaFiguritaEstaRepetida() throws Exception {
    Usuario donante = usuarioConId(1L);
    Figurita figurita = figuritaConId(10L);
    RelacionFiguritaUsuario relacion = relacionConId(20L, donante, figurita);

    when(repositorioUsuario.buscarPorId(1L)).thenReturn(donante);
    when(paqueteServicio.obtenerFiguritasDelInventario(1L))
      .thenReturn(Collections.singletonList(new InventarioItemDTO(figurita, 2, true)));
    when(repositorioInventario.buscarRelacionDisponible(1L, 10L)).thenReturn(relacion);
    when(repositorioForo.buscarDonacionDisponiblePorRelacion(20L)).thenReturn(null);

    servicioForo.donarFigurita(1L, 10L);

    ArgumentCaptor<DonacionSolidaria> captor = ArgumentCaptor.forClass(DonacionSolidaria.class);
    verify(repositorioForo).guardarDonacion(captor.capture());
    assertThat(captor.getValue().getDonante(), is(donante));
    assertThat(captor.getValue().getFigurita(), is(figurita));
    assertThat(captor.getValue().getRelacionDonada(), is(relacion));
    assertThat(captor.getValue().getEstado(), is(EstadoDonacionSolidaria.DISPONIBLE));
  }

  @Test
  public void noDeberiaDonarUnaFiguritaQueNoEstaRepetida() {
    Usuario donante = usuarioConId(1L);
    Figurita figurita = figuritaConId(10L);
    when(repositorioUsuario.buscarPorId(1L)).thenReturn(donante);
    when(paqueteServicio.obtenerFiguritasDelInventario(1L))
      .thenReturn(Collections.singletonList(new InventarioItemDTO(figurita, 1, true)));

    Assertions.assertThrows(
      IntercambioFiguritasException.class,
      () -> servicioForo.donarFigurita(1L, 10L)
    );

    verify(repositorioForo, never()).guardarDonacion(any(DonacionSolidaria.class));
  }

  @Test
  public void deberiaReclamarUnaDonacionYTransferirLaFigurita() throws Exception {
    Usuario donante = usuarioConId(1L);
    Usuario reclamante = usuarioConId(2L);
    RelacionFiguritaUsuario relacion = relacionConId(30L, donante, figuritaConId(10L));
    DonacionSolidaria donacion = new DonacionSolidaria(donante, relacion);
    donacion.setId(99L);

    when(repositorioUsuario.buscarPorId(2L)).thenReturn(reclamante);
    when(repositorioForo.buscarDonacionPorId(99L)).thenReturn(donacion);

    servicioForo.reclamarDonacion(2L, 99L);

    assertThat(relacion.getPropietario(), is(reclamante));
    assertThat(donacion.getReclamante(), is(reclamante));
    assertThat(donacion.getEstado(), is(EstadoDonacionSolidaria.RECLAMADA));
    verify(repositorioInventario).modificar(relacion);
    verify(repositorioForo).modificarDonacion(donacion);
    verify(servicioAlbum).actualizarEstadisticas(1L);
    verify(servicioAlbum).actualizarEstadisticas(2L);
  }

  @Test
  public void noDeberiaMostrarComoDisponiblesLasDonacionesDelMismoUsuario() {
    Usuario usuario = usuarioConId(1L);
    Usuario otroUsuario = usuarioConId(2L);
    DonacionSolidaria propia = new DonacionSolidaria(
      usuario,
      relacionConId(1L, usuario, figuritaConId(1L))
    );
    DonacionSolidaria ajena = new DonacionSolidaria(
      otroUsuario,
      relacionConId(2L, otroUsuario, figuritaConId(2L))
    );
    when(repositorioForo.buscarDonacionesDisponibles()).thenReturn(Arrays.asList(propia, ajena));

    assertThat(
      servicioForo.obtenerDonacionesDisponibles(1L),
      equalTo(Collections.singletonList(ajena))
    );
  }

  private Usuario usuarioConId(Long id) {
    Usuario usuario = new Usuario();
    usuario.setId(id);
    return usuario;
  }

  private Figurita figuritaConId(Long id) {
    Figurita figurita = new Figurita();
    figurita.setId(id);
    figurita.setOrdenAlbum(id.intValue());
    return figurita;
  }

  private RelacionFiguritaUsuario relacionConId(Long id, Usuario propietario, Figurita figurita) {
    RelacionFiguritaUsuario relacion = new RelacionFiguritaUsuario(propietario, figurita);
    relacion.setId(id);
    return relacion;
  }
}
