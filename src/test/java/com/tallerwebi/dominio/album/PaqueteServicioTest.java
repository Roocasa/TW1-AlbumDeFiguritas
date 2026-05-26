package com.tallerwebi.dominio.album;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.Usuario;
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
  ServicioAlbum servicioAlbumFalso;
  ResultadoApertura dtoResultado;

  @BeforeEach
  public void init() {
    repositorioFiguritaFalso = mock(RepositorioFigurita.class);
    repositorioInventarioFalso = mock(RepositorioInventario.class);
    repositorioUsuarioFalso = mock(RepositorioUsuario.class);
    servicioAlbumFalso = mock(ServicioAlbum.class);

    paqueteServicio =
      new PaqueteServicioImpl(
        repositorioFiguritaFalso,
        repositorioInventarioFalso,
        repositorioUsuarioFalso,
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
      new Figurita("Facundo Medina", "Argentina", Rareza.COMUN),
      new Figurita("Nico Paz", "Argentina", Rareza.COMUN),
      new Figurita("Julian Alvarez", "Argentina", Rareza.COMUN),
      new Figurita("Lionel Messi", "Argentina", Rareza.COMUN),
      new Figurita("Thiago Almada", "Argentina", Rareza.COMUN)
    );
    when(repositorioFiguritaFalso.buscarFiguritasAleatorias(5)).thenReturn(figuritas);

    dtoResultado = paqueteServicio.abrirPaquete(usuarioMock.getId());

    assertThat(dtoResultado.getFiguritasNuevas(), hasSize(5));
    assertThat(dtoResultado.getFiguritasNuevas(), contains(figuritas.toArray()));
    assertThat(usuarioMock.getPaquetesDisponibles(), is(0));
    verify(repositorioInventarioFalso, times(5)).guardar(any());
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
}
