package com.tallerwebi.dominio.prode;

import java.util.List;

public interface ServicioProde {
  List<PartidoProdeDTO> obtenerPartidosConPronosticos(Long idUsuario);
  int obtenerPuntaje(Long idUsuario);
  void pronosticar(Long idUsuario, Long idPartido, Integer golesLocal, Integer golesVisitante);
  int actualizarResultados();
}
