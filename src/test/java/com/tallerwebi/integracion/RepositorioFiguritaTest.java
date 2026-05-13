package com.tallerwebi.integracion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.tallerwebi.dominio.album.Figurita;
import com.tallerwebi.dominio.album.Rareza;
import com.tallerwebi.dominio.album.RepositorioFigurita;
import com.tallerwebi.infraestructura.RepositorioFiguritaImpl;
import com.tallerwebi.infraestructura.config.HibernateInfraestructuraTestConfig;
import javax.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
  classes = { HibernateInfraestructuraTestConfig.class, RepositorioFiguritaImpl.class }
)
public class RepositorioFiguritaTest {

  @Autowired
  private RepositorioFigurita repositorioFigurita;

  @Test
  @Transactional
  @Rollback // Borra todo al terminar para no dejar basura
  public void queSePuedaBuscarUnaFiguritaAleatoriaPorSuRareza() {
    // GIVEN
    Figurita leyenda1 = new Figurita("Lionel Messi", "Argentina", Rareza.LEYENDA);
    Figurita oro1 = new Figurita("Dibu Martínez", "Argentina", Rareza.ORO);
    Figurita comun1 = new Figurita("Facundo Medina", "Argentina", Rareza.COMUN);
    Figurita leyenda2 = new Figurita("Angel Di Maria", "Argentina", Rareza.LEYENDA);
    Figurita oro2 = new Figurita("Enzo Fernandez", "Argentina", Rareza.ORO);
    Figurita comun2 = new Figurita("Gonzalo Montiel", "Argentina", Rareza.COMUN);

    repositorioFigurita.guardar(leyenda1);
    repositorioFigurita.guardar(oro1);
    repositorioFigurita.guardar(comun1);
    repositorioFigurita.guardar(leyenda2);
    repositorioFigurita.guardar(oro2);
    repositorioFigurita.guardar(comun2);

    // WHEN: Llamamos a nuestro método para que busque una de ORO al azar

    Figurita resultado1 = repositorioFigurita.buscarFiguritaAleatoriaPorRareza(Rareza.ORO);
    Figurita resultado2 = repositorioFigurita.buscarFiguritaAleatoriaPorRareza(Rareza.LEYENDA);

    //        System.out.println(resultado1.getNombre());
    //        System.out.println(resultado2.getNombre());

    // THEN: Se comprueba que el HQL haya funcionado

    assertThat(resultado1, is(notNullValue()));
    assertThat(resultado2, is(notNullValue()));

    assertThat(resultado1.getRareza(), is(Rareza.ORO));
    assertThat(resultado2.getRareza(), is(Rareza.LEYENDA));
  }

  @Test
  @Transactional
  @Rollback
  public void queSePuedaBuscarUnaFiguritaPorSuId() {
    Figurita figurita = new Figurita("Alexis Mac Allister", "Argentina", Rareza.PLATA);

    repositorioFigurita.guardar(figurita);

    Figurita resultado = repositorioFigurita.buscarPorId(figurita.getId());

    assertThat(resultado, is(notNullValue()));
    assertThat(resultado.getId(), is(figurita.getId()));
  }
}
