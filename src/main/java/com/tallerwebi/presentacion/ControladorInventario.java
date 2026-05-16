package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.album.PaqueteServicio;
import com.tallerwebi.dominio.album.ResultadoApertura;
import com.tallerwebi.dominio.excepcion.PaquetesInsuficientesException;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ControladorInventario {

  private PaqueteServicio paqueteServicio;

  @Autowired
  public ControladorInventario(PaqueteServicio paqueteServicio) {
    this.paqueteServicio = paqueteServicio;
  }

  @RequestMapping(path = "/inventario", method = RequestMethod.GET)
  public ModelAndView irAlInventario() {
    return new ModelAndView("inventario");
  }

  @RequestMapping(path = "/abrir-paquete", method = RequestMethod.GET)
  public ModelAndView abrirUnPaquete(
    @RequestParam(value = "premium", required = false, defaultValue = "false") boolean esPremium,
    HttpSession session,
    RedirectAttributes ra
  ) {
    Usuario usuario = (Usuario) session.getAttribute("USUARIO");

    try {
      ResultadoApertura resultado = paqueteServicio.abrirPaquete(usuario.getId(), esPremium);

      session.setAttribute("USUARIO", resultado.getUsuarioActualizado());

      ra.addFlashAttribute("figuritasNuevas", resultado.getFiguritasNuevas());
      ra.addFlashAttribute("paqueteAbierto", true);
    } catch (PaquetesInsuficientesException e) {
      ra.addFlashAttribute("error", e.getMessage());
    }

    return new ModelAndView("redirect:/inventario");
  }
}
