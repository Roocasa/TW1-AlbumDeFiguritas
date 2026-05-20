package com.tallerwebi.dominio.album;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.Usuario;
// import com.tallerwebi.dominio.excepciones.SinPaquetesException;
import com.tallerwebi.dominio.excepcion.PaquetesInsuficientesException;
import java.util.*;
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

  private static final int FIGURITAS_POR_PAQUETE = 7;

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
  public ResultadoApertura abrirPaquete(Long idUsuario, boolean esPremium)
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

    repositorioUsuario.modificar(usuario); // se actualiza el usuario en el repositorio con la resta del correspondiente paquete abierto

    List<Rareza> rarezasObtenidas = obtenerRarezas(esPremium); // obtiene una lista con 7 rarezas (si es premium el porcentaje de rarezas mejores aumenta)
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

    return new ResultadoApertura(figuritasDelPaquete, usuario);
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

  @Override
  public void pegarFigurita(Long idUsuario, Long idFigurita) {
    RelacionFiguritaUsuario relacion = repositorioInventario.buscarRelacionDisponible(
      idUsuario,
      idFigurita
    );

    if (relacion == null) {
      //  El usuario no la tiene en su inventario
      throw new RuntimeException("No tenés esta figurita en tu inventario.");
    }

    if (relacion.isEstaPegadaEnElAlbum()) {
      // La tiene, pero ya la había pegado
      throw new RuntimeException("Esta figurita ya está pegada en tu álbum.");
    }

    // Si pasamos los dos controles, significa que la tiene y no está pegada
    relacion.setEstaPegadaEnElAlbum(true);
    repositorioInventario.modificar(relacion);
  }

  @SuppressWarnings({ "PMD.DataflowAnomalyAnalysis" })
  @Override
  public List<InventarioItemDTO> obtenerFiguritasDelInventario(Long idUsuario) {
    Usuario usuario = repositorioUsuario.buscarPorId(idUsuario);

    List<RelacionFiguritaUsuario> noPegadas =
      repositorioInventario.buscarFiguritasEnInventarioPorUsuario(usuario);
    List<RelacionFiguritaUsuario> pegadas = repositorioInventario.buscarFiguritasPegadasPorUsuario(
      usuario
    );

    Set<Long> idsPegadas = new HashSet<>();
    for (RelacionFiguritaUsuario rel : pegadas) {
      idsPegadas.add(rel.getFigurita().getId());
    }

    Map<Long, Figurita> figuritasMap = new HashMap<>();
    Map<Long, Integer> conteoMap = new HashMap<>();

    for (RelacionFiguritaUsuario rel : noPegadas) {
      Long figId = rel.getFigurita().getId();
      figuritasMap.put(figId, rel.getFigurita());
      conteoMap.put(figId, conteoMap.getOrDefault(figId, 0) + 1);
    }

    List<InventarioItemDTO> resultado = new ArrayList<>();

    for (Map.Entry<Long, Integer> entry : conteoMap.entrySet()) {
      Long figId = entry.getKey();
      Integer cantidad = entry.getValue();
      Figurita fig = figuritasMap.get(figId);

      boolean sePuedePegar = !idsPegadas.contains(figId);
      resultado.add(new InventarioItemDTO(fig, cantidad, sePuedePegar));
    }

    return resultado;
  }
}
