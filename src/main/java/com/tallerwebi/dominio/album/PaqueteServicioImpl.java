package com.tallerwebi.dominio.album;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.CanjeFiguritasException;
import com.tallerwebi.dominio.excepcion.PaquetesInsuficientesException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("paqueteServicio")
@Transactional
public class PaqueteServicioImpl implements PaqueteServicio {

  private static final int FIGURITAS_POR_PAQUETE = 5;
  private static final int REPETIDAS_POR_PAQUETE = 5;
  private static final int REPETIDAS_POR_ESCUDO = 3;

  private final RepositorioFigurita repositorioFigurita;
  private final RepositorioInventario repositorioInventario;
  private final RepositorioUsuario repositorioUsuario;
  private final ServicioAlbum servicioAlbum;

  @Autowired
  public PaqueteServicioImpl(
    RepositorioFigurita repositorioFigurita,
    RepositorioInventario repositorioInventario,
    RepositorioUsuario repositorioUsuario,
    ServicioAlbum servicioAlbum
  ) {
    this.repositorioFigurita = repositorioFigurita;
    this.repositorioInventario = repositorioInventario;
    this.repositorioUsuario = repositorioUsuario;
    this.servicioAlbum = servicioAlbum;
  }

  @Override
  public ResultadoApertura abrirPaquete(Long idUsuario) throws PaquetesInsuficientesException {
    Usuario usuario = repositorioUsuario.buscarPorId(idUsuario);

    if (usuario.getPaquetesDisponibles() <= 0) {
      throw new PaquetesInsuficientesException("No tenes paquetes disponibles.");
    }

    usuario.setPaquetesDisponibles(usuario.getPaquetesDisponibles() - 1);
    repositorioUsuario.modificar(usuario);

    List<Figurita> figuritasDelPaquete = repositorioFigurita.buscarFiguritasAleatorias(
      FIGURITAS_POR_PAQUETE
    );

    for (Figurita figuritaObtenida : figuritasDelPaquete) {
      RelacionFiguritaUsuario nuevaRelacion = new RelacionFiguritaUsuario(
        usuario,
        figuritaObtenida
      );
      repositorioInventario.guardar(nuevaRelacion);
    }

    return new ResultadoApertura(figuritasDelPaquete, usuario);
  }

  @Override
  public void pegarFigurita(Long idUsuario, Long idFigurita) {
    RelacionFiguritaUsuario relacion = repositorioInventario.buscarRelacionDisponible(
      idUsuario,
      idFigurita
    );

    if (relacion == null) {
      throw new RuntimeException("No tenes esta figurita en tu inventario.");
    }

    if (relacion.isEstaPegadaEnElAlbum()) {
      throw new RuntimeException("Esta figurita ya esta pegada en tu album.");
    }

    relacion.setEstaPegadaEnElAlbum(true);
    repositorioInventario.modificar(relacion);
    servicioAlbum.actualizarEstadisticas(idUsuario);
  }

  @Override
  public List<InventarioItemDTO> obtenerFiguritasDelInventario(Long idUsuario) {
    Usuario usuario = repositorioUsuario.buscarPorId(idUsuario);

    List<RelacionFiguritaUsuario> noPegadas =
      repositorioInventario.buscarFiguritasEnInventarioPorUsuario(usuario);
    List<RelacionFiguritaUsuario> pegadas = repositorioInventario.buscarFiguritasPegadasPorUsuario(
      usuario
    );

    Set<Long> idsPegadas = new HashSet<>(); // NOPMD - false positive from PMD dataflow analysis
    for (RelacionFiguritaUsuario rel : pegadas) {
      idsPegadas.add(rel.getFigurita().getId());
    }

    Map<Long, Figurita> figuritasMap = new HashMap<>(); // NOPMD - false positive from PMD dataflow analysis
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

  @Override
  public void canjearRepetidasPorPaquete(Long idUsuario) throws CanjeFiguritasException {
    Usuario usuario = repositorioUsuario.buscarPorId(idUsuario);
    consumirRepetidas(idUsuario, REPETIDAS_POR_PAQUETE);
    usuario.sumarPaquetesComunes(1);
    usuario.sumarIntercambioRealizado();
    repositorioUsuario.modificar(usuario);
    servicioAlbum.actualizarEstadisticas(idUsuario);
  }

  @Override
  public Figurita canjearRepetidasPorEscudo(Long idUsuario) throws CanjeFiguritasException {
    Usuario usuario = repositorioUsuario.buscarPorId(idUsuario); // NOPMD - false positive from PMD dataflow analysis
    consumirRepetidas(idUsuario, REPETIDAS_POR_ESCUDO);

    Figurita escudoAleatorio = repositorioFigurita.buscarEscudoAleatorio();
    if (escudoAleatorio == null) {
      throw new CanjeFiguritasException("No hay escudos disponibles para canjear.");
    }

    repositorioInventario.guardar(new RelacionFiguritaUsuario(usuario, escudoAleatorio));
    usuario.sumarIntercambioRealizado();
    repositorioUsuario.modificar(usuario);
    servicioAlbum.actualizarEstadisticas(idUsuario);

    return escudoAleatorio;
  }

  @Override
  public int obtenerCantidadTotalRepetidas(Long idUsuario) {
    int totalRepetidas = 0;

    for (InventarioItemDTO item : obtenerFiguritasDelInventario(idUsuario)) {
      totalRepetidas += item.getCantidadRepetidas();
    }

    return totalRepetidas;
  }

  @Override
  public int obtenerCostoCanjePaquete() {
    return REPETIDAS_POR_PAQUETE;
  }

  @Override
  public int obtenerCostoCanjeEscudo() {
    return REPETIDAS_POR_ESCUDO;
  }

  private void consumirRepetidas(Long idUsuario, int cantidadNecesaria)
    throws CanjeFiguritasException {
    List<RelacionFiguritaUsuario> repetidasDisponibles = obtenerRelacionesRepetidasCanjeables(
      idUsuario
    );

    if (repetidasDisponibles.size() < cantidadNecesaria) {
      throw new CanjeFiguritasException(
        "No tienes suficientes figuritas repetidas para hacer ese canje."
      );
    }

    for (int i = 0; i < cantidadNecesaria; i++) {
      repositorioInventario.eliminar(repetidasDisponibles.get(i));
    }
  }

  private List<RelacionFiguritaUsuario> obtenerRelacionesRepetidasCanjeables(Long idUsuario) {
    Usuario usuario = repositorioUsuario.buscarPorId(idUsuario);
    List<RelacionFiguritaUsuario> noPegadas =
      repositorioInventario.buscarFiguritasEnInventarioPorUsuario(usuario);
    List<RelacionFiguritaUsuario> pegadas = repositorioInventario.buscarFiguritasPegadasPorUsuario(
      usuario
    );

    Set<Long> idsPegadas = new HashSet<>(); // NOPMD - false positive from PMD dataflow analysis
    for (RelacionFiguritaUsuario relacionPegada : pegadas) {
      idsPegadas.add(relacionPegada.getFigurita().getId());
    }

    Map<Long, List<RelacionFiguritaUsuario>> relacionesPorFigurita = new HashMap<>();
    for (RelacionFiguritaUsuario relacion : noPegadas) {
      Long figuritaId = relacion.getFigurita().getId();
      relacionesPorFigurita.computeIfAbsent(figuritaId, key -> new ArrayList<>()).add(relacion);
    }

    List<RelacionFiguritaUsuario> repetidasCanjeables = new ArrayList<>();
    for (List<RelacionFiguritaUsuario> relaciones : relacionesPorFigurita.values()) {
      relaciones.sort(Comparator.comparing(RelacionFiguritaUsuario::getId));

      int indiceInicial = idsPegadas.contains(relaciones.get(0).getFigurita().getId()) ? 0 : 1;
      for (int i = indiceInicial; i < relaciones.size(); i++) {
        repetidasCanjeables.add(relaciones.get(i));
      }
    }

    repetidasCanjeables.sort(Comparator.comparing(RelacionFiguritaUsuario::getId));
    return repetidasCanjeables;
  }
}
