package com.tallerwebi.dominio.album;

public class AlbumSlotDTO {

  private final Figurita figurita;
  private final boolean pegada;
  private final boolean disponibleParaPegar;

  public AlbumSlotDTO(Figurita figurita, boolean pegada, boolean disponibleParaPegar) {
    this.figurita = figurita;
    this.pegada = pegada;
    this.disponibleParaPegar = disponibleParaPegar;
  }

  public Figurita getFigurita() {
    return figurita;
  }

  public boolean isPegada() {
    return pegada;
  }

  public boolean isDisponibleParaPegar() {
    return disponibleParaPegar;
  }
}
