package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.Usuario;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioUsuario")
public class RepositorioUsuarioImpl implements RepositorioUsuario {

  private final SessionFactory sessionFactory;

  @Autowired
  public RepositorioUsuarioImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public Usuario buscarUsuario(String email, String password) {
    /* Se utiliza sessionFactory.getCurrentSession() directamente para que el recurso sea gestionado por Spring y PMD no exija cerrarlo manualmente */
    return (Usuario) sessionFactory
      .getCurrentSession()
      .createCriteria(Usuario.class)
      .add(Restrictions.eq("email", email))
      .add(Restrictions.eq("password", password))
      .uniqueResult();
  }

  @Override
  public void guardar(Usuario usuario) {
    sessionFactory.getCurrentSession().save(usuario);
  }

  @Override
  public Usuario buscar(String email) {
    return (Usuario) sessionFactory
      .getCurrentSession()
      .createCriteria(Usuario.class)
      .add(Restrictions.eq("email", email))
      .uniqueResult();
  }

  @Override
  public void modificar(Usuario usuario) {
    sessionFactory.getCurrentSession().update(usuario);
  }

  @Override
  public Usuario buscarPorId(Long id) {
    return sessionFactory.getCurrentSession().get(Usuario.class, id);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Usuario> buscarTodosExcepto(Long idUsuario) {
    return sessionFactory
      .getCurrentSession()
      .createCriteria(Usuario.class)
      .add(Restrictions.ne("id", idUsuario))
      .list();
  }
}
