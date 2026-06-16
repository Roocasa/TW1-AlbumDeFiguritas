package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import com.tallerwebi.dominio.notificacion.ServicioNotificacion;
import java.time.LocalDate;
import java.util.Locale;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("servicioPerfil")
@Transactional
public class ServicioPerfilImpl implements ServicioPerfil {

  private static final int PAQUETES_DIARIOS = 2;
  private static final int MONEDAS_DIARIAS = 20;
  private static final int COSTO_SOBRE_MONEDAS = 50;
  private static final int SOBRE_POR_ANUNCIO = 1;
  private static final int MINIMO_CARACTERES_PASSWORD = 6;
  private RepositorioUsuario repositorioUsuario;
  private ServicioNotificacion servicioNotificacion;

  @Autowired
  public ServicioPerfilImpl(
    RepositorioUsuario repositorioUsuario,
    ServicioNotificacion servicioNotificacion
  ) {
    this.repositorioUsuario = repositorioUsuario;
    this.servicioNotificacion = servicioNotificacion;
  }

  public ServicioPerfilImpl(RepositorioUsuario repositorioUsuario) {
    this(repositorioUsuario, null);
  }

  @Override
  public Usuario buscarUsuarioPorEmail(String email) {
    return repositorioUsuario.buscar(normalizarEmail(email));
  }

  @Override
  public Usuario buscarUsuarioPorId(Long id) {
    return repositorioUsuario.buscarPorId(id);
  }

  @Override
  public Usuario otorgarPaquetesDiariosSiCorresponde(Long idUsuario) {
    Usuario usuario = repositorioUsuario.buscarPorId(idUsuario);

    if (usuario == null) {
      return null;
    }

    LocalDate hoy = LocalDate.now();
    if (hoy.equals(usuario.getFechaUltimoRegaloDiario())) {
      return usuario;
    }

    usuario.sumarPaquetesComunes(PAQUETES_DIARIOS);
    usuario.sumarMonedas(MONEDAS_DIARIAS);
    usuario.setFechaUltimoRegaloDiario(hoy);
    repositorioUsuario.modificar(usuario);
    avisarSobresDiariosDisponibles(usuario.getId());

    return usuario;
  }

  @Override
  public Usuario comprarSobreConMonedas(Long idUsuario) {
    Usuario usuario = repositorioUsuario.buscarPorId(idUsuario);

    if (usuario == null) {
      return null;
    }

    if (usuario.getMonedas() < COSTO_SOBRE_MONEDAS) {
      throw new IllegalArgumentException("No tenes suficientes monedas para comprar un sobre.");
    }

    usuario.gastarMonedas(COSTO_SOBRE_MONEDAS);
    usuario.sumarPaquetesComunes(1);
    repositorioUsuario.modificar(usuario);

    return usuario;
  }

  @Override
  public int obtenerCostoSobreEnMonedas() {
    return COSTO_SOBRE_MONEDAS;
  }

  @Override
  public Usuario otorgarSobrePorAnuncio(Long idUsuario) {
    Usuario usuario = repositorioUsuario.buscarPorId(idUsuario);

    if (usuario == null) {
      return null;
    }

    if (usuario.getPaquetesDisponibles() > 0) {
      return usuario;
    }

    usuario.sumarPaquetesComunes(SOBRE_POR_ANUNCIO);
    repositorioUsuario.modificar(usuario);

    return usuario;
  }

  @Override
  public Usuario actualizarEmail(Long idUsuario, String nuevoEmail, String passwordActual)
    throws UsuarioExistente {
    Usuario usuario = obtenerUsuarioPorId(idUsuario);
    validarPasswordActual(usuario, passwordActual);

    String emailNormalizado = validarNuevoEmail(usuario, nuevoEmail);
    asegurarQueElEmailEsteDisponible(usuario, emailNormalizado);

    usuario.setEmail(emailNormalizado);
    repositorioUsuario.modificar(usuario);
    return usuario;
  }

  @Override
  public Usuario actualizarPassword(
    Long idUsuario,
    String passwordActual,
    String nuevaPassword,
    String confirmacionPassword
  ) {
    Usuario usuario = obtenerUsuarioPorId(idUsuario);
    validarPasswordActual(usuario, passwordActual);

    if (nuevaPassword == null || nuevaPassword.length() < MINIMO_CARACTERES_PASSWORD) {
      throw new IllegalArgumentException("La nueva contrasena debe tener al menos 6 caracteres.");
    }

    if (!nuevaPassword.equals(confirmacionPassword)) {
      throw new IllegalArgumentException("La confirmacion de la nueva contrasena no coincide.");
    }

    if (nuevaPassword.equals(usuario.getPassword())) {
      throw new IllegalArgumentException("La nueva contrasena debe ser distinta a la actual.");
    }

    usuario.setPassword(nuevaPassword);
    repositorioUsuario.modificar(usuario);
    return usuario;
  }

  @Override
  public Usuario actualizarFotoPerfil(Long idUsuario, String rutaFotoPerfil) {
    Usuario usuario = obtenerUsuarioPorId(idUsuario);
    usuario.setFotoPerfil(rutaFotoPerfil);
    repositorioUsuario.modificar(usuario);
    return usuario;
  }

  @Override
  public Usuario eliminarFotoPerfil(Long idUsuario) {
    Usuario usuario = obtenerUsuarioPorId(idUsuario);
    usuario.setFotoPerfil(null);
    repositorioUsuario.modificar(usuario);
    return usuario;
  }

  private Usuario obtenerUsuarioPorId(Long idUsuario) {
    Usuario usuario = repositorioUsuario.buscarPorId(idUsuario);
    if (usuario == null) {
      throw new IllegalArgumentException("No encontramos tu usuario.");
    }
    return usuario;
  }

  private void validarPasswordActual(Usuario usuario, String passwordActual) {
    if (passwordActual == null || !passwordActual.equals(usuario.getPassword())) {
      throw new SecurityException("La contrasena actual no es correcta.");
    }
  }

  private String validarNuevoEmail(Usuario usuario, String nuevoEmail) {
    String emailNormalizado = normalizarEmail(nuevoEmail);
    if (emailNormalizado == null || emailNormalizado.isEmpty()) {
      throw new IllegalArgumentException("Ingresa un email valido.");
    }

    if (emailNormalizado.equals(usuario.getEmail())) {
      throw new IllegalArgumentException("El nuevo email debe ser distinto al actual.");
    }

    return emailNormalizado;
  }

  private void asegurarQueElEmailEsteDisponible(Usuario usuario, String emailNormalizado)
    throws UsuarioExistente {
    Usuario usuarioConEseEmail = repositorioUsuario.buscar(emailNormalizado);
    if (usuarioConEseEmail == null || usuarioConEseEmail.getId() == null) {
      return;
    }

    if (!usuarioConEseEmail.getId().equals(usuario.getId())) {
      throw new UsuarioExistente();
    }
  }

  private String normalizarEmail(String email) {
    return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
  }

  private void avisarSobresDiariosDisponibles(Long idUsuario) {
    if (servicioNotificacion != null) {
      servicioNotificacion.avisarSobresDiariosDisponibles(idUsuario, PAQUETES_DIARIOS);
    }
  }
}
