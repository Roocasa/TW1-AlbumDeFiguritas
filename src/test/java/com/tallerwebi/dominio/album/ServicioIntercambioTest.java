package com.tallerwebi.dominio.album;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.IntercambioFiguritasException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioIntercambioTest {

  private RepositorioUsuario repositorioUsuario;
  private RepositorioInventario repositorioInventario;
  private RepositorioPropuestaIntercambio repositorioPropuestaIntercambio;
  private PaqueteServicio paqueteServicio;
  private ServicioAlbum servicioAlbum;
  private ServicioIntercambio servicioIntercambio;

  @BeforeEach
  public void init() {
    repositorioUsuario = mock(RepositorioUsuario.class);
    repositorioInventario = mock(RepositorioInventario.class);
    repositorioPropuestaIntercambio = mock(RepositorioPropuestaIntercambio.class);
    paqueteServicio = mock(PaqueteServicio.class);
    servicioAlbum = mock(ServicioAlbum.class);

    servicioIntercambio =
      new ServicioIntercambioImpl(
        repositorioUsuario,
        repositorioInventario,
        repositorioPropuestaIntercambio,
        paqueteServicio,
        servicioAlbum
      );
  }

  @Test
  public void alObtenerOfertasSoloMuestraFiguritasQueLeFaltanAlUsuarioYConMayorCantidad() {
    Usuario usuarioActual = crearUsuario(1L, "actual@test.com");
    Usuario ofertante = crearUsuario(2L, "ofertante@test.com");
    Figurita figuritaYaPegada = crearFigurita(10L, "Figurita ya pegada");
    Figurita figuritaFaltanteConMenosRepetidas = crearFigurita(11L, "Figurita faltante menor");
    Figurita figuritaFaltanteConMasRepetidas = crearFigurita(12L, "Figurita faltante mayor");

    when(repositorioUsuario.buscarPorId(usuarioActual.getId())).thenReturn(usuarioActual);
    when(repositorioUsuario.buscarTodosExcepto(usuarioActual.getId()))
      .thenReturn(List.of(ofertante));
    when(repositorioInventario.buscarFiguritasPegadasPorUsuario(usuarioActual))
      .thenReturn(List.of(new RelacionFiguritaUsuario(usuarioActual, figuritaYaPegada)));
    when(paqueteServicio.obtenerFiguritasDelInventario(ofertante.getId()))
      .thenReturn(
        List.of(
          new InventarioItemDTO(figuritaYaPegada, 5, false),
          new InventarioItemDTO(figuritaFaltanteConMenosRepetidas, 2, false),
          new InventarioItemDTO(figuritaFaltanteConMasRepetidas, 4, false)
        )
      );

    List<OfertaIntercambioDTO> ofertas = servicioIntercambio.obtenerOfertasDeOtrosUsuarios(
      usuarioActual.getId()
    );

    assertThat(ofertas, hasSize(1));
    assertThat(ofertas.get(0).getFiguritasRepetidas(), hasSize(1));
    assertThat(
      ofertas.get(0).getFiguritasRepetidas().get(0).getFigurita(),
      equalTo(figuritaFaltanteConMasRepetidas)
    );
  }

  @Test
  public void alCancelarUnaPropuestaEnviadaPendienteLaMarcaComoCancelada()
    throws IntercambioFiguritasException {
    Usuario solicitante = crearUsuario(1L, "solicitante@test.com");
    Usuario receptor = crearUsuario(2L, "receptor@test.com");
    PropuestaIntercambio propuesta = new PropuestaIntercambio(
      solicitante,
      receptor,
      crearFigurita(10L, "Ofrecida"),
      crearFigurita(11L, "Solicitada")
    );

    when(repositorioPropuestaIntercambio.buscarPorId(7L)).thenReturn(propuesta);

    servicioIntercambio.cancelarPropuesta(solicitante.getId(), 7L);

    assertThat(propuesta.getEstado(), is(EstadoPropuestaIntercambio.CANCELADA));
    verify(repositorioPropuestaIntercambio).modificar(propuesta);
  }

  @Test
  public void noPermiteCancelarUnaPropuestaEnviadaPorOtroUsuario() {
    Usuario solicitante = crearUsuario(1L, "solicitante@test.com");
    Usuario otroUsuario = crearUsuario(3L, "otro@test.com");
    Usuario receptor = crearUsuario(2L, "receptor@test.com");
    PropuestaIntercambio propuesta = new PropuestaIntercambio(
      solicitante,
      receptor,
      crearFigurita(10L, "Ofrecida"),
      crearFigurita(11L, "Solicitada")
    );

    when(repositorioPropuestaIntercambio.buscarPorId(7L)).thenReturn(propuesta);

    IntercambioFiguritasException excepcion = assertThrows(
      IntercambioFiguritasException.class,
      () -> servicioIntercambio.cancelarPropuesta(otroUsuario.getId(), 7L)
    );

    assertThat(excepcion.getMessage(), is("No podes cancelar una propuesta de otro usuario."));
    assertThat(propuesta.getEstado(), is(EstadoPropuestaIntercambio.PENDIENTE));
    verify(repositorioPropuestaIntercambio, never()).modificar(any(PropuestaIntercambio.class));
  }

  private Usuario crearUsuario(Long id, String email) {
    Usuario usuario = new Usuario();
    usuario.setId(id);
    usuario.setEmail(email);
    return usuario;
  }

  private Figurita crearFigurita(Long id, String nombre) {
    Figurita figurita = new Figurita(nombre, "Argentina");
    figurita.setId(id);
    return figurita;
  }
}
