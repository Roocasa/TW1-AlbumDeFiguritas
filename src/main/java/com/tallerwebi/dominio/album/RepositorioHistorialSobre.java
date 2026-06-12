package com.tallerwebi.dominio.album;

import java.util.List;

public interface RepositorioHistorialSobre {
  void guardar(HistorialSobre historialSobre);

  List<HistorialSobre> buscarPorUsuario(Long idUsuario);
}
