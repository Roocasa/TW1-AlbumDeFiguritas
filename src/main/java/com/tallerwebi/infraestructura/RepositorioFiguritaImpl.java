package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.album.Figurita;
import com.tallerwebi.dominio.album.Rareza;
import com.tallerwebi.dominio.album.RepositorioFigurita;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("respositorioFigurita")
public class RepositorioFiguritaImpl implements RepositorioFigurita {

  private SessionFactory sessionFactory;

  @Autowired
  public RepositorioFiguritaImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public Figurita buscarFiguritaAleatoriaPorRareza(Rareza rareza) {
    String hql = "FROM Figurita f WHERE f.rareza = :laRareza ORDER BY RAND()";

    return (Figurita) sessionFactory
      .getCurrentSession()
      .createQuery(hql)
      .setParameter("laRareza", rareza)
      .setMaxResults(1)
      .uniqueResult();
  }

  @Override
  public void guardar(Figurita figurita) {
    sessionFactory.getCurrentSession().save(figurita);
  }

  @Override
  public Figurita buscarPorId(Long id) {
    return sessionFactory.getCurrentSession().get(Figurita.class, id);
  }
}
