package com.tallerwebi.dominio.mision;

import java.util.List;

public interface ServicioMision {
  List<MisionEstadoDTO> obtenerMisiones(Long idUsuario);

  MisionDefinicion canjearMision(Long idUsuario, String codigoMision);
}
