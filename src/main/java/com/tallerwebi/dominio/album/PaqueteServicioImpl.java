package com.tallerwebi.dominio.album;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.PaquetesInsuficientesException;
import java.util.ArrayList;
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
