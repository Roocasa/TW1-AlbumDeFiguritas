package com.tallerwebi.dominio.album;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.CanjeFiguritasException;
import com.tallerwebi.dominio.excepcion.PaquetesInsuficientesException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PaqueteServicioTest {

  PaqueteServicio paqueteServicio;
  Usuario usuarioMock;
  RepositorioFigurita repositorioFiguritaFalso;
  RepositorioInventario repositorioInventarioFalso;
  RepositorioUsuario repositorioUsuarioFalso;
  RepositorioHistorialSobre repositorioHistorialSobreFalso;
  ServicioAlbum servicioAlbumFalso;
  ResultadoApertura dtoResultado;

  @BeforeEach
  public void init() {
    repositorioFiguritaFalso = mock(RepositorioFigurita.class);
    repositorioInventarioFalso = mock(RepositorioInventario.class);
    repositorioUsuarioFalso = mock(RepositorioUsuario.class);
    repositorioHistorialSobreFalso = mock(RepositorioHistorialSobre.class);
    servicioAlbumFalso = mock(ServicioAlbum.class);

    paqueteServicio =
      new PaqueteServicioImpl(
        repositorioFiguritaFalso,
        repositorioInventarioFalso,
        repositorioUsuarioFalso,
        repositorioHistorialSobreFalso,
        servicioAlbumFalso
      );

    usuarioMock = new Usuario();
    usuarioMock.setId(1L);
  }

  @Test
  public void alAbrirUnPaqueteComunDeberiaDevolverCincoFiguritasYDescontarUnPaquete()
    throws PaquetesInsuficientesException {
    when(repositorioUsuarioFalso.buscarPorId(usuarioMock.getId())).thenReturn(usuarioMock);
    usuarioMock.setPaquetesDisponibles(1);

    List<Figurita> figuritas = List.of(
      new Figurita("Facundo Medina", "Argentina"),
      new Figurita("Nico Paz", "Argentina"),
      new Figurita("Julian Alvarez", "Argentina"),
      new Figurita("Lionel Messi", "Argentina"),
      new Figurita("Thiago Almada", "Argentina")
    );
    when(repositorioFiguritaFalso.buscarFiguritasAleatorias(5)).thenReturn(figuritas);

    dtoResultado = paqueteServicio.abrirPaquete(usuarioMock.getId());

    assertThat(dtoResultado.getFiguritasNuevas(), hasSize(5));
    assertThat(dtoResultado.getFiguritasNuevas(), contains(figuritas.toArray()));
    assertThat(usuarioMock.getPaquetesDisponibles(), is(0));
    verify(repositorioInventarioFalso, times(5)).guardar(any());
    verify(repositorioHistorialSobreFalso).guardar(any(HistorialSobre.class));
  }

  @Test
  public void dadoQueUnUsuarioNoTienePaquetesSeLanzaUnaExcepcionCuandoIntentaAbrirUnPaquete()
    throws PaquetesInsuficientesException {
    when(repositorioUsuarioFalso.buscarPorId(usuarioMock.getId())).thenReturn(usuarioMock);

    PaquetesInsuficientesException excepcionAtrapada = assertThrows(
      PaquetesInsuficientesException.class,
      () -> paqueteServicio.abrirPaquete(usuarioMock.getId())
    );

    assertThat(excepcionAtrapada.getMessage(), is("No tenes paquetes disponibles."));
  }

  @Test
  public void siTieneRepetidasSuficientesPuedeCanjearlasPorUnPaquete()
    throws CanjeFiguritasException {
    when(repositorioUsuarioFalso.buscarPorId(usuarioMock.getId())).thenReturn(usuarioMock);

    Figurita figurita = new Figurita("Lionel Messi", "Argentina");
    figurita.setId(10L);

    when(repositorioInventarioFalso.buscarFiguritasEnInventarioPorUsuario(usuarioMock))
      .thenReturn(
        List.of(
          crearRelacion(1L, figurita),
          crearRelacion(2L, figurita),
          crearRelacion(3L, figurita),
          crearRelacion(4L, figurita),
          crearRelacion(5L, figurita),
          crearRelacion(6L, figurita)
        )
      );
    when(repositorioInventarioFalso.buscarFiguritasPegadasPorUsuario(usuarioMock))
      .thenReturn(List.of());

    paqueteServicio.canjearRepetidasPorPaquete(usuarioMock.getId());

    assertThat(usuarioMock.getPaquetesDisponibles(), is(1));
    assertThat(usuarioMock.getIntercambiosRealizados(), is(1));
    verify(repositorioInventarioFalso, times(5)).eliminar(any(RelacionFiguritaUsuario.class));
  }

  @Test
  public void siTieneRepetidasSuficientesPuedeCanjearlasPorUnEscudoAleatorio()
    throws CanjeFiguritasException {
    when(repositorioUsuarioFalso.buscarPorId(usuarioMock.getId())).thenReturn(usuarioMock);

    Figurita repetida = new Figurita("Nahuel Molina", "Argentina");
    repetida.setId(3L);
    Figurita escudoPremio = new Figurita("Escudo de Brasil", "Brasil", TipoFigurita.ESCUDO);
    escudoPremio.setId(9L);

    when(repositorioInventarioFalso.buscarFiguritasEnInventarioPorUsuario(usuarioMock))
      .thenReturn(
        List.of(
          crearRelacion(1L, repetida),
          crearRelacion(2L, repetida),
          crearRelacion(3L, repetida),
          crearRelacion(4L, repetida)
        )
      );
    when(repositorioInventarioFalso.buscarFiguritasPegadasPorUsuario(usuarioMock))
      .thenReturn(List.of());
    when(repositorioFiguritaFalso.buscarEscudoAleatorio()).thenReturn(escudoPremio);

    Figurita figuritaGanada = paqueteServicio.canjearRepetidasPorEscudo(usuarioMock.getId());

    assertThat(figuritaGanada, equalTo(escudoPremio));
    assertThat(usuarioMock.getIntercambiosRealizados(), is(1));
    verify(repositorioInventarioFalso, times(3)).eliminar(any(RelacionFiguritaUsuario.class));
    verify(repositorioInventarioFalso, times(1)).guardar(any(RelacionFiguritaUsuario.class));
  }

  @Test
  public void siNoTieneRepetidasSuficientesNoPuedeCanjearPorPaquete() {
    when(repositorioUsuarioFalso.buscarPorId(usuarioMock.getId())).thenReturn(usuarioMock);

    Figurita figurita = new Figurita("Lionel Messi", "Argentina");
    figurita.setId(10L);

    when(repositorioInventarioFalso.buscarFiguritasEnInventarioPorUsuario(usuarioMock))
      .thenReturn(List.of(crearRelacion(1L, figurita), crearRelacion(2L, figurita)));
    when(repositorioInventarioFalso.buscarFiguritasPegadasPorUsuario(usuarioMock))
      .thenReturn(List.of());

    CanjeFiguritasException excepcion = assertThrows(
      CanjeFiguritasException.class,
      () -> paqueteServicio.canjearRepetidasPorPaquete(usuarioMock.getId())
    );

    assertThat(
      excepcion.getMessage(),
      equalTo("No tienes suficientes figuritas repetidas para hacer ese canje.")
    );
    verify(repositorioInventarioFalso, never()).eliminar(any(RelacionFiguritaUsuario.class));
  }

  private RelacionFiguritaUsuario crearRelacion(Long id, Figurita figurita) {
    RelacionFiguritaUsuario relacion = new RelacionFiguritaUsuario(usuarioMock, figurita);
    relacion.setId(id);
    return relacion;
  }
}
