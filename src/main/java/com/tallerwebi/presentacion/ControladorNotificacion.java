package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.notificacion.ServicioNotificacion;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorNotificacion {

  private static final String ATRIBUTO_USUARIO = "USUARIO";
  private static final String REDIRECT_LOGIN = "redirect:/login";

  private final ServicioNotificacion servicioNotificacion;

  @Autowired
  public ControladorNotificacion(ServicioNotificacion servicioNotificacion) {
    this.servicioNotificacion = servicioNotificacion;
  }

  @RequestMapping(path = "/notificaciones/marcar-leidas", method = RequestMethod.POST)
  public ModelAndView marcarLeidas(HttpSession session) {
    Usuario usuario = (Usuario) session.getAttribute(ATRIBUTO_USUARIO);
    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    servicioNotificacion.marcarTodasComoLeidas(usuario.getId());
    return new ModelAndView("redirect:/home");
  }

  @RequestMapping(path = "/notificaciones/abrir", method = RequestMethod.GET)
  public ModelAndView abrirNotificacion(
    @RequestParam("id") Long idNotificacion,
    HttpSession session
  ) {
    Usuario usuario = (Usuario) session.getAttribute(ATRIBUTO_USUARIO);
    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    String destino = servicioNotificacion.marcarComoLeida(usuario.getId(), idNotificacion);
    return new ModelAndView("redirect:" + destino);
  }
}
