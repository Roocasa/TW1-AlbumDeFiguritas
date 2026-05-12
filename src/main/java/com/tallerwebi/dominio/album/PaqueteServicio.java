package com.tallerwebi.dominio.album;

import com.tallerwebi.dominio.excepcion.PaquetesInsuficientesException;
import java.util.List;

public interface PaqueteServicio {
  public List<Figurita> abrirPaquete(Long idUsuario, boolean esPremium)
    throws PaquetesInsuficientesException;
}
