package com.tallerwebi.dominio.album;

import com.tallerwebi.dominio.Usuario;
import javax.persistence.*;

@Entity
public class Album {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(name = "usuario_id", nullable = false, unique = true)
  private Usuario usuario;

  @Column(name = "total_figuritas")
  private Integer totalFiguritas = 0;

  @Column(name = "figuritas_pegadas")
  private Integer figuritasPegadas = 0;

  @Column(name = "figuritas_faltantes")
  private Integer figuritasFaltantes = 0;

  @Column(name = "figuritas_repetidas")
  private Integer figuritasRepetidas = 0;

  public Album() {}

  public Album(Usuario usuario) {
    this.usuario = usuario;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public Integer getTotalFiguritas() {
    return totalFiguritas;
  }

  public void setTotalFiguritas(Integer totalFiguritas) {
    this.totalFiguritas = totalFiguritas;
  }

  public Integer getFiguritasPegadas() {
    return figuritasPegadas;
  }

  public void setFiguritasPegadas(Integer figuritasPegadas) {
    this.figuritasPegadas = figuritasPegadas;
  }

  public Integer getFiguritasFaltantes() {
    return figuritasFaltantes;
  }

  public void setFiguritasFaltantes(Integer figuritasFaltantes) {
    this.figuritasFaltantes = figuritasFaltantes;
  }

  public Integer getFiguritasRepetidas() {
    return figuritasRepetidas;
  }

  public void setFiguritasRepetidas(Integer figuritasRepetidas) {
    this.figuritasRepetidas = figuritasRepetidas;
  }
}
