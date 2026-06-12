package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.album.HistorialSobre;
import com.tallerwebi.dominio.album.RepositorioHistorialSobre;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioHistorialSobre")
public class RepositorioHistorialSobreImpl implements RepositorioHistorialSobre {

  private final SessionFactory sessionFactory;

  @Autowired
  public RepositorioHistorialSobreImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardar(HistorialSobre historialSobre) {
    sessionFactory.getCurrentSession().save(historialSobre);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<HistorialSobre> buscarPorUsuario(Long idUsuario) {
    return sessionFactory
      .getCurrentSession()
      .createCriteria(HistorialSobre.class)
      .createAlias("usuario", "usuario")
      .add(Restrictions.eq("usuario.id", idUsuario))
      .addOrder(Order.desc("fechaApertura"))
      .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
      .list();
  }
}
