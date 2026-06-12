package com.tallerwebi.dominio.notificacion;

import java.util.List;

public interface RepositorioNotificacion {
  void guardar(Notificacion notificacion);
  void modificar(Notificacion notificacion);
  Notificacion buscarPorId(Long idNotificacion);
  List<Notificacion> buscarUltimasPorUsuario(Long idUsuario, int limite);
  List<Notificacion> buscarNoLeidasPorUsuario(Long idUsuario);
  Long contarNoLeidasPorUsuario(Long idUsuario);
}
