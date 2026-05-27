package com.tallerwebi.integracion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.tallerwebi.dominio.album.Figurita;
import com.tallerwebi.dominio.album.Pais;
import com.tallerwebi.dominio.album.RepositorioFigurita;
import com.tallerwebi.infraestructura.RepositorioFiguritaImpl;
import com.tallerwebi.infraestructura.config.HibernateInfraestructuraTestConfig;
import java.util.List;
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
  @Rollback
  public void queSePuedanBuscarCincoFiguritasAleatoriasSinRepetirRegistros() {
    Pais argentina = new Pais("ARG", "Argentina", "J", 38, "ar");
    sessionFactory.getCurrentSession().save(argentina);

    for (int i = 1; i <= 6; i++) {
      Figurita figurita = new Figurita();
      figurita.setNombre("Jugador " + i);
      figurita.setPais(argentina);
      sessionFactory.getCurrentSession().save(figurita);
    }

    List<Figurita> resultado = repositorioFigurita.buscarFiguritasAleatorias(5);

    assertThat(resultado, is(notNullValue()));
    assertThat(resultado, hasSize(5));
    assertThat(resultado.stream().map(Figurita::getId).distinct().count(), is(5L));
  }
}
