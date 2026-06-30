package com.tallerwebi.dominio.foro;

import com.tallerwebi.dominio.album.InventarioItemDTO;
import com.tallerwebi.dominio.excepcion.IntercambioFiguritasException;
import java.util.List;

public interface ServicioForo {
  List<ForoPublicacionDTO> obtenerPublicaciones();
  void publicar(Long idUsuario, String contenido, String imagenUrl);
  void comentar(Long idUsuario, Long idPublicacion, String contenido);
  List<InventarioItemDTO> obtenerFiguritasRepetidasParaDonar(Long idUsuario);
  List<DonacionSolidaria> obtenerDonacionesDisponibles(Long idUsuario);
  void donarFigurita(Long idUsuario, Long idFigurita) throws IntercambioFiguritasException;
  void reclamarDonacion(Long idUsuario, Long idDonacion) throws IntercambioFiguritasException;
}
