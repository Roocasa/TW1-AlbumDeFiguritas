package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.album.Pais;
import com.tallerwebi.dominio.album.RepositorioPais;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioPais")
public class RepositorioPaisImpl implements RepositorioPais {

  private final SessionFactory sessionFactory;

  @Autowired
  public RepositorioPaisImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Pais> buscarTodos() {
    return sessionFactory
      .getCurrentSession()
      .createQuery("FROM Pais p ORDER BY p.ordenAlbum")
      .list();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Pais> buscarPorGrupoYNombreOCodigo(String grupo, String busqueda) {
    StringBuilder hql = new StringBuilder("FROM Pais p WHERE 1 = 1");

    if (grupo != null) {
      hql.append(" AND p.grupo = :grupo");
    }

    if (busqueda != null) {
      hql.append(" AND (LOWER(p.nombre) LIKE :busqueda OR LOWER(p.codigo) LIKE :busqueda)");
    }

    hql.append(" ORDER BY p.ordenAlbum");

    org.hibernate.query.Query<Pais> query = sessionFactory
      .getCurrentSession()
      .createQuery(hql.toString(), Pais.class);

    if (grupo != null) {
      query.setParameter("grupo", grupo);
    }

    if (busqueda != null) {
      query.setParameter("busqueda", "%" + busqueda + "%");
    }

    return query.list();
  }

  @Override
  public Pais buscarPorCodigo(String codigo) {
    return sessionFactory
      .getCurrentSession()
      .createQuery("FROM Pais p WHERE p.codigo = :codigo", Pais.class)
      .setParameter("codigo", codigo)
      .uniqueResult();
  }
}
