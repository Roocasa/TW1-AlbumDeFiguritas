package com.tallerwebi.dominio;

public interface ServicioPerfil {
  Usuario buscarUsuarioPorEmail(String email);
  Usuario buscarUsuarioPorId(Long id);
  Usuario otorgarPaquetesDiariosSiCorresponde(Long idUsuario);
  Usuario otorgarSobrePorAnuncio(Long idUsuario);
}
