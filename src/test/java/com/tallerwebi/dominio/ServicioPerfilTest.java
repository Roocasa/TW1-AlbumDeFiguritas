package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
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
    assertThat(usuarioActualizado.getMonedas(), equalTo(20));
    assertThat(usuario.getFechaUltimoRegaloDiario(), equalTo(LocalDate.now()));
    verify(repositorioUsuario, times(1)).modificar(usuario);
  }

  @Test
  public void deberiaComprarUnSobreConMonedas() {
    RepositorioUsuario repositorioUsuario = mock(RepositorioUsuario.class);
    Usuario usuario = new Usuario();
    usuario.setId(11L);
    usuario.setMonedas(60);
    usuario.setPaquetesDisponibles(0);

    when(repositorioUsuario.buscarPorId(11L)).thenReturn(usuario);

    ServicioPerfil servicioPerfil = new ServicioPerfilImpl(repositorioUsuario);

    Usuario usuarioActualizado = servicioPerfil.comprarSobreConMonedas(11L);

    assertThat(usuarioActualizado.getMonedas(), equalTo(10));
    assertThat(usuarioActualizado.getPaquetesDisponibles(), equalTo(1));
    verify(repositorioUsuario, times(1)).modificar(usuario);
  }

  @Test
  public void deberiaComprarUnPaqueteDeMonedas() {
    RepositorioUsuario repositorioUsuario = mock(RepositorioUsuario.class);
    Usuario usuario = new Usuario();
    usuario.setId(13L);
    usuario.setMonedas(25);

    when(repositorioUsuario.buscarPorId(13L)).thenReturn(usuario);

    ServicioPerfil servicioPerfil = new ServicioPerfilImpl(repositorioUsuario);

    Usuario usuarioActualizado = servicioPerfil.comprarMonedas(13L, "monedas-500");

    assertThat(usuarioActualizado.getMonedas(), equalTo(525));
    verify(repositorioUsuario, times(1)).modificar(usuario);
  }

  @Test
  public void noDeberiaComprarMonedasSiElPaqueteNoExiste() {
    RepositorioUsuario repositorioUsuario = mock(RepositorioUsuario.class);
    Usuario usuario = new Usuario();
    usuario.setId(14L);
    usuario.setMonedas(25);

    when(repositorioUsuario.buscarPorId(14L)).thenReturn(usuario);

    ServicioPerfil servicioPerfil = new ServicioPerfilImpl(repositorioUsuario);

    Assertions.assertThrows(
      IllegalArgumentException.class,
      () -> servicioPerfil.comprarMonedas(14L, "paquete-inexistente")
    );
    verify(repositorioUsuario, never()).modificar(usuario);
  }

  @Test
  public void noDeberiaComprarUnSobreSiNoTieneMonedasSuficientes() {
    RepositorioUsuario repositorioUsuario = mock(RepositorioUsuario.class);
    Usuario usuario = new Usuario();
    usuario.setId(12L);
    usuario.setMonedas(10);

    when(repositorioUsuario.buscarPorId(12L)).thenReturn(usuario);

    ServicioPerfil servicioPerfil = new ServicioPerfilImpl(repositorioUsuario);

    Assertions.assertThrows(
      IllegalArgumentException.class,
      () -> servicioPerfil.comprarSobreConMonedas(12L)
    );
    verify(repositorioUsuario, never()).modificar(usuario);
  }

  @Test
  public void deberiaOtorgarUnSobrePorAnuncioCuandoElUsuarioNoTieneSobres() {
    RepositorioUsuario repositorioUsuario = mock(RepositorioUsuario.class);
    Usuario usuario = new Usuario();
    usuario.setId(9L);
    usuario.setPaquetesDisponibles(0);

    when(repositorioUsuario.buscarPorId(9L)).thenReturn(usuario);

    ServicioPerfil servicioPerfil = new ServicioPerfilImpl(repositorioUsuario);

    Usuario usuarioActualizado = servicioPerfil.otorgarSobrePorAnuncio(9L);

    assertThat(usuarioActualizado.getPaquetesDisponibles(), equalTo(1));
    verify(repositorioUsuario, times(1)).modificar(usuario);
  }

  @Test
  public void noDeberiaOtorgarUnSobrePorAnuncioSiElUsuarioTodaviaTieneSobres() {
    RepositorioUsuario repositorioUsuario = mock(RepositorioUsuario.class);
    Usuario usuario = new Usuario();
    usuario.setId(10L);
    usuario.setPaquetesDisponibles(3);

    when(repositorioUsuario.buscarPorId(10L)).thenReturn(usuario);

    ServicioPerfil servicioPerfil = new ServicioPerfilImpl(repositorioUsuario);

    Usuario usuarioActualizado = servicioPerfil.otorgarSobrePorAnuncio(10L);

    assertThat(usuarioActualizado.getPaquetesDisponibles(), equalTo(3));
    verify(repositorioUsuario, never()).modificar(usuario);
  }

  @Test
  public void deberiaActualizarElEmailSiLaContrasenaActualEsCorrecta() throws UsuarioExistente {
    RepositorioUsuario repositorioUsuario = mock(RepositorioUsuario.class);
    Usuario usuario = new Usuario();
    usuario.setId(4L);
    usuario.setEmail("actual@test.com");
    usuario.setPassword("123456");

    when(repositorioUsuario.buscarPorId(4L)).thenReturn(usuario);
    when(repositorioUsuario.buscar("nuevo@test.com")).thenReturn(null);

    ServicioPerfil servicioPerfil = new ServicioPerfilImpl(repositorioUsuario);

    Usuario usuarioActualizado = servicioPerfil.actualizarEmail(4L, " NUEVO@test.com ", "123456");

    assertThat(usuarioActualizado.getEmail(), equalTo("nuevo@test.com"));
    verify(repositorioUsuario).modificar(usuario);
  }

  @Test
  public void noDeberiaActualizarElEmailSiYaLoUsaOtraCuenta() {
    RepositorioUsuario repositorioUsuario = mock(RepositorioUsuario.class);
    Usuario usuario = new Usuario();
    usuario.setId(4L);
    usuario.setEmail("actual@test.com");
    usuario.setPassword("123456");

    Usuario usuarioExistente = new Usuario();
    usuarioExistente.setId(8L);
    usuarioExistente.setEmail("nuevo@test.com");

    when(repositorioUsuario.buscarPorId(4L)).thenReturn(usuario);
    when(repositorioUsuario.buscar("nuevo@test.com")).thenReturn(usuarioExistente);

    ServicioPerfil servicioPerfil = new ServicioPerfilImpl(repositorioUsuario);

    Assertions.assertThrows(
      UsuarioExistente.class,
      () -> servicioPerfil.actualizarEmail(4L, "nuevo@test.com", "123456")
    );
    verify(repositorioUsuario, never()).modificar(usuario);
  }

  @Test
  public void deberiaActualizarLaContrasenaSiLosDatosSonValidos() {
    RepositorioUsuario repositorioUsuario = mock(RepositorioUsuario.class);
    Usuario usuario = new Usuario();
    usuario.setId(12L);
    usuario.setPassword("anterior");

    when(repositorioUsuario.buscarPorId(12L)).thenReturn(usuario);

    ServicioPerfil servicioPerfil = new ServicioPerfilImpl(repositorioUsuario);

    Usuario usuarioActualizado = servicioPerfil.actualizarPassword(
      12L,
      "anterior",
      "nueva123",
      "nueva123"
    );

    assertThat(usuarioActualizado.getPassword(), equalTo("nueva123"));
    verify(repositorioUsuario).modificar(usuario);
  }

  @Test
  public void noDeberiaActualizarLaContrasenaSiLaActualEsIncorrecta() {
    RepositorioUsuario repositorioUsuario = mock(RepositorioUsuario.class);
    Usuario usuario = new Usuario();
    usuario.setId(12L);
    usuario.setPassword("anterior");

    when(repositorioUsuario.buscarPorId(12L)).thenReturn(usuario);

    ServicioPerfil servicioPerfil = new ServicioPerfilImpl(repositorioUsuario);

    Assertions.assertThrows(
      SecurityException.class,
      () -> servicioPerfil.actualizarPassword(12L, "incorrecta", "nueva123", "nueva123")
    );
    verify(repositorioUsuario, never()).modificar(usuario);
  }

  @Test
  public void deberiaActualizarLaFotoDePerfil() {
    RepositorioUsuario repositorioUsuario = mock(RepositorioUsuario.class);
    Usuario usuario = new Usuario();
    usuario.setId(16L);

    when(repositorioUsuario.buscarPorId(16L)).thenReturn(usuario);

    ServicioPerfil servicioPerfil = new ServicioPerfilImpl(repositorioUsuario);

    Usuario usuarioActualizado = servicioPerfil.actualizarFotoPerfil(
      16L,
      "/uploads/perfiles/usuario-16.png"
    );

    assertThat(usuarioActualizado.getFotoPerfil(), equalTo("/uploads/perfiles/usuario-16.png"));
    verify(repositorioUsuario).modificar(usuario);
  }
}
