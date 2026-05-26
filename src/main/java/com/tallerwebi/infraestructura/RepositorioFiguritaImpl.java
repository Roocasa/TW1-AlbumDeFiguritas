package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.album.Figurita;
import com.tallerwebi.dominio.album.RepositorioFigurita;
import java.util.List;
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
  @SuppressWarnings("unchecked")
  public List<Figurita> buscarFiguritasAleatorias(int cantidad) {
    String hql = "FROM Figurita f ORDER BY RAND()";

    return sessionFactory.getCurrentSession().createQuery(hql).setMaxResults(cantidad).list();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Figurita> buscarPorPaisCodigoOrdenadas(String codigoPais) {
    String hql =
      "FROM Figurita f WHERE f.pais.codigo = :codigoPais ORDER BY f.numeroDentroDelPais ASC";

    return sessionFactory
      .getCurrentSession()
      .createQuery(hql)
      .setParameter("codigoPais", codigoPais)
      .list();
  }

  @Override
  public long contarFiguritas() {
    return (Long) sessionFactory
      .getCurrentSession()
      .createQuery("SELECT COUNT(f.id) FROM Figurita f")
      .uniqueResult();
  }
}
