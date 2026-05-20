package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.album.RelacionFiguritaUsuario;
import com.tallerwebi.dominio.album.RepositorioRelacionFiguritaUsuario;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioRelacionAlbumFigurita")
public class RepositorioRelacionFiguritaUsuarioImpl implements RepositorioRelacionFiguritaUsuario {

  private SessionFactory sessionFactory;

  @Autowired
  public RepositorioRelacionFiguritaUsuarioImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardar(RelacionFiguritaUsuario relacion) {
    sessionFactory.getCurrentSession().save(relacion);
  }

  @Override
  public List<RelacionFiguritaUsuario> buscarPorUsuario(Long usuarioId) {
    String hql = "FROM RelacionFiguritaUsuario r WHERE r.propietario.id = :usuarioId";

    return (List<RelacionFiguritaUsuario>) sessionFactory
      .getCurrentSession()
      .createQuery(hql)
      .setParameter("usuarioId", usuarioId)
      .list();
  }
}
