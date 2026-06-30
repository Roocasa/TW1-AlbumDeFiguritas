package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.foro.DonacionSolidaria;
import com.tallerwebi.dominio.foro.EstadoDonacionSolidaria;
import com.tallerwebi.dominio.foro.ForoComentario;
import com.tallerwebi.dominio.foro.ForoPublicacion;
import com.tallerwebi.dominio.foro.RepositorioForo;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioForo")
public class RepositorioForoImpl implements RepositorioForo {

  private final SessionFactory sessionFactory;

  @Autowired
  public RepositorioForoImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardarPublicacion(ForoPublicacion publicacion) {
    sessionFactory.getCurrentSession().save(publicacion);
  }

  @Override
  public ForoPublicacion buscarPublicacionPorId(Long idPublicacion) {
    return sessionFactory.getCurrentSession().get(ForoPublicacion.class, idPublicacion);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<ForoPublicacion> buscarPublicaciones() {
    return sessionFactory
      .getCurrentSession()
      .createCriteria(ForoPublicacion.class)
      .addOrder(Order.desc("fechaCreacion"))
      .list();
  }

  @Override
  public void guardarComentario(ForoComentario comentario) {
    sessionFactory.getCurrentSession().save(comentario);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<ForoComentario> buscarComentariosDePublicacion(Long idPublicacion) {
    return sessionFactory
      .getCurrentSession()
      .createCriteria(ForoComentario.class)
      .createAlias("publicacion", "p")
      .add(Restrictions.eq("p.id", idPublicacion))
      .addOrder(Order.asc("fechaCreacion"))
      .list();
  }

  @Override
  public void guardarDonacion(DonacionSolidaria donacion) {
    sessionFactory.getCurrentSession().save(donacion);
  }

  @Override
  public void modificarDonacion(DonacionSolidaria donacion) {
    sessionFactory.getCurrentSession().update(donacion);
  }

  @Override
  public DonacionSolidaria buscarDonacionPorId(Long idDonacion) {
    return sessionFactory.getCurrentSession().get(DonacionSolidaria.class, idDonacion);
  }

  @Override
  public DonacionSolidaria buscarDonacionDisponiblePorRelacion(Long idRelacion) {
    return (DonacionSolidaria) sessionFactory
      .getCurrentSession()
      .createCriteria(DonacionSolidaria.class)
      .createAlias("relacionDonada", "r")
      .add(Restrictions.eq("r.id", idRelacion))
      .add(Restrictions.eq("estado", EstadoDonacionSolidaria.DISPONIBLE))
      .setMaxResults(1)
      .uniqueResult();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<DonacionSolidaria> buscarDonacionesDisponibles() {
    return sessionFactory
      .getCurrentSession()
      .createCriteria(DonacionSolidaria.class)
      .add(Restrictions.eq("estado", EstadoDonacionSolidaria.DISPONIBLE))
      .addOrder(Order.desc("fechaCreacion"))
      .list();
  }
}
