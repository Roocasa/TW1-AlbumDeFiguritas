package com.tallerwebi.dominio.foro;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.album.Figurita;
import com.tallerwebi.dominio.album.RelacionFiguritaUsuario;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class DonacionSolidaria {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "donante_id")
  private Usuario donante;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "reclamante_id")
  private Usuario reclamante;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "figurita_id")
  private Figurita figurita;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "relacion_donada_id")
  private RelacionFiguritaUsuario relacionDonada;

  @Enumerated(EnumType.STRING)
  private EstadoDonacionSolidaria estado = EstadoDonacionSolidaria.DISPONIBLE;

  private LocalDateTime fechaCreacion = LocalDateTime.now();
  private LocalDateTime fechaReclamo;

  public DonacionSolidaria() {}

  public DonacionSolidaria(Usuario donante, RelacionFiguritaUsuario relacionDonada) {
    this.donante = donante;
    this.relacionDonada = relacionDonada;
    this.figurita = relacionDonada.getFigurita();
    this.estado = EstadoDonacionSolidaria.DISPONIBLE;
    this.fechaCreacion = LocalDateTime.now();
  }

  public void reclamar(Usuario usuarioReclamante) {
    this.reclamante = usuarioReclamante;
    this.estado = EstadoDonacionSolidaria.RECLAMADA;
    this.fechaReclamo = LocalDateTime.now();
  }

  public boolean isDisponible() {
    return EstadoDonacionSolidaria.DISPONIBLE.equals(estado);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Usuario getDonante() {
    return donante;
  }

  public void setDonante(Usuario donante) {
    this.donante = donante;
  }

  public Usuario getReclamante() {
    return reclamante;
  }

  public void setReclamante(Usuario reclamante) {
    this.reclamante = reclamante;
  }

  public Figurita getFigurita() {
    return figurita;
  }

  public void setFigurita(Figurita figurita) {
    this.figurita = figurita;
  }

  public RelacionFiguritaUsuario getRelacionDonada() {
    return relacionDonada;
  }

  public void setRelacionDonada(RelacionFiguritaUsuario relacionDonada) {
    this.relacionDonada = relacionDonada;
  }

  public EstadoDonacionSolidaria getEstado() {
    return estado;
  }

  public void setEstado(EstadoDonacionSolidaria estado) {
    this.estado = estado;
  }

  public LocalDateTime getFechaCreacion() {
    return fechaCreacion;
  }

  public void setFechaCreacion(LocalDateTime fechaCreacion) {
    this.fechaCreacion = fechaCreacion;
  }

  public LocalDateTime getFechaReclamo() {
    return fechaReclamo;
  }

  public void setFechaReclamo(LocalDateTime fechaReclamo) {
    this.fechaReclamo = fechaReclamo;
  }
}
