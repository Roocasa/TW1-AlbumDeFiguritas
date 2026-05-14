package com.tallerwebi.dominio.album;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.Usuario;
// import com.tallerwebi.dominio.excepciones.SinPaquetesException;
import com.tallerwebi.dominio.excepcion.PaquetesInsuficientesException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("paqueteServicio")
@Transactional
public class PaqueteServicioImpl implements PaqueteServicio {

  private final RepositorioFigurita repositorioFigurita;
  private final RepositorioInventario repositorioInventario;
  private final RepositorioUsuario repositorioUsuario;
  private final RuletaFiguritas ruleta;

  private static final int FIGURITAS_POR_PAQUETE = 5;

  @Autowired
  public PaqueteServicioImpl(
    RepositorioFigurita repositorioFigurita,
    RepositorioInventario repositorioInventario,
    RepositorioUsuario repositorioUsuario,
    RuletaFiguritas ruleta
  ) {
    this.repositorioFigurita = repositorioFigurita;
    this.repositorioInventario = repositorioInventario;
    this.repositorioUsuario = repositorioUsuario;
    this.ruleta = ruleta;
  }

  @Override
  public List<Figurita> abrirPaquete(Long idUsuario, boolean esPremium)
    throws PaquetesInsuficientesException {
    Usuario usuario = repositorioUsuario.buscarPorId(idUsuario);

    if (esPremium) { // se resta un paquete o se lanza una exception si no hay paquetes disponibles
      if (usuario.getPaquetesPremiumDisponibles() <= 0) {
        throw new PaquetesInsuficientesException("No tenés paquetes Premium disponibles.");
      }
      usuario.setPaquetesPremiumDisponibles(usuario.getPaquetesPremiumDisponibles() - 1);
    } else {
      if (usuario.getPaquetesDisponibles() <= 0) {
        throw new PaquetesInsuficientesException("No tenés paquetes disponibles.");
      }
      usuario.setPaquetesDisponibles(usuario.getPaquetesDisponibles() - 1);
    }

    List<Rareza> rarezasObtenidas = obtenerRarezas(esPremium); // obtiene una lista con 5 rarezas (si es premium el porcentaje de rarezas mejores aumenta)
    List<Figurita> figuritasDelPaquete = new ArrayList<>();

    // Buscamos en el catálogo segun las rarezas obtenidas
    for (Rareza r : rarezasObtenidas) {
      Figurita figuritaObtenida = repositorioFigurita.buscarFiguritaAleatoriaPorRareza(r);
      figuritasDelPaquete.add(figuritaObtenida);

      RelacionFiguritaUsuario nuevaRelacion = new RelacionFiguritaUsuario(
        usuario,
        figuritaObtenida
      );
      repositorioInventario.guardar(nuevaRelacion);
    }

    return figuritasDelPaquete;
  }

  private List<Rareza> obtenerRarezas(boolean esPremium) {
    List<Rareza> rarezasObtenidas = new ArrayList<>();
    int puntosDeRareza;

    for (int i = 0; i < FIGURITAS_POR_PAQUETE; i++) {
      puntosDeRareza = ThreadLocalRandom.current().nextInt(1, 101);

      Rareza rarezaActual = esPremium
        ? this.ruleta.calcularRarezaPremium(puntosDeRareza)
        : this.ruleta.calcularRareza(puntosDeRareza);

      rarezasObtenidas.add(rarezaActual);
    }
    return rarezasObtenidas;
  }
}
