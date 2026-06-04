package com.tallerwebi.dominio;

import java.io.IOException;

public interface ServicioFotoPerfil {
  String guardarFoto(
    Long idUsuario,
    String nombreOriginal,
    String tipoContenido,
    byte[] contenido,
    String fotoActual
  ) throws IOException;

  void eliminarFoto(String rutaFoto) throws IOException;
}
