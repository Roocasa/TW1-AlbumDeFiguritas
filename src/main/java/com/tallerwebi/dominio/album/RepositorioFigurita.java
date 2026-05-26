package com.tallerwebi.dominio.album;

import java.util.List;

public interface RepositorioFigurita {
  List<Figurita> buscarFiguritasAleatorias(int cantidad);

  List<Figurita> buscarPorPaisCodigoOrdenadas(String codigoPais);

  long contarFiguritas();
}
