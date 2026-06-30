package com.tallerwebi.dominio.amistad;

import com.tallerwebi.dominio.Usuario;
import java.util.List;
import java.util.Set;

public interface ServicioAmistad {
  void enviarSolicitud(Long idSolicitante, Long idReceptor);
  void aceptarSolicitud(Long idUsuario, Long idSolicitud);
  void rechazarSolicitud(Long idUsuario, Long idSolicitud);
  List<SolicitudAmistad> obtenerSolicitudesRecibidas(Long idUsuario);
  List<SolicitudAmistad> obtenerSolicitudesEnviadas(Long idUsuario);
  List<Usuario> obtenerAmigos(Long idUsuario);
  List<Usuario> obtenerUsuariosParaAgregar(Long idUsuario);
  Set<Long> obtenerIdsUsuariosRelacionados(Long idUsuario);
}
