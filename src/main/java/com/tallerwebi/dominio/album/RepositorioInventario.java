package com.tallerwebi.dominio.album;

import com.tallerwebi.dominio.Usuario;
import java.util.List;

public interface RepositorioInventario {
  // Al abrir un sobre guardo la figurita relacionada a un usuario
  void guardar(RelacionFiguritaUsuario relacion);

  // Lista de figuritas en inventario que no estan pegadas
  List<RelacionFiguritaUsuario> buscarFiguritasEnInventarioPorUsuario(Usuario usuario);

  // Lista de figuritas que estan pegadas, por usuario
  List<RelacionFiguritaUsuario> buscarFiguritasPegadasPorUsuario(Usuario usuario);

  List<RelacionFiguritaUsuario> buscarTodasLasFiguritasPorUsuario(Usuario usuario);

  RelacionFiguritaUsuario buscarRelacionDisponible(Long idUsuario, Long idFigurita);

  void modificar(RelacionFiguritaUsuario relacion);

  void eliminar(RelacionFiguritaUsuario relacion);
}
