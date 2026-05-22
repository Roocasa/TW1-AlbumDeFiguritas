package com.tallerwebi.dominio.album;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Pais {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 3)
  private String codigo;

  @Column(nullable = false)
  private String nombre;

  @Column(name = "grupo_album", nullable = false, length = 1)
  private String grupo;

  @Column(name = "orden_album", nullable = false)
  private Integer ordenAlbum;

  @Column(name = "codigo_bandera")
  private String codigoBandera;

  public Pais() {}

  public Pais(
    String codigo,
    String nombre,
    String grupo,
    Integer ordenAlbum,
    String codigoBandera
  ) {
    this.codigo = codigo;
    this.nombre = nombre;
    this.grupo = grupo;
    this.ordenAlbum = ordenAlbum;
    this.codigoBandera = codigoBandera;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getCodigo() {
    return codigo;
  }

  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getGrupo() {
    return grupo;
  }

  public void setGrupo(String grupo) {
    this.grupo = grupo;
  }

  public Integer getOrdenAlbum() {
    return ordenAlbum;
  }

  public void setOrdenAlbum(Integer ordenAlbum) {
    this.ordenAlbum = ordenAlbum;
  }

  public String getCodigoBandera() {
    return codigoBandera;
  }

  public void setCodigoBandera(String codigoBandera) {
    this.codigoBandera = codigoBandera;
  }
}
