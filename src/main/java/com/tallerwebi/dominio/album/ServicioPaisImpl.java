package com.tallerwebi.dominio.album;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("servicioPais")
@Transactional
public class ServicioPaisImpl implements ServicioPais {

  private final RepositorioPais repositorioPais;

  @Autowired
  public ServicioPaisImpl(RepositorioPais repositorioPais) {
    this.repositorioPais = repositorioPais;
  }

  @Override
  public List<Pais> buscarPaises(String grupo, String busqueda) {
    return repositorioPais.buscarPorGrupoYNombreOCodigo(
      normalizarGrupo(grupo),
      normalizarBusqueda(busqueda)
    );
  }

  @Override
  public Map<String, List<Pais>> agruparPorGrupo(List<Pais> paises) {
    Map<String, List<Pais>> paisesPorGrupo = new LinkedHashMap<>();

    for (Pais pais : paises) {
      paisesPorGrupo.computeIfAbsent(pais.getGrupo(), clave -> new ArrayList<>()).add(pais);
    }

    return paisesPorGrupo;
  }

  @Override
  public Pais buscarPorCodigo(String codigo) {
    if (codigo == null) {
      return null;
    }

    return repositorioPais.buscarPorCodigo(codigo.trim().toUpperCase(Locale.ROOT));
  }

  @Override
  public List<String> listarGrupos() {
    List<String> grupos = new ArrayList<>();
    for (char grupo = 'A'; grupo <= 'L'; grupo++) {
      grupos.add(String.valueOf(grupo));
    }
    return grupos;
  }

  private String normalizarGrupo(String grupo) {
    if (grupo == null || grupo.trim().isEmpty()) {
      return null;
    }

    return grupo.replace("grupo-", "").trim().toUpperCase(Locale.ROOT);
  }

  private String normalizarBusqueda(String busqueda) {
    if (busqueda == null || busqueda.trim().isEmpty()) {
      return null;
    }

    return busqueda.trim().toLowerCase(Locale.ROOT);
  }
}
