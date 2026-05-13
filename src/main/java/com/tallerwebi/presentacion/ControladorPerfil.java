package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioPerfil;
import com.tallerwebi.dominio.Usuario;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorPerfil {

  private ServicioPerfil servicioPerfil;

  @Autowired
  public ControladorPerfil(ServicioPerfil servicioPerfil) {
    this.servicioPerfil = servicioPerfil;
  }

  @RequestMapping(path = "/perfil", method = RequestMethod.GET)
  public ModelAndView verPerfil(HttpServletRequest request) {
    String email = (String) request.getSession().getAttribute("EMAIL");

    if (email == null) {
      return new ModelAndView("redirect:/login");
    }

    Usuario usuario = servicioPerfil.buscarUsuarioPorEmail(email);
    if (usuario == null) {
      return new ModelAndView("redirect:/login");
    }

    ModelMap modelo = new ModelMap();
    modelo.put("usuario", usuario);
    return new ModelAndView("perfil", modelo);
  }
}
