package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.notificacion.Notificacion;
import com.tallerwebi.dominio.notificacion.RepositorioNotificacion;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioNotificacion")
public class RepositorioNotificacionImpl implements RepositorioNotificacion {

  private static final String ALIAS_USUARIO = "usuario";
  private static final String CAMPO_USUARIO_ID = "usuario.id";
  private static final String CAMPO_LEIDA = "leida";

  private final SessionFactory sessionFactory;

  @Autowired
  public RepositorioNotificacionImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardar(Notificacion notificacion) {
    sessionFactory.getCurrentSession().save(notificacion);
  }

  @Override
  public void modificar(Notificacion notificacion) {
    sessionFactory.getCurrentSession().update(notificacion);
  }

  @Override
  public Notificacion buscarPorId(Long idNotificacion) {
    return sessionFactory.getCurrentSession().get(Notificacion.class, idNotificacion);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Notificacion> buscarUltimasPorUsuario(Long idUsuario, int limite) {
    return sessionFactory
      .getCurrentSession()
      .createCriteria(Notificacion.class)
      .createAlias(ALIAS_USUARIO, ALIAS_USUARIO)
      .add(Restrictions.eq(CAMPO_USUARIO_ID, idUsuario))
      .addOrder(Order.desc("fechaCreacion"))
      .setMaxResults(limite)
      .list();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Notificacion> buscarNoLeidasPorUsuario(Long idUsuario) {
    return sessionFactory
      .getCurrentSession()
      .createCriteria(Notificacion.class)
      .createAlias(ALIAS_USUARIO, ALIAS_USUARIO)
      .add(Restrictions.eq(CAMPO_USUARIO_ID, idUsuario))
      .add(Restrictions.eq(CAMPO_LEIDA, false))
      .list();
  }

  @Override
  public Long contarNoLeidasPorUsuario(Long idUsuario) {
    return (Long) sessionFactory
      .getCurrentSession()
      .createCriteria(Notificacion.class)
      .createAlias(ALIAS_USUARIO, ALIAS_USUARIO)
      .add(Restrictions.eq(CAMPO_USUARIO_ID, idUsuario))
      .add(Restrictions.eq(CAMPO_LEIDA, false))
      .setProjection(Projections.rowCount())
      .uniqueResult();
  }
}
