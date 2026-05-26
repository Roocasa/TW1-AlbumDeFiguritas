package com.tallerwebi.dominio;

import java.time.LocalDate;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("servicioPerfil")
@Transactional
public class ServicioPerfilImpl implements ServicioPerfil {

  private static final int PAQUETES_DIARIOS = 2;
  private static final int SOBRE_POR_ANUNCIO = 1;
  private RepositorioUsuario repositorioUsuario;

  @Autowired
  public ServicioPerfilImpl(RepositorioUsuario repositorioUsuario) {
    this.repositorioUsuario = repositorioUsuario;
  }

  @Override
  public Usuario buscarUsuarioPorEmail(String email) {
    return repositorioUsuario.buscar(email);
  }

  @Override
  public Usuario buscarUsuarioPorId(Long id) {
    return repositorioUsuario.buscarPorId(id);
  }

  @Override
  public Usuario otorgarPaquetesDiariosSiCorresponde(Long idUsuario) {
    Usuario usuario = repositorioUsuario.buscarPorId(idUsuario);

    if (usuario == null) {
      return null;
    }

    LocalDate hoy = LocalDate.now();
    if (hoy.equals(usuario.getFechaUltimoRegaloDiario())) {
      return usuario;
    }

    usuario.sumarPaquetesComunes(PAQUETES_DIARIOS);
    usuario.setFechaUltimoRegaloDiario(hoy);
    repositorioUsuario.modificar(usuario);

    return usuario;
  }

  @Override
  public Usuario otorgarSobrePorAnuncio(Long idUsuario) {
    Usuario usuario = repositorioUsuario.buscarPorId(idUsuario);

    if (usuario == null) {
      return null;
    }

    if (usuario.getPaquetesDisponibles() > 0) {
      return usuario;
    }

    usuario.sumarPaquetesComunes(SOBRE_POR_ANUNCIO);
    repositorioUsuario.modificar(usuario);

    return usuario;
  }
}
