package com.tallerwebi.dominio.album;

public class InventarioItemDTO {

  private Figurita figurita;
  private int cantidad;
  private boolean sePuedePegar;

  public InventarioItemDTO(Figurita figurita, int cantidad, boolean sePuedePegar) {
    this.figurita = figurita;
    this.cantidad = cantidad;
    this.sePuedePegar = sePuedePegar;
  }

  public Figurita getFigurita() {
    return figurita;
  }

  public void setFigurita(Figurita figurita) {
    this.figurita = figurita;
  }

  public int getCantidad() {
    return cantidad;
  }

  public void setCantidad(int cantidad) {
    this.cantidad = cantidad;
  }

  public boolean isSePuedePegar() {
    return sePuedePegar;
  }

  public void setSePuedePegar(boolean sePuedePegar) {
    this.sePuedePegar = sePuedePegar;
  }
}
