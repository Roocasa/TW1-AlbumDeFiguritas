package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;

public class ServicioPerfilTest {

  @Test
  public void deberiaBuscarUnUsuarioPorEmail() {
    RepositorioUsuario repositorioUsuario = mock(RepositorioUsuario.class);
    Usuario usuario = new Usuario();
    usuario.setEmail("test@unlam.edu.ar");
    when(repositorioUsuario.buscar("test@unlam.edu.ar")).thenReturn(usuario);

    ServicioPerfil servicioPerfil = new ServicioPerfilImpl(repositorioUsuario);

    Usuario usuarioEncontrado = servicioPerfil.buscarUsuarioPorEmail("test@unlam.edu.ar");

    assertThat(usuarioEncontrado, is(usuario));
    verify(repositorioUsuario, times(1)).buscar("test@unlam.edu.ar");
  }
}
