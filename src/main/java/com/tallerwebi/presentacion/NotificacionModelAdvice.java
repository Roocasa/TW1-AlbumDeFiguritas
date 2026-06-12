package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.notificacion.Notificacion;
import com.tallerwebi.dominio.notificacion.ServicioNotificacion;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class NotificacionModelAdvice {

  private static final String ATRIBUTO_USUARIO = "USUARIO";

  private final ServicioNotificacion servicioNotificacion;

  @Autowired
  public NotificacionModelAdvice(ServicioNotificacion servicioNotificacion) {
    this.servicioNotificacion = servicioNotificacion;
  }

  @ModelAttribute("notificacionesNoLeidas")
  public Long agregarCantidadNoLeidas(HttpSession session) {
    Usuario usuario = (Usuario) session.getAttribute(ATRIBUTO_USUARIO);
    return usuario == null ? 0L : servicioNotificacion.contarNoLeidas(usuario.getId());
  }

  @ModelAttribute("notificacionesMenu")
  public List<Notificacion> agregarNotificacionesMenu(HttpSession session) {
    Usuario usuario = (Usuario) session.getAttribute(ATRIBUTO_USUARIO);
    return usuario == null
      ? Collections.emptyList()
      : servicioNotificacion.obtenerUltimas(usuario.getId());
  }
}
