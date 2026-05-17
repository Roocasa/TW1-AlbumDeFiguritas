package com.tallerwebi.dominio.album;

import javax.persistence.*;

@Entity
public class Figurita {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nombre;
  private String seleccion;
  private Integer score;
  private Integer ordenAlbum;

  @Column(name = "imagen_url")
  private String imagenUrl;

  @Enumerated(EnumType.STRING)
  private Rareza rareza;


  public Figurita() {}


  public Figurita(String nombre, String seleccion, Rareza rareza) {
    this.nombre = nombre;
    this.seleccion = seleccion;
    this.rareza = rareza;
  }


  public Figurita(String nombre, String seleccion, Integer score, Rareza rareza, String imagenUrl) {
    this.nombre = nombre;
    this.seleccion = seleccion;
    this.rareza = rareza;
    this.score = score;
    this.imagenUrl = imagenUrl;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
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

  public Integer getOrdenAlbum() {
    return ordenAlbum;
  }

  public void setOrdenAlbum(Integer ordenAlbum) {
    this.ordenAlbum = ordenAlbum;
  }

  public String getImagenUrl() {
    return imagenUrl;
  }

  public void setImagenUrl(String imagenUrl) {
    this.imagenUrl = imagenUrl;
  }

  public Rareza getRareza() {
    return rareza;
  }

  public void setRareza(Rareza rareza) {
    this.rareza = rareza;
  }
}
