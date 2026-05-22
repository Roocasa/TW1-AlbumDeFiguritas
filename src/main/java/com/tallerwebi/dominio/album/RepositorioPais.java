package com.tallerwebi.dominio.album;

import java.util.List;

public interface RepositorioPais {
  List<Pais> buscarTodos();

  List<Pais> buscarPorGrupoYNombreOCodigo(String grupo, String busqueda);

  Pais buscarPorCodigo(String codigo);
}
