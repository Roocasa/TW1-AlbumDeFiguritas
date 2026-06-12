package com.tallerwebi.dominio.album;

import com.tallerwebi.dominio.excepcion.CanjeFiguritasException;
import com.tallerwebi.dominio.excepcion.PaquetesInsuficientesException;
import java.util.List;

public interface PaqueteServicio {
  public ResultadoApertura abrirPaquete(Long idUsuario) throws PaquetesInsuficientesException;

  List<HistorialSobre> obtenerHistorialSobres(Long idUsuario);

  public void pegarFigurita(Long idUsuario, Long idFigurita);

  List<InventarioItemDTO> obtenerFiguritasDelInventario(Long idUsuario);

  void canjearRepetidasPorPaquete(Long idUsuario) throws CanjeFiguritasException;

  Figurita canjearRepetidasPorEscudo(Long idUsuario) throws CanjeFiguritasException;

  int obtenerCantidadTotalRepetidas(Long idUsuario);

  int obtenerCostoCanjePaquete();

  int obtenerCostoCanjeEscudo();
}
