package com.tallerwebi.integracion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

import com.tallerwebi.dominio.album.Pais;
import com.tallerwebi.dominio.album.RepositorioPais;
import com.tallerwebi.infraestructura.RepositorioPaisImpl;
import com.tallerwebi.infraestructura.config.HibernateInfraestructuraTestConfig;
import java.util.List;
import java.util.stream.Collectors;
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
  classes = { HibernateInfraestructuraTestConfig.class, RepositorioPaisImpl.class }
)
public class RepositorioPaisTest {

  @Autowired
  private RepositorioPais repositorioPais;

  @Autowired
  private SessionFactory sessionFactory;

  @Test
  @Transactional
  @Rollback
  public void queSePuedanFiltrarPaisesPorGrupoYBusqueda() {
    sessionFactory.getCurrentSession().save(new Pais("ARG", "Argentina", "J", 1, "ar"));
    sessionFactory.getCurrentSession().save(new Pais("ALG", "Argelia", "J", 2, "dz"));
    sessionFactory.getCurrentSession().save(new Pais("BRA", "Brasil", "C", 3, "br"));

    List<Pais> paises = repositorioPais.buscarPorGrupoYNombreOCodigo("J", "tina");

    assertThat(paises.size(), is(1));
    assertThat(paises.get(0).getCodigo(), is("ARG"));
  }

  @Test
  @Transactional
  @Rollback
  public void queSePuedanBuscarTodosLosPaisesOrdenadosPorAlbum() {
    sessionFactory.getCurrentSession().save(new Pais("BRA", "Brasil", "C", 2, "br"));
    sessionFactory.getCurrentSession().save(new Pais("ARG", "Argentina", "J", 1, "ar"));

    List<Pais> paises = repositorioPais.buscarTodos();

    assertThat(paises.size(), is(2));
    assertThat(
      paises.stream().map(Pais::getCodigo).collect(Collectors.toList()),
      contains("ARG", "BRA")
    );
  }
}
