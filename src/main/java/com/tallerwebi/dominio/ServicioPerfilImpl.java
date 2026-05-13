package com.tallerwebi.dominio;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("servicioPerfil")
@Transactional
public class ServicioPerfilImpl implements ServicioPerfil {

  private RepositorioUsuario repositorioUsuario;

  @Autowired
  public ServicioPerfilImpl(RepositorioUsuario repositorioUsuario) {
    this.repositorioUsuario = repositorioUsuario;
  }

  @Override
  public Usuario buscarUsuarioPorEmail(String email) {
    return repositorioUsuario.buscar(email);
  }
}
