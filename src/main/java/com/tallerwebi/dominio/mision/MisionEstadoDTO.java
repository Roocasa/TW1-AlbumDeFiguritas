package com.tallerwebi.dominio.mision;

public class MisionEstadoDTO {

  private final MisionDefinicion mision;
  private final int progreso;
  private final boolean completada;
  private final boolean canjeada;

  public MisionEstadoDTO(
    MisionDefinicion mision,
    int progreso,
    boolean completada,
    boolean canjeada
  ) {
    this.mision = mision;
    this.progreso = Math.min(progreso, mision.getObjetivo());
    this.completada = completada;
    this.canjeada = canjeada;
  }

  public MisionDefinicion getMision() {
    return mision;
  }

  public int getProgreso() {
    return progreso;
  }

  public int getObjetivo() {
    return mision.getObjetivo();
  }

  public int getPorcentaje() {
    if (mision.getObjetivo() <= 0) {
      return 100;
    }

    return Math.min(100, (progreso * 100) / mision.getObjetivo());
  }

  public boolean isCompletada() {
    return completada;
  }

  public boolean isCanjeada() {
    return canjeada;
  }

  public boolean isDisponibleParaCanjear() {
    return completada && !canjeada;
  }
}
