package com.tallerwebi.dominio.album;

import com.tallerwebi.dominio.excepcion.IntercambioFiguritasException;
import java.util.List;

public interface ServicioIntercambio {
  List<InventarioItemDTO> obtenerFiguritasPropiasParaIntercambiar(Long idUsuario);

  List<OfertaIntercambioDTO> obtenerOfertasDeOtrosUsuarios(Long idUsuario);

  List<PropuestaIntercambio> obtenerPropuestasRecibidas(Long idUsuario);

  List<PropuestaIntercambio> obtenerPropuestasEnviadas(Long idUsuario);

  List<HistorialIntercambioDTO> obtenerHistorialIntercambios(Long idUsuario);

  void enviarPropuesta(
    Long idUsuarioOrigen,
    Long idFiguritaOrigen,
    Long idUsuarioDestino,
    Long idFiguritaDestino
  ) throws IntercambioFiguritasException;

  void aceptarPropuesta(Long idUsuarioReceptor, Long idPropuesta)
    throws IntercambioFiguritasException;

  void rechazarPropuesta(Long idUsuarioReceptor, Long idPropuesta)
    throws IntercambioFiguritasException;

  void intercambiarFiguritas(
    Long idUsuarioOrigen,
    Long idFiguritaOrigen,
    Long idUsuarioDestino,
    Long idFiguritaDestino
  ) throws IntercambioFiguritasException;
}
