package com.tallerwebi.dominio.album;

import java.util.List;
import java.util.Map;

public interface ServicioPais {
  List<Pais> buscarPaises(String grupo, String busqueda);

  Map<String, List<Pais>> agruparPorGrupo(List<Pais> paises);

  Pais buscarPorCodigo(String codigo);

  List<String> listarGrupos();
}
