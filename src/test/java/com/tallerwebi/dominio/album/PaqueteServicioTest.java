package com.tallerwebi.dominio.album;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Mockito.*;

import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.api.Test;

public class PaqueteServicioTest {

  @Test
  public void alAbrirUnPaqueteDeberiaDevolverUnaListaDeCincoFiguritas() {
    //Given Dado que tenemos una base de datos (simulada con mockito) y una ruleta
    RepositorioFigurita repositorioFalso = mock(RepositorioFigurita.class);
    RuletaFiguritas ruleta = new RuletaFiguritas();

    // Mockeamos la base de datos para que responda lo que queremos segun el tipo de rareza
    when(repositorioFalso.buscarFiguritaAleatoriaPorRareza(Rareza.COMUN))
      .thenReturn(new Figurita("Facundo Medina", "Argentina", Rareza.COMUN));
    when(repositorioFalso.buscarFiguritaAleatoriaPorRareza(Rareza.PLATA))
      .thenReturn(new Figurita("Nico Paz", "Argentina", Rareza.PLATA));
    when(repositorioFalso.buscarFiguritaAleatoriaPorRareza(Rareza.ORO))
      .thenReturn(new Figurita("Julián Álvarez", "Argentina", Rareza.ORO));
    when(repositorioFalso.buscarFiguritaAleatoriaPorRareza(Rareza.LEYENDA))
      .thenReturn(new Figurita("Lionel Messi", "Argentina", Rareza.LEYENDA));

    // Pasamos el repositorio por parametro al servicio y la ruleta que requiere
    PaqueteServicio paqueteServicio = new PaqueteServicio(repositorioFalso, ruleta);

    //When > Cuando el usuario abre un sobre
    List<Figurita> paqueteAbierto = paqueteServicio.abrirPaquete();

    //        Iterator<Figurita> it = paqueteAbierto.iterator();
    //        while (it.hasNext()) {
    //            System.out.println(it.next().getNombre());
    //        }

    //Then
    assertThat(paqueteAbierto, hasSize(5));
    // se verifica que el primer elemento de la lista sea una figurita
    assertThat(paqueteAbierto.get(0), instanceOf(Figurita.class));
  }

  @Test
  public void alAbrirUnPaquetePremiumDeberiaDevolverCincoFiguritasPremium() {
    RepositorioFigurita repositorioFalso = mock(RepositorioFigurita.class);
    RuletaFiguritas ruleta = new RuletaFiguritas();

    when(repositorioFalso.buscarFiguritaAleatoriaPorRareza(Rareza.PLATA))
      .thenReturn(new Figurita("Enzo Fernandez", "Argentina", Rareza.PLATA));
    when(repositorioFalso.buscarFiguritaAleatoriaPorRareza(Rareza.ORO))
      .thenReturn(new Figurita("Lautaro Martinez", "Argentina", Rareza.ORO));
    when(repositorioFalso.buscarFiguritaAleatoriaPorRareza(Rareza.LEYENDA))
      .thenReturn(new Figurita("Lionel Messi", "Argentina", Rareza.LEYENDA));

    PaqueteServicio paqueteServicio = new PaqueteServicio(repositorioFalso, ruleta);

    List<Figurita> paqueteAbierto = paqueteServicio.abrirPaquetePremium();

    assertThat(paqueteAbierto, hasSize(5));
    assertThat(paqueteAbierto.get(0), instanceOf(Figurita.class));
  }
}
