package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.album.PaqueteServicio;
import com.tallerwebi.dominio.album.RelacionFiguritaUsuario;
import com.tallerwebi.dominio.album.ResultadoApertura;
import com.tallerwebi.dominio.excepcion.PaquetesInsuficientesException;
import java.util.List;
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

  private static final String ATRIBUTO_USUARIO = "USUARIO";

  @Autowired
  public ControladorInventario(PaqueteServicio paqueteServicio) {
    this.paqueteServicio = paqueteServicio;
  }

  @RequestMapping(path = "/inventario", method = RequestMethod.GET)
  public ModelAndView irAlInventario(HttpSession session) {
    ModelAndView mav = new ModelAndView("inventario");
    Usuario usuario = (Usuario) session.getAttribute(ATRIBUTO_USUARIO);

    if (usuario != null) {
      List<RelacionFiguritaUsuario> figuritas = paqueteServicio.obtenerFiguritasDelInventario(
        usuario.getId()
      );
      mav.addObject("figuritas", figuritas);
    }

    return mav;
  }

  @RequestMapping(path = "/abrir-paquete", method = RequestMethod.GET)
  public ModelAndView abrirUnPaquete(
    @RequestParam(value = "premium", required = false, defaultValue = "false") boolean esPremium,
    HttpSession session,
    RedirectAttributes ra
  ) {
    Usuario usuario = (Usuario) session.getAttribute(ATRIBUTO_USUARIO);

    try {
      ResultadoApertura resultado = paqueteServicio.abrirPaquete(usuario.getId(), esPremium);
      session.setAttribute(ATRIBUTO_USUARIO, resultado.getUsuarioActualizado());
      ra.addFlashAttribute("figuritasNuevas", resultado.getFiguritasNuevas());
      ra.addFlashAttribute("paqueteAbierto", true);
    } catch (PaquetesInsuficientesException e) {
      ra.addFlashAttribute("error", e.getMessage());
    }

    return new ModelAndView("redirect:/inventario");
  }

  @RequestMapping(path = "/pegar-figurita", method = RequestMethod.GET)
  public ModelAndView pegarFigurita(
    @RequestParam("id") Long idFigurita,
    HttpSession session,
    RedirectAttributes ra
  ) {
    Usuario usuario = (Usuario) session.getAttribute(ATRIBUTO_USUARIO);

    try {
      paqueteServicio.pegarFigurita(usuario.getId(), idFigurita);
      ra.addFlashAttribute("mensajeExito", "¡Figurita pegada con éxito!");
    } catch (Exception e) {
      ra.addFlashAttribute("error", "No se pudo pegar la figurita.");
    }

    return new ModelAndView("redirect:/inventario");
  }
}
