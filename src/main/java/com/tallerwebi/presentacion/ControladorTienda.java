package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioPerfil;
import com.tallerwebi.dominio.Usuario;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ControladorTienda {

  private static final String ATRIBUTO_USUARIO = "USUARIO";
  private static final String REDIRECT_LOGIN = "redirect:/login";
  private static final String REDIRECT_TIENDA = "redirect:/tienda";
  private static final String FLASH_ERROR = "error";

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
    mav.addObject("paquetesMonedas", servicioPerfil.obtenerPaquetesMonedas());
    return mav;
  }

  @RequestMapping(path = "/comprar-monedas", method = RequestMethod.GET)
  public ModelAndView comprarMonedas(
    @RequestParam("paquete") String paquete,
    HttpSession session,
    RedirectAttributes ra
  ) {
    Usuario usuario = (Usuario) session.getAttribute(ATRIBUTO_USUARIO);

    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    try {
      Usuario usuarioActualizado = servicioPerfil.comprarMonedas(usuario.getId(), paquete);
      session.setAttribute(ATRIBUTO_USUARIO, usuarioActualizado);
      ra.addFlashAttribute("mensajeSobre", "Compra simulada: se acreditaron tus monedas.");
    } catch (IllegalArgumentException e) {
      ra.addFlashAttribute(FLASH_ERROR, e.getMessage());
    }

    return new ModelAndView(REDIRECT_TIENDA);
  }
}
