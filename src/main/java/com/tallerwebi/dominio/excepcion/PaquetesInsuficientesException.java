package com.tallerwebi.dominio.excepcion;

public class PaquetesInsuficientesException extends Exception {

  private static final long serialVersionUID = 1L;

  public PaquetesInsuficientesException(String message) {
    super(message);
  }
}
