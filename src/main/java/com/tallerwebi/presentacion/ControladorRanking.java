package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.ranking.ServicioRanking;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorRanking {

  private static final String ATRIBUTO_USUARIO = "USUARIO";

  private final ServicioRanking servicioRanking;

  @Autowired
  public ControladorRanking(ServicioRanking servicioRanking) {
    this.servicioRanking = servicioRanking;
  }

  @RequestMapping(path = "/ranking", method = RequestMethod.GET)
  public ModelAndView irARanking(HttpSession session) {
    Usuario usuario = (Usuario) session.getAttribute(ATRIBUTO_USUARIO);

    if (usuario == null) {
      return new ModelAndView("redirect:/login");
    }

    ModelAndView mav = new ModelAndView("ranking");
    mav.addObject("ranking", servicioRanking.obtenerRankingColeccionistas());
    return mav;
  }
}
