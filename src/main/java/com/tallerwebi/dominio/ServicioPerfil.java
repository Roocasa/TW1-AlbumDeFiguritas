package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.UsuarioExistente;

public interface ServicioPerfil {
  Usuario buscarUsuarioPorEmail(String email);
  Usuario buscarUsuarioPorId(Long id);
  Usuario otorgarPaquetesDiariosSiCorresponde(Long idUsuario);
  Usuario otorgarSobrePorAnuncio(Long idUsuario);
  Usuario actualizarEmail(Long idUsuario, String nuevoEmail, String passwordActual)
    throws UsuarioExistente;
  Usuario actualizarPassword(
    Long idUsuario,
    String passwordActual,
    String nuevaPassword,
    String confirmacionPassword
  );
  Usuario actualizarFotoPerfil(Long idUsuario, String rutaFotoPerfil);
  Usuario eliminarFotoPerfil(Long idUsuario);
}
