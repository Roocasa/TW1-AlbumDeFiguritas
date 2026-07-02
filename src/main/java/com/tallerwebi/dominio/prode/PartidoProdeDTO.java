package com.tallerwebi.dominio.prode;

public class PartidoProdeDTO {

  private final PartidoProde partido;
  private final PronosticoProde pronostico;

  public PartidoProdeDTO(PartidoProde partido, PronosticoProde pronostico) {
    this.partido = partido;
    this.pronostico = pronostico;
  }

  public PartidoProde getPartido() {
    return partido;
  }

  public PronosticoProde getPronostico() {
    return pronostico;
  }
}
