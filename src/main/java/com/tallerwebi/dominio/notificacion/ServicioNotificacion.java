package com.tallerwebi.dominio.notificacion;

import java.util.List;

public interface ServicioNotificacion {
  void avisarPropuestaRecibida(Long idUsuarioDestino, String usuarioOrigen);
  void avisarPropuestaRespondida(Long idUsuarioSolicitante, boolean aceptada);
  void avisarSobresDiariosDisponibles(Long idUsuario, int cantidad);
  List<Notificacion> obtenerUltimas(Long idUsuario);
  Long contarNoLeidas(Long idUsuario);
  String marcarComoLeida(Long idUsuario, Long idNotificacion);
  void marcarTodasComoLeidas(Long idUsuario);
}
