package com.tallerwebi.integracion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.album.*;
import com.tallerwebi.infraestructura.RepositorioInventarioImpl;
import com.tallerwebi.infraestructura.config.HibernateInfraestructuraTestConfig;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
  classes = { HibernateInfraestructuraTestConfig.class, RepositorioInventarioImpl.class }
)
public class RepositorioInventarioTest {

  @Autowired
  private RepositorioInventario repositorioInventario;

  @Autowired
  private SessionFactory sessionFactory;

  @Test
  @Transactional
  @Rollback
  public void queSePuedaGuardarUnaFiguritaEnElInventarioDeUnUsuario() {
    //Given guardamos un usuario y una figurita en la base de datos, y creamos la relacion de propiedad
    Figurita figurita = new Figurita();
    figurita.setNombre("Cucinelli Ezequiel");

    Usuario usuario = new Usuario();
    usuario.setEmail("email@deezequiel.com");

    sessionFactory.getCurrentSession().save(figurita);
    sessionFactory.getCurrentSession().save(usuario);

    RelacionFiguritaUsuario relacion = new RelacionFiguritaUsuario(usuario, figurita);

    //When guardamos la relacion en el repositorio del inventario
    repositorioInventario.guardar(relacion);

    //Then buscamos la relacion entre figurita y usuario en la base de datos
    RelacionFiguritaUsuario relacionGuardada = sessionFactory
      .getCurrentSession()
      .get(RelacionFiguritaUsuario.class, relacion.getId());

    //verificamos que hibernate la haya encontrado (que no sea nula)
    assertThat(relacionGuardada, is(notNullValue()));

    //verificamos que los datos sean los que creamos en Given
    assertThat(relacionGuardada.getPropietario().getEmail(), is("email@deezequiel.com"));
    assertThat(relacionGuardada.getFigurita().getNombre(), is("Cucinelli Ezequiel"));
  }
}
