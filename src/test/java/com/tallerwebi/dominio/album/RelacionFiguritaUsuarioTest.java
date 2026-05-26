package com.tallerwebi.dominio.album;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import com.tallerwebi.dominio.Usuario;
import org.junit.jupiter.api.Test;

public class RelacionFiguritaUsuarioTest {

  @Test
  public void constructorConParametrosDeberiaInicializarLaRelacion() {
    Usuario usuario = new Usuario();
    Figurita figurita = new Figurita("Julian Alvarez", "Argentina", Rareza.COMUN);

    RelacionFiguritaUsuario relacion = new RelacionFiguritaUsuario(usuario, figurita);

    assertThat(relacion.getPropietario(), equalTo(usuario));
    assertThat(relacion.getFigurita(), equalTo(figurita));
    assertThat(relacion.isEstaPegadaEnElAlbum(), is(false));
  }

  @Test
  public void deberiaPermitirModificarLosDatosDeLaRelacion() {
    RelacionFiguritaUsuario relacion = new RelacionFiguritaUsuario();
    Usuario usuario = new Usuario();
    Figurita figurita = new Figurita("Thiago Almada", "Argentina", Rareza.COMUN);

    relacion.setId(7L);
    relacion.setPropietario(usuario);
    relacion.setFigurita(figurita);
    relacion.setEstaPegadaEnElAlbum(true);

    assertThat(relacion.getId(), equalTo(7L));
    assertThat(relacion.getPropietario(), equalTo(usuario));
    assertThat(relacion.getFigurita(), equalTo(figurita));
    assertThat(relacion.isEstaPegadaEnElAlbum(), is(true));
  }
}
