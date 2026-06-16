package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioPerfil;
import com.tallerwebi.dominio.Usuario;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorTienda {

  private static final String ATRIBUTO_USUARIO = "USUARIO";
  private static final String REDIRECT_LOGIN = "redirect:/login";

  private final ServicioPerfil servicioPerfil;

  @Autowired
  public ControladorTienda(ServicioPerfil servicioPerfil) {
    this.servicioPerfil = servicioPerfil;
  }

  @RequestMapping(path = "/tienda", method = RequestMethod.GET)
  public ModelAndView verTienda(HttpSession session) {
    Usuario usuario = (Usuario) session.getAttribute(ATRIBUTO_USUARIO);

    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    ModelAndView mav = new ModelAndView("tienda");
    mav.addObject("costoSobreMonedas", servicioPerfil.obtenerCostoSobreEnMonedas());
    return mav;
  }
}
