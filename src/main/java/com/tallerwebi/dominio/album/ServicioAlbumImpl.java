package com.tallerwebi.dominio.album;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.Usuario;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("servicioAlbum")
@Transactional
public class ServicioAlbumImpl implements ServicioAlbum {

  private final RepositorioAlbum repositorioAlbum;
  private final RepositorioUsuario repositorioUsuario;
  private final RepositorioInventario repositorioInventario;
  private final RepositorioFigurita repositorioFigurita;

  @Autowired
  public ServicioAlbumImpl(
    RepositorioAlbum repositorioAlbum,
    RepositorioUsuario repositorioUsuario,
    RepositorioInventario repositorioInventario,
    RepositorioFigurita repositorioFigurita
  ) {
    this.repositorioAlbum = repositorioAlbum;
    this.repositorioUsuario = repositorioUsuario;
    this.repositorioInventario = repositorioInventario;
    this.repositorioFigurita = repositorioFigurita;
  }

  @Override
  public Album obtenerAlbumActualizado(Long idUsuario) {
    actualizarEstadisticas(idUsuario);
    return obtenerOCrearAlbum(idUsuario);
  }

  @Override
  public void actualizarEstadisticas(Long idUsuario) {
    Album album = obtenerOCrearAlbum(idUsuario);
    Usuario usuario = repositorioUsuario.buscarPorId(idUsuario);

    int total = (int) repositorioFigurita.contarFiguritas();
    int pegadas = repositorioInventario.buscarFiguritasPegadasPorUsuario(usuario).size();

    album.setTotalFiguritas(total);
    album.setFiguritasPegadas(pegadas);
    album.setFiguritasFaltantes(Math.max(total - pegadas, 0));
    repositorioAlbum.modificar(album);
  }

  @Override
  @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
  public List<AlbumSlotDTO> obtenerSlotsPorPais(Long idUsuario, String codigoPais) {
    Usuario usuario = repositorioUsuario.buscarPorId(idUsuario);
    List<Figurita> figuritasDelPais = repositorioFigurita.buscarPorPaisCodigoOrdenadas(codigoPais);
    List<RelacionFiguritaUsuario> pegadas = repositorioInventario.buscarFiguritasPegadasPorUsuario(
      usuario
    );
    List<RelacionFiguritaUsuario> disponibles =
      repositorioInventario.buscarFiguritasEnInventarioPorUsuario(usuario);

    Set<Long> idsPegadas = extraerIdsFigurita(pegadas);
    Set<Long> idsDisponibles = extraerIdsFigurita(disponibles);

    List<AlbumSlotDTO> slots = new ArrayList<>();
    for (Figurita figurita : figuritasDelPais) {
      boolean pegada = idsPegadas.contains(figurita.getId());
      boolean disponibleParaPegar = !pegada && idsDisponibles.contains(figurita.getId());
      slots.add(new AlbumSlotDTO(figurita, pegada, disponibleParaPegar));
    }

    return slots;
  }

  @Override
  public Map<String, Integer> obtenerPegadasPorPais(Long idUsuario) {
    Usuario usuario = repositorioUsuario.buscarPorId(idUsuario);
    List<RelacionFiguritaUsuario> pegadas = repositorioInventario.buscarFiguritasPegadasPorUsuario(
      usuario
    );
    Map<String, Integer> pegadasPorPais = new HashMap<>();

    for (RelacionFiguritaUsuario relacion : pegadas) {
      String codigoPais = relacion.getFigurita().getPais().getCodigo();
      pegadasPorPais.put(codigoPais, pegadasPorPais.getOrDefault(codigoPais, 0) + 1);
    }

    return pegadasPorPais;
  }

  @Override
  @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
  public Map<String, Integer> obtenerPendientesPorPais(Long idUsuario) {
    Usuario usuario = repositorioUsuario.buscarPorId(idUsuario);
    List<RelacionFiguritaUsuario> pegadas = repositorioInventario.buscarFiguritasPegadasPorUsuario(
      usuario
    );
    List<RelacionFiguritaUsuario> disponibles =
      repositorioInventario.buscarFiguritasEnInventarioPorUsuario(usuario);

    Set<Long> idsPegadas = extraerIdsFigurita(pegadas);
    Map<String, Integer> pendientesPorPais = new HashMap<>();
    Set<Long> figuritasYaContadas = new HashSet<>();

    for (RelacionFiguritaUsuario relacion : disponibles) {
      Long figuritaId = relacion.getFigurita().getId();

      if (!deberiaContarseComoPendiente(figuritaId, idsPegadas, figuritasYaContadas)) {
        continue;
      }

      figuritasYaContadas.add(figuritaId);
      String codigoPais = relacion.getFigurita().getPais().getCodigo();
      pendientesPorPais.put(codigoPais, pendientesPorPais.getOrDefault(codigoPais, 0) + 1);
    }

    return pendientesPorPais;
  }

  private Album obtenerOCrearAlbum(Long idUsuario) {
    Album album = repositorioAlbum.buscarPorUsuarioId(idUsuario);

    if (album != null) {
      return album;
    }

    Usuario usuario = repositorioUsuario.buscarPorId(idUsuario);
    Album nuevoAlbum = new Album(usuario);
    repositorioAlbum.guardar(nuevoAlbum);
    return nuevoAlbum;
  }

  private Set<Long> extraerIdsFigurita(List<RelacionFiguritaUsuario> relaciones) {
    Set<Long> idsFigurita = new HashSet<>();

    for (RelacionFiguritaUsuario relacion : relaciones) {
      idsFigurita.add(relacion.getFigurita().getId());
    }

    return idsFigurita;
  }

  private boolean deberiaContarseComoPendiente(
    Long figuritaId,
    Set<Long> idsPegadas,
    Set<Long> figuritasYaContadas
  ) {
    return !idsPegadas.contains(figuritaId) && !figuritasYaContadas.contains(figuritaId);
  }
}
