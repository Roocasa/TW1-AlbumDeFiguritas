package com.tallerwebi.dominio.album;

public interface RepositorioAlbum {
  Album buscarPorUsuarioId(Long usuarioId);

  void guardar(Album album);

  void modificar(Album album);
}
