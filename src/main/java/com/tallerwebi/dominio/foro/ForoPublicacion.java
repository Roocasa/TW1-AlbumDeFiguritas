package com.tallerwebi.dominio.foro;

import com.tallerwebi.dominio.Usuario;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

@Entity
public class ForoPublicacion {

  private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern(
    "dd/MM/yyyy HH:mm"
  );

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "autor_id")
  private Usuario autor;

  @Lob
  @Column(nullable = false)
  private String contenido;

  private String imagenUrl;
  private LocalDateTime fechaCreacion = LocalDateTime.now();

  public ForoPublicacion() {}

  public ForoPublicacion(Usuario autor, String contenido, String imagenUrl) {
    this.autor = autor;
    this.contenido = contenido;
    this.imagenUrl = imagenUrl;
    this.fechaCreacion = LocalDateTime.now();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Usuario getAutor() {
    return autor;
  }

  public void setAutor(Usuario autor) {
    this.autor = autor;
  }

  public String getContenido() {
    return contenido;
  }

  public void setContenido(String contenido) {
    this.contenido = contenido;
  }

  public String getImagenUrl() {
    return imagenUrl;
  }

  public void setImagenUrl(String imagenUrl) {
    this.imagenUrl = imagenUrl;
  }

  public LocalDateTime getFechaCreacion() {
    return fechaCreacion;
  }

  public String getFechaCreacionFormateada() {
    return fechaCreacion == null ? "" : fechaCreacion.format(FORMATO_FECHA);
  }

  public void setFechaCreacion(LocalDateTime fechaCreacion) {
    this.fechaCreacion = fechaCreacion;
  }
}
