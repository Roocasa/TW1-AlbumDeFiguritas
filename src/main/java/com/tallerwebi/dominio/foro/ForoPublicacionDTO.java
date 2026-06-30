package com.tallerwebi.dominio.foro;

import java.util.List;

public class ForoPublicacionDTO {

  private final ForoPublicacion publicacion;
  private final List<ForoComentario> comentarios;

  public ForoPublicacionDTO(ForoPublicacion publicacion, List<ForoComentario> comentarios) {
    this.publicacion = publicacion;
    this.comentarios = comentarios;
  }

  public ForoPublicacion getPublicacion() {
    return publicacion;
  }

  public List<ForoComentario> getComentarios() {
    return comentarios;
  }
}
