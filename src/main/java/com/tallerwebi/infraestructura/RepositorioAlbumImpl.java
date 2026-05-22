package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.album.Album;
import com.tallerwebi.dominio.album.RepositorioAlbum;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioAlbum")
public class RepositorioAlbumImpl implements RepositorioAlbum {

  private final SessionFactory sessionFactory;

  @Autowired
  public RepositorioAlbumImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public Album buscarPorUsuarioId(Long usuarioId) {
    return (Album) sessionFactory
      .getCurrentSession()
      .createCriteria(Album.class)
      .createAlias("usuario", "u")
      .add(Restrictions.eq("u.id", usuarioId))
      .uniqueResult();
  }

  @Override
  public void guardar(Album album) {
    sessionFactory.getCurrentSession().save(album);
  }

  @Override
  public void modificar(Album album) {
    sessionFactory.getCurrentSession().update(album);
  }
}
