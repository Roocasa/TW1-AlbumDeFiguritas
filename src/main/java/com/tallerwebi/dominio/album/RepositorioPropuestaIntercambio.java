package com.tallerwebi.dominio.album;

import java.util.List;

public interface RepositorioPropuestaIntercambio {
  void guardar(PropuestaIntercambio propuesta);

  void modificar(PropuestaIntercambio propuesta);

  PropuestaIntercambio buscarPorId(Long idPropuesta);

  List<PropuestaIntercambio> buscarPendientesRecibidas(Long idUsuario);

  List<PropuestaIntercambio> buscarPendientesEnviadas(Long idUsuario);
}
