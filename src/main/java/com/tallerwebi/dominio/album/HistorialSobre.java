package com.tallerwebi.dominio.album;

import com.tallerwebi.dominio.Usuario;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class HistorialSobre {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private Usuario usuario;

  @Temporal(TemporalType.TIMESTAMP)
  private Date fechaApertura = new Date();

  @OneToMany(mappedBy = "historialSobre", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private List<HistorialSobreDetalle> detalles = new ArrayList<>();

  public HistorialSobre() {}

  public HistorialSobre(Usuario usuario) {
    this.usuario = usuario;
  }

  public Long getId() {
    return id;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public Date getFechaApertura() {
    return fechaApertura;
  }

  public List<HistorialSobreDetalle> getDetalles() {
    return detalles;
  }

  public void agregarDetalle(Figurita figurita, boolean repetida) {
    HistorialSobreDetalle detalle = new HistorialSobreDetalle(this, figurita, repetida);
    detalles.add(detalle);
  }
}
