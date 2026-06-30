package com.tallerwebi.dominio.amistad;

import java.util.List;

public interface RepositorioAmistad {
  void guardar(SolicitudAmistad solicitud);
  void modificar(SolicitudAmistad solicitud);
  SolicitudAmistad buscarPorId(Long idSolicitud);
  SolicitudAmistad buscarEntreUsuarios(Long idUsuario, Long idOtroUsuario);
  List<SolicitudAmistad> buscarPendientesRecibidas(Long idUsuario);
  List<SolicitudAmistad> buscarPendientesEnviadas(Long idUsuario);
  List<SolicitudAmistad> buscarAceptadas(Long idUsuario);
  List<SolicitudAmistad> buscarRelacionadas(Long idUsuario);
}
