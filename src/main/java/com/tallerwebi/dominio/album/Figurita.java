package com.tallerwebi.dominio.album;

import javax.persistence.*;

@Entity
public class Figurita {

  @Id //Clave primaria
  @GeneratedValue(strategy = GenerationType.IDENTITY) // ID incremental
  private Long id;

  private String nombre;
  private String seleccion;
  private Integer score;

  @Column(name = "imagen_url")
  private String imagenUrl;

  @Enumerated(EnumType.STRING)
  private Rareza rareza;

  @Column(columnDefinition = "BOOLEAN DEFAULT false")
  private boolean pegada;

  public Figurita() {}

  public Figurita(String nombre, String seleccion, Rareza rareza) {
    this.nombre = nombre;
    this.seleccion = seleccion;
    this.rareza = rareza;
    this.pegada = false;
  }

  public String getImagenUrl() {
    return imagenUrl;
  }

  public void setImagenUrl(String imagenUrl) {
    this.imagenUrl = imagenUrl;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getSeleccion() {
    return seleccion;
  }

  public void setSeleccion(String seleccion) {
    this.seleccion = seleccion;
  }

  public Integer getScore() {
    return score;
  }

  public void setScore(Integer score) {
    this.score = score;
  }

  public Rareza getRareza() {
    return rareza;
  }

  public void setRareza(Rareza rareza) {
    this.rareza = rareza;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public void pegar() {
    this.pegada = true;
  }

  public boolean isPegada() {
    return this.pegada;
  }
}
