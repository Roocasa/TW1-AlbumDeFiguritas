package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.amistad.EstadoSolicitudAmistad;
import com.tallerwebi.dominio.amistad.RepositorioAmistad;
import com.tallerwebi.dominio.amistad.SolicitudAmistad;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioAmistad")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class RepositorioAmistadImpl implements RepositorioAmistad {

  private static final String ALIAS_SOLICITANTE = "solicitante";
  private static final String ALIAS_RECEPTOR = "receptor";
  private static final String CAMPO_SOLICITANTE_ID = "solicitante.id";
  private static final String CAMPO_RECEPTOR_ID = "receptor.id";
  private static final String CAMPO_ESTADO = "estado";

  private final SessionFactory sessionFactory;

  @Autowired
  public RepositorioAmistadImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardar(SolicitudAmistad solicitud) {
    sessionFactory.getCurrentSession().save(solicitud);
  }

  @Override
  public void modificar(SolicitudAmistad solicitud) {
    sessionFactory.getCurrentSession().update(solicitud);
  }

  @Override
  public SolicitudAmistad buscarPorId(Long idSolicitud) {
    return sessionFactory.getCurrentSession().get(SolicitudAmistad.class, idSolicitud);
  }

  @Override
  public SolicitudAmistad buscarEntreUsuarios(Long idUsuario, Long idOtroUsuario) {
    return (SolicitudAmistad) sessionFactory
      .getCurrentSession()
      .createCriteria(SolicitudAmistad.class)
      .createAlias(ALIAS_SOLICITANTE, ALIAS_SOLICITANTE)
      .createAlias(ALIAS_RECEPTOR, ALIAS_RECEPTOR)
      .add(
        Restrictions.or(
          Restrictions.and(
            Restrictions.eq(CAMPO_SOLICITANTE_ID, idUsuario),
            Restrictions.eq(CAMPO_RECEPTOR_ID, idOtroUsuario)
          ),
          Restrictions.and(
            Restrictions.eq(CAMPO_SOLICITANTE_ID, idOtroUsuario),
            Restrictions.eq(CAMPO_RECEPTOR_ID, idUsuario)
          )
        )
      )
      .setMaxResults(1)
      .uniqueResult();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<SolicitudAmistad> buscarPendientesRecibidas(Long idUsuario) {
    return sessionFactory
      .getCurrentSession()
      .createCriteria(SolicitudAmistad.class)
      .createAlias(ALIAS_RECEPTOR, ALIAS_RECEPTOR)
      .add(Restrictions.eq(CAMPO_RECEPTOR_ID, idUsuario))
      .add(Restrictions.eq(CAMPO_ESTADO, EstadoSolicitudAmistad.PENDIENTE))
      .addOrder(Order.desc("fechaCreacion"))
      .list();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<SolicitudAmistad> buscarPendientesEnviadas(Long idUsuario) {
    return sessionFactory
      .getCurrentSession()
      .createCriteria(SolicitudAmistad.class)
      .createAlias(ALIAS_SOLICITANTE, ALIAS_SOLICITANTE)
      .add(Restrictions.eq(CAMPO_SOLICITANTE_ID, idUsuario))
      .add(Restrictions.eq(CAMPO_ESTADO, EstadoSolicitudAmistad.PENDIENTE))
      .addOrder(Order.desc("fechaCreacion"))
      .list();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<SolicitudAmistad> buscarAceptadas(Long idUsuario) {
    return sessionFactory
      .getCurrentSession()
      .createCriteria(SolicitudAmistad.class)
      .createAlias(ALIAS_SOLICITANTE, ALIAS_SOLICITANTE)
      .createAlias(ALIAS_RECEPTOR, ALIAS_RECEPTOR)
      .add(
        Restrictions.or(
          Restrictions.eq(CAMPO_SOLICITANTE_ID, idUsuario),
          Restrictions.eq(CAMPO_RECEPTOR_ID, idUsuario)
        )
      )
      .add(Restrictions.eq(CAMPO_ESTADO, EstadoSolicitudAmistad.ACEPTADA))
      .addOrder(Order.desc("fechaRespuesta"))
      .list();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<SolicitudAmistad> buscarRelacionadas(Long idUsuario) {
    return sessionFactory
      .getCurrentSession()
      .createCriteria(SolicitudAmistad.class)
      .createAlias(ALIAS_SOLICITANTE, ALIAS_SOLICITANTE)
      .createAlias(ALIAS_RECEPTOR, ALIAS_RECEPTOR)
      .add(
        Restrictions.or(
          Restrictions.eq(CAMPO_SOLICITANTE_ID, idUsuario),
          Restrictions.eq(CAMPO_RECEPTOR_ID, idUsuario)
        )
      )
      .list();
  }
}
