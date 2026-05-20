package com.tallerwebi.dominio.album;

import com.tallerwebi.dominio.excepcion.PaquetesInsuficientesException;
import java.util.List;

public interface PaqueteServicio {
  public ResultadoApertura abrirPaquete(Long idUsuario, boolean esPremium)
    throws PaquetesInsuficientesException;

  public void pegarFigurita(Long idUsuario, Long idFigurita);

  List<RelacionFiguritaUsuario> obtenerFiguritasDelInventario(Long idUsuario);
}
