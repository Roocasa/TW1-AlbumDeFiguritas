package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.album.PaqueteServicio;
import com.tallerwebi.dominio.album.ServicioIntercambio;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorHistorial {

  private static final String ATRIBUTO_USUARIO = "USUARIO";
  private static final String REDIRECT_LOGIN = "redirect:/login";

  private final PaqueteServicio paqueteServicio;
  private final ServicioIntercambio servicioIntercambio;

  @Autowired
  public ControladorHistorial(
    PaqueteServicio paqueteServicio,
    ServicioIntercambio servicioIntercambio
  ) {
    this.paqueteServicio = paqueteServicio;
    this.servicioIntercambio = servicioIntercambio;
  }

  @RequestMapping(path = "/historial", method = RequestMethod.GET)
  public ModelAndView verHistorial(HttpSession session) {
    Usuario usuario = (Usuario) session.getAttribute(ATRIBUTO_USUARIO);

    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    ModelAndView mav = new ModelAndView("historial");
    mav.addObject("historialSobres", paqueteServicio.obtenerHistorialSobres(usuario.getId()));
    mav.addObject(
      "historialIntercambios",
      servicioIntercambio.obtenerHistorialIntercambios(usuario.getId())
    );
    return mav;
  }
}
