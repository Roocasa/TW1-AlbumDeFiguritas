package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
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

  @Test
  public void deberiaOtorgarPaquetesDiariosSoloUnaVezPorDia() {
    RepositorioUsuario repositorioUsuario = mock(RepositorioUsuario.class);
    Usuario usuario = new Usuario();
    usuario.setId(7L);
    usuario.setPaquetesDisponibles(3);

    when(repositorioUsuario.buscarPorId(7L)).thenReturn(usuario);

    ServicioPerfil servicioPerfil = new ServicioPerfilImpl(repositorioUsuario);

    Usuario usuarioActualizado = servicioPerfil.otorgarPaquetesDiariosSiCorresponde(7L);
    Usuario usuarioSegundaVez = servicioPerfil.otorgarPaquetesDiariosSiCorresponde(7L);

    assertThat(usuarioActualizado.getPaquetesDisponibles(), equalTo(5));
    assertThat(usuarioSegundaVez.getPaquetesDisponibles(), equalTo(5));
    assertThat(usuario.getFechaUltimoRegaloDiario(), equalTo(LocalDate.now()));
    verify(repositorioUsuario, times(1)).modificar(usuario);
  }
}
