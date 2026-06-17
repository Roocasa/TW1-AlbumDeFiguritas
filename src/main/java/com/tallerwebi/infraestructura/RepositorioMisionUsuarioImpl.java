package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.mision.MisionUsuario;
import com.tallerwebi.dominio.mision.RepositorioMisionUsuario;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioMisionUsuario")
public class RepositorioMisionUsuarioImpl implements RepositorioMisionUsuario {

  private static final String ALIAS_USUARIO = "usuario";
  private static final String CAMPO_USUARIO_ID = "usuario.id";

  private final SessionFactory sessionFactory;

  @Autowired
  public RepositorioMisionUsuarioImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardar(MisionUsuario misionUsuario) {
    sessionFactory.getCurrentSession().save(misionUsuario);
  }

  @Override
  public MisionUsuario buscarPorUsuarioYCodigo(Long idUsuario, String codigoMision) {
    return (MisionUsuario) sessionFactory
      .getCurrentSession()
      .createCriteria(MisionUsuario.class)
      .createAlias(ALIAS_USUARIO, ALIAS_USUARIO)
      .add(Restrictions.eq(CAMPO_USUARIO_ID, idUsuario))
      .add(Restrictions.eq("codigoMision", codigoMision))
      .uniqueResult();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<MisionUsuario> buscarPorUsuario(Long idUsuario) {
    return sessionFactory
      .getCurrentSession()
      .createCriteria(MisionUsuario.class)
      .createAlias(ALIAS_USUARIO, ALIAS_USUARIO)
      .add(Restrictions.eq(CAMPO_USUARIO_ID, idUsuario))
      .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
      .list();
  }
}
