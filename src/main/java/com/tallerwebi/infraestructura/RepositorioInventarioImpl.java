package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.album.RelacionFiguritaUsuario;
import com.tallerwebi.dominio.album.RepositorioInventario;
import com.tallerwebi.dominio.Usuario;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("repositorioInventario")
public class RepositorioInventarioImpl implements RepositorioInventario {

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
        return sessionFactory.getCurrentSession()
                .createCriteria(RelacionFiguritaUsuario.class)
                .add(Restrictions.eq("propietario", usuario))
                .add(Restrictions.eq("estaPegadaEnElAlbum", false))
                .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<RelacionFiguritaUsuario> buscarFiguritasPegadasPorUsuario(Usuario usuario) {

        // Busca en la tabla RelacionFiguritaUsuario donde el propietario sea el usuario y estén pegadas
        return sessionFactory.getCurrentSession()
                .createCriteria(RelacionFiguritaUsuario.class)
                .add(Restrictions.eq("propietario", usuario))
                .add(Restrictions.eq("estaPegadaEnElAlbum", true))
                .list();
    }
}