package com.tallerwebi.dominio.notificacion;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.Usuario;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("servicioNotificacion")
@Transactional
public class ServicioNotificacionImpl implements ServicioNotificacion {

  private static final int LIMITE_NOTIFICACIONES_MENU = 6;

  private final RepositorioNotificacion repositorioNotificacion;
  private final RepositorioUsuario repositorioUsuario;

  @Autowired
  public ServicioNotificacionImpl(
    RepositorioNotificacion repositorioNotificacion,
    RepositorioUsuario repositorioUsuario
  ) {
    this.repositorioNotificacion = repositorioNotificacion;
    this.repositorioUsuario = repositorioUsuario;
  }

  @Override
  public void avisarPropuestaRecibida(Long idUsuarioDestino, String usuarioOrigen) {
    Usuario usuario = repositorioUsuario.buscarPorId(idUsuarioDestino);
    if (usuario == null) {
      return;
    }

    repositorioNotificacion.guardar(
      new Notificacion(
        usuario,
        "Nueva propuesta",
        usuarioOrigen + " te envio una propuesta de intercambio.",
        "/intercambios"
      )
    );
  }

  @Override
  public void avisarPropuestaRespondida(Long idUsuarioSolicitante, boolean aceptada) {
    Usuario usuario = repositorioUsuario.buscarPorId(idUsuarioSolicitante);
    if (usuario == null) {
      return;
    }

    repositorioNotificacion.guardar(
      new Notificacion(
        usuario,
        aceptada ? "Propuesta aceptada" : "Propuesta rechazada",
        aceptada
          ? "Aceptaron tu propuesta de intercambio."
          : "Rechazaron tu propuesta de intercambio.",
        "/intercambios"
      )
    );
  }

  @Override
  public void avisarSobresDiariosDisponibles(Long idUsuario, int cantidad) {
    Usuario usuario = repositorioUsuario.buscarPorId(idUsuario);
    if (usuario == null) {
      return;
    }

    repositorioNotificacion.guardar(
      new Notificacion(
        usuario,
        "Sobres diarios disponibles",
        "Ya tenes " + cantidad + " sobres diarios para abrir.",
        "/inventario"
      )
    );
  }

  @Override
  public List<Notificacion> obtenerUltimas(Long idUsuario) {
    return repositorioNotificacion.buscarUltimasPorUsuario(idUsuario, LIMITE_NOTIFICACIONES_MENU);
  }

  @Override
  public Long contarNoLeidas(Long idUsuario) {
    return repositorioNotificacion.contarNoLeidasPorUsuario(idUsuario);
  }

  @Override
  public String marcarComoLeida(Long idUsuario, Long idNotificacion) {
    Notificacion notificacion = repositorioNotificacion.buscarPorId(idNotificacion);

    if (
      notificacion == null ||
      notificacion.getUsuario() == null ||
      !notificacion.getUsuario().getId().equals(idUsuario)
    ) {
      return "/home";
    }

    notificacion.marcarComoLeida();
    repositorioNotificacion.modificar(notificacion);
    return notificacion.getDestino() == null ? "/home" : notificacion.getDestino();
  }

  @Override
  public void marcarTodasComoLeidas(Long idUsuario) {
    for (Notificacion notificacion : repositorioNotificacion.buscarNoLeidasPorUsuario(idUsuario)) {
      notificacion.marcarComoLeida();
      repositorioNotificacion.modificar(notificacion);
    }
  }
}
