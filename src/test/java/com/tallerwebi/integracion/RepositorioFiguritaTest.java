package com.tallerwebi.integracion;

import com.tallerwebi.dominio.album.Figurita;
import com.tallerwebi.dominio.album.Rareza;
import com.tallerwebi.dominio.album.RepositorioFigurita;
import com.tallerwebi.infraestructura.RepositorioFiguritaImpl;
import com.tallerwebi.infraestructura.config.HibernateInfraestructuraTestConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@ExtendWith(SpringExtension.class)

@ContextConfiguration(classes = {HibernateInfraestructuraTestConfig.class, RepositorioFiguritaImpl.class})
public class RepositorioFiguritaTest {

    @Autowired
    private RepositorioFigurita repositorioFigurita;

    @Test
    @Transactional
    @Rollback  // Borra todo al terminar para no dejar basura
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
}