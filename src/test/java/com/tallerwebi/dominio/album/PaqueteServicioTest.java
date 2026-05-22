package com.tallerwebi.dominio.album;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.PaquetesInsuficientesException;
//import java.util.Iterator;
//import com.tallerwebi.dominio.album.ResultadoApertura;
//import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PaqueteServicioTest {

  PaqueteServicio paqueteServicio;
  Usuario usuarioMock;
  RepositorioFigurita repositorioFiguritaFalso;
  RepositorioInventario repositorioInventarioFalso;
  RepositorioUsuario repositorioUsuarioFalso;
  RuletaFiguritas ruleta;
  ServicioAlbum servicioAlbumFalso;
  ResultadoApertura dtoResultado;

  @BeforeEach
  public void init() {
    repositorioFiguritaFalso = mock(RepositorioFigurita.class);
    repositorioInventarioFalso = mock(RepositorioInventario.class);
    repositorioUsuarioFalso = mock(RepositorioUsuario.class);
    servicioAlbumFalso = mock(ServicioAlbum.class);
    ruleta = new RuletaFiguritas();

    paqueteServicio =
      new PaqueteServicioImpl(
        repositorioFiguritaFalso,
        repositorioInventarioFalso,
        repositorioUsuarioFalso,
        ruleta,
        servicioAlbumFalso
      );

    usuarioMock = new Usuario();
    usuarioMock.setId(1L);
  }

  @Test
  public void alAbrirUnPaqueteComunDeberiaDevolverUnDtoConSieteFiguritasYElUsuarioActualizado()
    throws PaquetesInsuficientesException {
    //Given tenemos un usuario cargado en la base de datos
    when(repositorioUsuarioFalso.buscarPorId(usuarioMock.getId())).thenReturn(usuarioMock);
    //Le cargamos un paquete comune
    usuarioMock.setPaquetesDisponibles(1);

    // Mockeamos la base de datos con 4 figuritas
    when(repositorioFiguritaFalso.buscarFiguritaAleatoriaPorRareza(Rareza.COMUN))
      .thenReturn(new Figurita("Facundo Medina", "Argentina", Rareza.COMUN));
    when(repositorioFiguritaFalso.buscarFiguritaAleatoriaPorRareza(Rareza.PLATA))
      .thenReturn(new Figurita("Nico Paz", "Argentina", Rareza.PLATA));
    when(repositorioFiguritaFalso.buscarFiguritaAleatoriaPorRareza(Rareza.ORO))
      .thenReturn(new Figurita("Julián Álvarez", "Argentina", Rareza.ORO));
    when(repositorioFiguritaFalso.buscarFiguritaAleatoriaPorRareza(Rareza.LEYENDA))
      .thenReturn(new Figurita("Lionel Messi", "Argentina", Rareza.LEYENDA));

    //When el usuario abre un paquete de figuritas
    dtoResultado = paqueteServicio.abrirPaquete(usuarioMock.getId(), false);

    //            Iterator<Figurita> it = paqueteAbierto.iterator();
    //            while (it.hasNext()) {
    //                System.out.println(it.next().getNombre());
    //            } iterator para ver en consola las figuritas

    //Then se verifica que hayan 5 figuritas en la lista, que el primer elemento de la lista sea una figurita y que se haya descontado el paquete del inventario del usuario
    assertThat(dtoResultado.getFiguritasNuevas(), hasSize(7));
    assertThat(dtoResultado.getFiguritasNuevas().get(0), instanceOf(Figurita.class));
    assertThat(usuarioMock.getPaquetesDisponibles(), is(0));
    verify(repositorioInventarioFalso, times(7)).guardar(any()); // verifica que se guarden las siete relaciones en el repositorio
  }

  @Test
  public void alAbrirUnPaquetePremiumDeberiaDevolverUnDtoConSieteFiguritasYElUsuarioActualizado()
    throws PaquetesInsuficientesException {
    //Given tenemos un usuario cargado en la base de datos
    when(repositorioUsuarioFalso.buscarPorId(usuarioMock.getId())).thenReturn(usuarioMock);
    // le cargamos un paquete premium
    usuarioMock.setPaquetesPremiumDisponibles(1);
    // Mockeamos la base de datos con 4 figuritas
    when(repositorioFiguritaFalso.buscarFiguritaAleatoriaPorRareza(Rareza.COMUN))
      .thenReturn(new Figurita("Facundo Medina", "Argentina", Rareza.COMUN));
    when(repositorioFiguritaFalso.buscarFiguritaAleatoriaPorRareza(Rareza.PLATA))
      .thenReturn(new Figurita("Nico Paz", "Argentina", Rareza.PLATA));
    when(repositorioFiguritaFalso.buscarFiguritaAleatoriaPorRareza(Rareza.ORO))
      .thenReturn(new Figurita("Julián Álvarez", "Argentina", Rareza.ORO));
    when(repositorioFiguritaFalso.buscarFiguritaAleatoriaPorRareza(Rareza.LEYENDA))
      .thenReturn(new Figurita("Lionel Messi", "Argentina", Rareza.LEYENDA));

    //When el usuario abre un paquete de figuritas premium
    dtoResultado = paqueteServicio.abrirPaquete(usuarioMock.getId(), true);

    //Then se verifica que hayan 5 figuritas en la lista, que el primer elemento de la lista sea una figurita y que se haya descontado el paquete del inventario del usuario
    assertThat(dtoResultado.getFiguritasNuevas(), hasSize(7));
    assertThat(dtoResultado.getFiguritasNuevas().get(0), instanceOf(Figurita.class));
    assertThat(usuarioMock.getPaquetesDisponibles(), is(0));
    verify(repositorioInventarioFalso, times(7)).guardar(any()); // verifica que el guardar (relaciones) del repositorio haya sido invocado siete veces
  }

  @Test
  public void dadoQueUnUsuarioNoTienePaquetesComunesSeLanzaUnaExcepcionCuandoSeIntentaAbrirUnoPaquete()
    throws PaquetesInsuficientesException {
    //Given tenemos un usuario cargado en la base de datos
    when(repositorioUsuarioFalso.buscarPorId(usuarioMock.getId())).thenReturn(usuarioMock);

    //When y Then > cuando intentamos abrir un paquete comun de figuritas con un usuario que por defecto no tiene paquetes en el repositorio
    // entonces se captura una excepcion de PaquetesInsuficientes

    PaquetesInsuficientesException excepcionAtrapada = assertThrows(
      PaquetesInsuficientesException.class,
      () -> paqueteServicio.abrirPaquete(usuarioMock.getId(), false)
    );

    assertThat(excepcionAtrapada.getMessage(), is("No tenés paquetes disponibles."));
  }

  @Test
  public void dadoQueUnUsuarioNoTienePaquetesPremiumSeLanzaUnaExcepcionCuandoSeIntentaAbrirUnoPaquetePremium()
    throws PaquetesInsuficientesException {
    //Given tenemos un usuario cargado en la base de datos
    when(repositorioUsuarioFalso.buscarPorId(usuarioMock.getId())).thenReturn(usuarioMock);

    //When y Then > cuando intentamos abrir un paquete comun de figuritas con un usuario que por defecto no tiene paquetes en el repositorio
    // entonces se captura una excepcion de PaquetesInsuficientes

    PaquetesInsuficientesException excepcionAtrapada = assertThrows(
      PaquetesInsuficientesException.class,
      () -> paqueteServicio.abrirPaquete(usuarioMock.getId(), true)
    );

    assertThat(excepcionAtrapada.getMessage(), is("No tenés paquetes Premium disponibles."));
  }
}
