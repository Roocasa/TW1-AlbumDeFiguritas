package com.tallerwebi.dominio.album;

import com.tallerwebi.dominio.excepcion.PaquetesInsuficientesException;

public interface PaqueteServicio {
  public ResultadoApertura abrirPaquete(Long idUsuario, boolean esPremium)
    throws PaquetesInsuficientesException;
}
