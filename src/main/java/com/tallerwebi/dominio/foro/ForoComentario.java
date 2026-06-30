package com.tallerwebi.dominio.foro;

import com.tallerwebi.dominio.Usuario;
import java.time.LocalDateTime;
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
public class ForoComentario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "publicacion_id")
  private ForoPublicacion publicacion;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "autor_id")
  private Usuario autor;

  @Lob
  @Column(nullable = false)
  private String contenido;

  private LocalDateTime fechaCreacion = LocalDateTime.now();

  public ForoComentario() {}

  public ForoComentario(ForoPublicacion publicacion, Usuario autor, String contenido) {
    this.publicacion = publicacion;
    this.autor = autor;
    this.contenido = contenido;
    this.fechaCreacion = LocalDateTime.now();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public ForoPublicacion getPublicacion() {
    return publicacion;
  }

  public void setPublicacion(ForoPublicacion publicacion) {
    this.publicacion = publicacion;
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

  public LocalDateTime getFechaCreacion() {
    return fechaCreacion;
  }

  public void setFechaCreacion(LocalDateTime fechaCreacion) {
    this.fechaCreacion = fechaCreacion;
  }
}
