package com.tallerwebi.dominio.album;

import java.util.List;

public interface RepositorioPropuestaIntercambio {
  void guardar(PropuestaIntercambio propuesta);

  void modificar(PropuestaIntercambio propuesta);

  PropuestaIntercambio buscarPorId(Long idPropuesta);

  List<PropuestaIntercambio> buscarRecibidas(Long idUsuario);

  List<PropuestaIntercambio> buscarEnviadas(Long idUsuario);

  List<PropuestaIntercambio> buscarPorUsuario(Long idUsuario);
}
