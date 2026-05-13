package com.tallerwebi.dominio.album;

public interface RepositorioFigurita {
  Figurita buscarFiguritaAleatoriaPorRareza(Rareza rareza);

  void guardar(Figurita figurita);

  Figurita buscarPorId(Long id);
}
