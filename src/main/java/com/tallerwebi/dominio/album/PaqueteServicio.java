package com.tallerwebi.dominio.album;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("paqueteServicio")
@Transactional
public class PaqueteServicio {

  private RepositorioFigurita repositorioFigurita;
  private static final int FIGURITAS_POR_PAQUETE = 5;
  private RuletaFiguritas ruleta;

  @Autowired
  public PaqueteServicio(RepositorioFigurita repositorioFigurita, RuletaFiguritas ruleta) {
    this.repositorioFigurita = repositorioFigurita;
    this.ruleta = ruleta;
  }

  public List<Figurita> abrirPaquete() {
    List<Rareza> rarezasObtenidas = obtenerRarezasDePaquete();
    List<Figurita> figuritasDelPaquete = new ArrayList<>();

    for (Rareza r : rarezasObtenidas) {
      Figurita figuritaObtenida = repositorioFigurita.buscarFiguritaAleatoriaPorRareza(r);

      figuritasDelPaquete.add(figuritaObtenida);
    }
    return figuritasDelPaquete;
  }

  private List<Rareza> obtenerRarezasDePaquete() {
    List<Rareza> rarezasObtenidas = new ArrayList<>();
    Rareza rarezaActual;
    int puntosDeRareza;

    for (int i = 0; i < FIGURITAS_POR_PAQUETE; i++) {
      puntosDeRareza = ThreadLocalRandom.current().nextInt(1, 101);
      rarezaActual = this.ruleta.calcularRareza(puntosDeRareza);
      rarezasObtenidas.add(rarezaActual);
    }
    /* System.out.println(rarezasObtenidas); */
    return rarezasObtenidas;
  }

  public List<Figurita> abrirPaquetePremium() {
    List<Rareza> rarezasObtenidas = obtenerRarezasPremium();
    List<Figurita> figuritasDelPaquete = new ArrayList<>();

    for (Rareza r : rarezasObtenidas) {
      Figurita figuritaObtenida = repositorioFigurita.buscarFiguritaAleatoriaPorRareza(r);
      figuritasDelPaquete.add(figuritaObtenida);
    }
    return figuritasDelPaquete;
  }

  private List<Rareza> obtenerRarezasPremium() {
    List<Rareza> rarezasObtenidas = new ArrayList<>();
    Rareza rarezaActual;
    int puntosDeRareza;

    for (int i = 0; i < FIGURITAS_POR_PAQUETE; i++) {
      puntosDeRareza = ThreadLocalRandom.current().nextInt(1, 101);

      rarezaActual = this.ruleta.calcularRarezaPremium(puntosDeRareza);
      rarezasObtenidas.add(rarezaActual);
    }
    return rarezasObtenidas;
  }
}
