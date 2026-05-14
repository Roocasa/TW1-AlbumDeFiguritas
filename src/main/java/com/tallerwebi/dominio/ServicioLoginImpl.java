package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import java.util.Locale;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("servicioLogin")
@Transactional
public class ServicioLoginImpl implements ServicioLogin {

  private RepositorioUsuario repositorioUsuario;

  @Autowired
  public ServicioLoginImpl(RepositorioUsuario repositorioUsuario) {
    this.repositorioUsuario = repositorioUsuario;
  }

  @Override
  public Usuario consultarUsuario(String email, String password) {
    if (email == null || password == null) {
      return null;
    }

    return repositorioUsuario.buscarUsuario(normalizarEmail(email), password);
  }

  @Override
  public void registrar(Usuario usuario) throws UsuarioExistente {
    String emailNormalizado = normalizarEmail(usuario.getEmail());
    usuario.setEmail(emailNormalizado);

    Usuario usuarioEncontrado = repositorioUsuario.buscar(emailNormalizado);
    if (usuarioEncontrado != null) {
      throw new UsuarioExistente();
    }

    if (usuario.getRol() == null || usuario.getRol().trim().isEmpty()) {
      usuario.setRol("USER");
    }

    usuario.setActivo(Boolean.TRUE);

    repositorioUsuario.guardar(usuario);
  }

  private String normalizarEmail(String email) {
    return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
  }
}
