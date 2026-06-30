package com.tallerwebi.dominio.foro;

import java.util.List;

public interface RepositorioForo {
  void guardarPublicacion(ForoPublicacion publicacion);
  ForoPublicacion buscarPublicacionPorId(Long idPublicacion);
  List<ForoPublicacion> buscarPublicaciones();
  void guardarComentario(ForoComentario comentario);
  List<ForoComentario> buscarComentariosDePublicacion(Long idPublicacion);
  void guardarDonacion(DonacionSolidaria donacion);
  void modificarDonacion(DonacionSolidaria donacion);
  DonacionSolidaria buscarDonacionPorId(Long idDonacion);
  DonacionSolidaria buscarDonacionDisponiblePorRelacion(Long idRelacion);
  List<DonacionSolidaria> buscarDonacionesDisponibles();
}
