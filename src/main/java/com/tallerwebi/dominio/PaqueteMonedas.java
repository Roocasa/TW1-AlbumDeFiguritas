package com.tallerwebi.dominio;

public class PaqueteMonedas {

  private final String codigo;
  private final String nombre;
  private final int cantidadMonedas;
  private final int precioPesos;

  public PaqueteMonedas(String codigo, String nombre, int cantidadMonedas, int precioPesos) {
    this.codigo = codigo;
    this.nombre = nombre;
    this.cantidadMonedas = cantidadMonedas;
    this.precioPesos = precioPesos;
  }

  public String getCodigo() {
    return codigo;
  }

  public String getNombre() {
    return nombre;
  }

  public int getCantidadMonedas() {
    return cantidadMonedas;
  }

  public int getPrecioPesos() {
    return precioPesos;
  }
}
