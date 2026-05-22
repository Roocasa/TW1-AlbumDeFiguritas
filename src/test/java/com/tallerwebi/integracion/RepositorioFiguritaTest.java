package com.tallerwebi.integracion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.tallerwebi.dominio.album.Figurita;
import com.tallerwebi.dominio.album.Pais;
import com.tallerwebi.dominio.album.Rareza;
import com.tallerwebi.dominio.album.RepositorioFigurita;
import com.tallerwebi.infraestructura.RepositorioFiguritaImpl;
import com.tallerwebi.infraestructura.config.HibernateInfraestructuraTestConfig;
import javax.transaction.Transactional;
import org.hibernate.SessionFactory;
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

  @Autowired
  private SessionFactory sessionFactory;

  @Test
  @Transactional
  @Rollback // Borra todo al terminar para no dejar basura
  public void queSePuedaBuscarUnaFiguritaAleatoriaPorSuRareza() {
    //GIVEN .. creo dos figuritas de rarezas diferentes
    Pais argentina = new Pais("ARG", "Argentina", "J", 38, "ar");
    Pais belgica = new Pais("BEL", "Belgica", "G", 25, "be");
    sessionFactory.getCurrentSession().save(argentina);
    sessionFactory.getCurrentSession().save(belgica);

    Figurita figOro = new Figurita();
    figOro.setNombre("Kevin De Bruyne");
    figOro.setPais(belgica);
    figOro.setRareza(Rareza.ORO);
    sessionFactory.getCurrentSession().save(figOro);

    Figurita figLeyenda = new Figurita();
    figLeyenda.setNombre("Lionel Messi");
    figLeyenda.setPais(argentina);
    figLeyenda.setRareza(Rareza.LEYENDA);
    sessionFactory.getCurrentSession().save(figLeyenda);

    //WHEN busco dos figuritas por rareza
    Figurita resultado1 = repositorioFigurita.buscarFiguritaAleatoriaPorRareza(Rareza.ORO);
    Figurita resultado2 = repositorioFigurita.buscarFiguritaAleatoriaPorRareza(Rareza.LEYENDA);

    //    System.out.println(resultado1.getNombre());
    //    System.out.println(resultado2.getNombre());

    // THEN: Se comprueba que el HQL haya funcionado, las figuritas obtenidas son de la rareza que fue guardada en la sesion actual
    assertThat(resultado1, is(notNullValue()));
    assertThat(resultado2, is(notNullValue()));

    assertThat(resultado1.getRareza(), is(Rareza.ORO));
    assertThat(resultado2.getRareza(), is(Rareza.LEYENDA));
  }
}
