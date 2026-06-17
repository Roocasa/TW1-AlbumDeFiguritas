package com.tallerwebi.dominio.mision;

import com.tallerwebi.dominio.Usuario;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

@Entity
@Table(
  name = "mision_usuario",
  uniqueConstraints = @UniqueConstraint(columnNames = { "usuario_id", "codigoMision" })
)
public class MisionUsuario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  private Usuario usuario;

  private String codigoMision;

  @Temporal(TemporalType.TIMESTAMP)
  private Date fechaCanje;

  public MisionUsuario() {}

  public MisionUsuario(Usuario usuario, String codigoMision) {
    this.usuario = usuario;
    this.codigoMision = codigoMision;
    this.fechaCanje = new Date();
  }

  public Long getId() {
    return id;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public String getCodigoMision() {
    return codigoMision;
  }

  public Date getFechaCanje() {
    return fechaCanje;
  }
}
