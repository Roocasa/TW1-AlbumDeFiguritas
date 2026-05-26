package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

public class UsuarioTest {

  @Test
  public void deberiaPermitirSetearYObtenerTodosLosDatosDelUsuario() {
    Usuario usuario = new Usuario();

    usuario.setId(10L);
    usuario.setEmail("test@unlam.edu.ar");
    usuario.setPassword("123456");
    usuario.setPais("Argentina");
    usuario.setRol("USER");
    usuario.setActivo(false);
    usuario.setFechaUltimoRegaloDiario(LocalDate.of(2026, 5, 25));

    assertThat(usuario.getId(), equalTo(10L));
    assertThat(usuario.getEmail(), equalTo("test@unlam.edu.ar"));
    assertThat(usuario.getPassword(), equalTo("123456"));
    assertThat(usuario.getPais(), equalTo("Argentina"));
    assertThat(usuario.getRol(), equalTo("USER"));
    assertThat(usuario.getActivo(), is(false));
    assertThat(usuario.getFechaUltimoRegaloDiario(), equalTo(LocalDate.of(2026, 5, 25)));
  }

  @Test
  public void activarDeberiaMarcarAlUsuarioComoActivo() {
    Usuario usuario = new Usuario();

    usuario.activar();

    assertThat(usuario.getActivo(), is(true));
  }
}
