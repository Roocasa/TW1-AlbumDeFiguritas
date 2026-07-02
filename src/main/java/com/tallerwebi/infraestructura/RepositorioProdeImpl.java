package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.prode.PartidoProde;
import com.tallerwebi.dominio.prode.PronosticoProde;
import com.tallerwebi.dominio.prode.RepositorioProde;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioProde")
public class RepositorioProdeImpl implements RepositorioProde {

  private final SessionFactory sessionFactory;

  @Autowired
  public RepositorioProdeImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<PartidoProde> buscarPartidos() {
    return sessionFactory
      .getCurrentSession()
      .createCriteria(PartidoProde.class)
      .addOrder(Order.asc("fecha"))
      .list();
  }

  @Override
  public PartidoProde buscarPartido(Long id) {
    return sessionFactory.getCurrentSession().get(PartidoProde.class, id);
  }

  @Override
  public PartidoProde buscarPartidoPorIdApi(Long idApi) {
    return (PartidoProde) sessionFactory
      .getCurrentSession()
      .createCriteria(PartidoProde.class)
      .add(Restrictions.eq("idApi", idApi))
      .uniqueResult();
  }

  @Override
  public void guardarPartido(PartidoProde partido) {
    sessionFactory.getCurrentSession().save(partido);
  }

  @Override
  public void modificarPartido(PartidoProde partido) {
    sessionFactory.getCurrentSession().update(partido);
  }

  @Override
  public PronosticoProde buscarPronostico(Usuario usuario, PartidoProde partido) {
    if (usuario == null || partido == null) {
      return null;
    }

    return (PronosticoProde) sessionFactory
      .getCurrentSession()
      .createCriteria(PronosticoProde.class)
      .add(Restrictions.eq("usuario", usuario))
      .add(Restrictions.eq("partido", partido))
      .uniqueResult();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<PronosticoProde> buscarPronosticosPorUsuario(Long idUsuario) {
    return sessionFactory
      .getCurrentSession()
      .createCriteria(PronosticoProde.class, "pronostico")
      .createAlias("pronostico.usuario", "usuario")
      .add(Restrictions.eq("usuario.id", idUsuario))
      .list();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<PronosticoProde> buscarPronosticosPorPartido(PartidoProde partido) {
    return sessionFactory
      .getCurrentSession()
      .createCriteria(PronosticoProde.class)
      .add(Restrictions.eq("partido", partido))
      .list();
  }

  @Override
  public void guardarPronostico(PronosticoProde pronostico) {
    sessionFactory.getCurrentSession().save(pronostico);
  }

  @Override
  public void modificarPronostico(PronosticoProde pronostico) {
    sessionFactory.getCurrentSession().update(pronostico);
  }
}
