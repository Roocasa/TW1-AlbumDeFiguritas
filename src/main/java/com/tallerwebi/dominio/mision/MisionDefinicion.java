package com.tallerwebi.dominio.mision;

public class MisionDefinicion {

  private final String codigo;
  private final String titulo;
  private final String descripcion;
  private final int objetivo;
  private final TipoRecompensaMision tipoRecompensa;
  private final int cantidadRecompensa;

  public MisionDefinicion(
    String codigo,
    String titulo,
    String descripcion,
    int objetivo,
    TipoRecompensaMision tipoRecompensa,
    int cantidadRecompensa
  ) {
    this.codigo = codigo;
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.objetivo = objetivo;
    this.tipoRecompensa = tipoRecompensa;
    this.cantidadRecompensa = cantidadRecompensa;
  }

  public String getCodigo() {
    return codigo;
  }

  public String getTitulo() {
    return titulo;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public int getObjetivo() {
    return objetivo;
  }

  public TipoRecompensaMision getTipoRecompensa() {
    return tipoRecompensa;
  }

  public int getCantidadRecompensa() {
    return cantidadRecompensa;
  }

  public String getTextoRecompensa() {
    if (tipoRecompensa == TipoRecompensaMision.MONEDAS) {
      return cantidadRecompensa + " monedas";
    }

    if (tipoRecompensa == TipoRecompensaMision.SOBRE_PREMIUM) {
      return cantidadRecompensa + " sobre premium";
    }

    return cantidadRecompensa + " sobre comun";
  }
}
