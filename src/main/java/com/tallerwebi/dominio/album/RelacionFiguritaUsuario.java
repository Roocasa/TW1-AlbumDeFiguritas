package com.tallerwebi.dominio.album;

import com.tallerwebi.dominio.Usuario;
import javax.persistence.*;

@Entity
public class RelacionFiguritaUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id") // FK en msql
    private Usuario propietario;

    @ManyToOne
    @JoinColumn(name = "figurita_id") // FK en msql
    private Figurita figurita;

    private boolean estaPegadaEnElAlbum;

    public RelacionFiguritaUsuario() {
    }


    public RelacionFiguritaUsuario(Usuario propietario, Figurita figurita) {
        this.propietario = propietario;
        this.figurita = figurita;
        this.estaPegadaEnElAlbum = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getPropietario() {
        return propietario;
    }

    public void setPropietario(Usuario propietario) {
        this.propietario = propietario;
    }

    public Figurita getFigurita() {
        return figurita;
    }

    public void setFigurita(Figurita figurita) {
        this.figurita = figurita;
    }

    public boolean isEstaPegadaEnElAlbum() {
        return estaPegadaEnElAlbum;
    }

    public void setEstaPegadaEnElAlbum(boolean estaPegadaEnElAlbum) {
        this.estaPegadaEnElAlbum = estaPegadaEnElAlbum;
    }
}