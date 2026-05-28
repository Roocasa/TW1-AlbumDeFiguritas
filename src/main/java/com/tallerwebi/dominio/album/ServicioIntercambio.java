package com.tallerwebi.dominio.album;

import com.tallerwebi.dominio.excepcion.IntercambioFiguritasException;
import java.util.List;

public interface ServicioIntercambio {
  List<InventarioItemDTO> obtenerFiguritasPropiasParaIntercambiar(Long idUsuario);

  List<OfertaIntercambioDTO> obtenerOfertasDeOtrosUsuarios(Long idUsuario);

  void intercambiarFiguritas(
    Long idUsuarioOrigen,
    Long idFiguritaOrigen,
    Long idUsuarioDestino,
    Long idFiguritaDestino
  ) throws IntercambioFiguritasException;
}
