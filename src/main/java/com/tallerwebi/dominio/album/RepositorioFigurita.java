package com.tallerwebi.dominio.album;

import java.util.List;

public interface RepositorioFigurita {
  Figurita buscarFiguritaAleatoriaPorRareza(Rareza rareza);

  List<Figurita> buscarPorPaisCodigoOrdenadas(String codigoPais);

  long contarFiguritas();
}
