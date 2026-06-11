package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.album.PropuestaIntercambio;
import com.tallerwebi.dominio.album.RepositorioPropuestaIntercambio;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioPropuestaIntercambio")
public class RepositorioPropuestaIntercambioImpl implements RepositorioPropuestaIntercambio {

  private final SessionFactory sessionFactory;

  @Autowired
  public RepositorioPropuestaIntercambioImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardar(PropuestaIntercambio propuesta) {
    sessionFactory.getCurrentSession().save(propuesta);
  }

  @Override
  public void modificar(PropuestaIntercambio propuesta) {
    sessionFactory.getCurrentSession().update(propuesta);
  }

  @Override
  public PropuestaIntercambio buscarPorId(Long idPropuesta) {
    return sessionFactory.getCurrentSession().get(PropuestaIntercambio.class, idPropuesta);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<PropuestaIntercambio> buscarRecibidas(Long idUsuario) {
    return sessionFactory
      .getCurrentSession()
      .createCriteria(PropuestaIntercambio.class)
      .createAlias("receptor", "receptor")
      .add(Restrictions.eq("receptor.id", idUsuario))
      .addOrder(Order.desc("id"))
      .list();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<PropuestaIntercambio> buscarEnviadas(Long idUsuario) {
    return sessionFactory
      .getCurrentSession()
      .createCriteria(PropuestaIntercambio.class)
      .createAlias("solicitante", "solicitante")
      .add(Restrictions.eq("solicitante.id", idUsuario))
      .addOrder(Order.desc("id"))
      .list();
  }
}
