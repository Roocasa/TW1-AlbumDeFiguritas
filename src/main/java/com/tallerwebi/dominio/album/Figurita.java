package com.tallerwebi.dominio.album;

import com.tallerwebi.dominio.TextoCorregido;
import javax.persistence.*;

@Entity
public class Figurita {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "nombre_jugador")
  private String nombreJugador;

  @ManyToOne
  @JoinColumn(name = "pais_id")
  private Pais pais;

  @Column(name = "numero_dentro_del_pais")
  private Integer numeroDentroDelPais;

  @Column(name = "imagen_url")
  private String imagenUrl;

  private String club;

  @Enumerated(EnumType.STRING)
  private TipoFigurita tipo = TipoFigurita.TITULAR;

  public Figurita() {}

  public Figurita(String nombreJugador, String nombrePais) {
    this.nombreJugador = nombreJugador;
    this.pais = crearPaisConNombre(nombrePais);
  }

  public Figurita(String nombreJugador, String nombrePais, TipoFigurita tipo) {
    this.nombreJugador = nombreJugador;
    this.pais = crearPaisConNombre(nombrePais);
    this.tipo = tipo;
  }

  public Figurita(String nombreJugador, String nombrePais, String imagenUrl) {
    this.nombreJugador = nombreJugador;
    this.pais = crearPaisConNombre(nombrePais);
    this.imagenUrl = imagenUrl;
  }

  private static Pais crearPaisConNombre(String nombrePais) {
    Pais paisNuevo = new Pais();
    paisNuevo.setNombre(nombrePais);
    return paisNuevo;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNombreJugador() {
    return TextoCorregido.normalizar(nombreJugador);
  }

  public void setNombreJugador(String nombreJugador) {
    this.nombreJugador = nombreJugador;
  }

  public Pais getPais() {
    return pais;
  }

  public void setPais(Pais pais) {
    this.pais = pais;
  }

  public String getNombre() {
    return getNombreJugador();
  }

  public void setNombre(String nombre) {
    this.nombreJugador = nombre;
  }

  public String getSeleccion() {
    return pais != null ? pais.getNombre() : null;
  }

  public void setSeleccion(String seleccion) {
    if (this.pais == null) {
      this.pais = new Pais();
    }
    this.pais.setNombre(seleccion);
  }

  public Integer getNumeroDentroDelPais() {
    return numeroDentroDelPais;
  }

  public void setNumeroDentroDelPais(Integer numeroDentroDelPais) {
    this.numeroDentroDelPais = numeroDentroDelPais;
  }

  public Integer getOrdenAlbum() {
    return numeroDentroDelPais;
  }

  public void setOrdenAlbum(Integer ordenAlbum) {
    this.numeroDentroDelPais = ordenAlbum;
  }

  public String getImagenUrl() {
    return imagenUrl;
  }

  public void setImagenUrl(String imagenUrl) {
    this.imagenUrl = imagenUrl;
  }

  public String getClub() {
    return TextoCorregido.normalizar(club);
  }

  public void setClub(String club) {
    this.club = club;
  }

  public String getBanderaUrl() {
    if (pais == null || pais.getCodigoBandera() == null) {
      return null;
    }
    return "https://flagcdn.com/" + pais.getCodigoBandera() + ".svg";
  }

  public TipoFigurita getTipo() {
    return tipo;
  }

  public void setTipo(TipoFigurita tipo) {
    this.tipo = tipo;
  }
}
