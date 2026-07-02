package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.prode.ServicioProde;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ControladorProde {

  private static final String ATRIBUTO_USUARIO = "USUARIO";
  private static final String REDIRECT_LOGIN = "redirect:/login";
  private static final String REDIRECT_PRODE = "redirect:/prode";

  private final ServicioProde servicioProde;

  @Autowired
  public ControladorProde(ServicioProde servicioProde) {
    this.servicioProde = servicioProde;
  }

  @RequestMapping(path = "/prode", method = RequestMethod.GET)
  public ModelAndView verProde(HttpSession session) {
    Usuario usuario = obtenerUsuario(session);
    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    ModelAndView mav = new ModelAndView("prode");
    mav.addObject("partidos", servicioProde.obtenerPartidosConPronosticos(usuario.getId()));
    mav.addObject("puntajeProde", servicioProde.obtenerPuntaje(usuario.getId()));
    return mav;
  }

  @RequestMapping(path = "/prode/pronosticar", method = RequestMethod.POST)
  public ModelAndView pronosticar(
    @RequestParam("partido") Long idPartido,
    @RequestParam("golesLocal") Integer golesLocal,
    @RequestParam("golesVisitante") Integer golesVisitante,
    HttpSession session,
    RedirectAttributes ra
  ) {
    Usuario usuario = obtenerUsuario(session);
    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    try {
      servicioProde.pronosticar(usuario.getId(), idPartido, golesLocal, golesVisitante);
      ra.addFlashAttribute("mensaje", "Pronostico guardado.");
    } catch (IllegalArgumentException e) {
      ra.addFlashAttribute("error", e.getMessage());
    }

    return new ModelAndView(REDIRECT_PRODE);
  }

  @RequestMapping(path = "/prode/actualizar-resultados", method = RequestMethod.POST)
  public ModelAndView actualizarResultados(HttpSession session, RedirectAttributes ra) {
    if (obtenerUsuario(session) == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    int actualizados = servicioProde.actualizarResultados();
    ra.addFlashAttribute("mensaje", "Partidos actualizados: " + actualizados);
    return new ModelAndView(REDIRECT_PRODE);
  }

  private Usuario obtenerUsuario(HttpSession session) {
    return session == null ? null : (Usuario) session.getAttribute(ATRIBUTO_USUARIO);
  }
}
