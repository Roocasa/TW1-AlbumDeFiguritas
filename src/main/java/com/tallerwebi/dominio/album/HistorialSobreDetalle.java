package com.tallerwebi.dominio.album;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class HistorialSobreDetalle {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private HistorialSobre historialSobre;

  @ManyToOne
  private Figurita figurita;

  private boolean repetida;

  public HistorialSobreDetalle() {}

  public HistorialSobreDetalle(HistorialSobre historialSobre, Figurita figurita, boolean repetida) {
    this.historialSobre = historialSobre;
    this.figurita = figurita;
    this.repetida = repetida;
  }

  public Long getId() {
    return id;
  }

  public HistorialSobre getHistorialSobre() {
    return historialSobre;
  }

  public Figurita getFigurita() {
    return figurita;
  }

  public boolean isRepetida() {
    return repetida;
  }

  public boolean isNueva() {
    return !repetida;
  }
}
