package com.tallerwebi.dominio.album;

import java.util.List;
import java.util.Map;

public interface ServicioAlbum {
  Album obtenerAlbumActualizado(Long idUsuario);

  void actualizarEstadisticas(Long idUsuario);

  List<AlbumSlotDTO> obtenerSlotsPorPais(Long idUsuario, String codigoPais);

  Map<String, Integer> obtenerPegadasPorPais(Long idUsuario);

  Map<String, Integer> obtenerPendientesPorPais(Long idUsuario);
}
