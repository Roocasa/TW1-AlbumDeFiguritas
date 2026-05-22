package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.album.RelacionFiguritaUsuario;
import com.tallerwebi.dominio.album.RepositorioInventario;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioInventario")
public class RepositorioInventarioImpl implements RepositorioInventario {

  private static final String CAMPO_PROPIETARIO = "propietario";
  private final SessionFactory sessionFactory;

  @Autowired
  public RepositorioInventarioImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardar(RelacionFiguritaUsuario relacion) {
    // Guarda la relación en la tabla intermedia
    sessionFactory.getCurrentSession().save(relacion);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<RelacionFiguritaUsuario> buscarFiguritasEnInventarioPorUsuario(Usuario usuario) {
    // Busca en la tabla RelacionFiguritaUsuario donde el propietario sea el usuario y no estén pegadas
    return sessionFactory
      .getCurrentSession()
      .createCriteria(RelacionFiguritaUsuario.class)
      .add(Restrictions.eq(CAMPO_PROPIETARIO, usuario))
      .add(Restrictions.eq("estaPegadaEnElAlbum", false))
      .list();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<RelacionFiguritaUsuario> buscarFiguritasPegadasPorUsuario(Usuario usuario) {
    // Busca en la tabla RelacionFiguritaUsuario donde el propietario sea el usuario y estén pegadas
    return sessionFactory
      .getCurrentSession()
      .createCriteria(RelacionFiguritaUsuario.class)
      .add(Restrictions.eq(CAMPO_PROPIETARIO, usuario))
      .add(Restrictions.eq("estaPegadaEnElAlbum", true))
      .list();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<RelacionFiguritaUsuario> buscarTodasLasFiguritasPorUsuario(Usuario usuario) {
    return sessionFactory
      .getCurrentSession()
      .createCriteria(RelacionFiguritaUsuario.class)
      .add(Restrictions.eq(CAMPO_PROPIETARIO, usuario))
      .list();
  }

  @Override
  public RelacionFiguritaUsuario buscarRelacionDisponible(Long idUsuario, Long idFigurita) {
    return (RelacionFiguritaUsuario) sessionFactory
      .getCurrentSession()
      .createCriteria(RelacionFiguritaUsuario.class)
      .createAlias(CAMPO_PROPIETARIO, "p")
      .createAlias("figurita", "f")
      .add(Restrictions.eq("p.id", idUsuario))
      .add(Restrictions.eq("f.id", idFigurita))
      .add(Restrictions.eq("estaPegadaEnElAlbum", false))
      .setMaxResults(1)
      .uniqueResult();
  }

  @Override
  public void modificar(RelacionFiguritaUsuario relacion) {
    // Actualiza el estado de la relación en la base de datos
    sessionFactory.getCurrentSession().update(relacion);
  }
}
