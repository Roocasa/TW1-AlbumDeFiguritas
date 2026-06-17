package com.tallerwebi.dominio.mision;

import java.util.List;

public interface RepositorioMisionUsuario {
  void guardar(MisionUsuario misionUsuario);

  MisionUsuario buscarPorUsuarioYCodigo(Long idUsuario, String codigoMision);

  List<MisionUsuario> buscarPorUsuario(Long idUsuario);
}
