package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioPerfil;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.album.InventarioItemDTO;
import com.tallerwebi.dominio.album.PaqueteServicio;
import com.tallerwebi.dominio.album.ResultadoApertura;
import com.tallerwebi.dominio.excepcion.PaquetesInsuficientesException;
import java.util.ArrayList;
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

  private static final String ATRIBUTO_USUARIO = "USUARIO";

  private final PaqueteServicio paqueteServicio;
  private final ServicioPerfil servicioPerfil;

  @Autowired
  public ControladorInventario(PaqueteServicio paqueteServicio, ServicioPerfil servicioPerfil) {
    this.paqueteServicio = paqueteServicio;
    this.servicioPerfil = servicioPerfil;
  }

  public ControladorInventario(PaqueteServicio paqueteServicio) {
    this(paqueteServicio, null);
  }

  @RequestMapping(path = "/inventario", method = RequestMethod.GET)
  public ModelAndView irAlInventario(
    HttpSession session,
    @RequestParam(value = "soloRepetidas", defaultValue = "false") boolean soloRepetidas
  ) {
    Usuario usuario = (Usuario) session.getAttribute(ATRIBUTO_USUARIO);

    if (usuario == null) {
      return new ModelAndView("redirect:/login");
    }

    if (servicioPerfil != null) {
      usuario = servicioPerfil.otorgarPaquetesDiariosSiCorresponde(usuario.getId());
      session.setAttribute(ATRIBUTO_USUARIO, usuario);
    }

    List<InventarioItemDTO> figuritas = paqueteServicio.obtenerFiguritasDelInventario(
      usuario.getId()
    );

    if (soloRepetidas) {
      figuritas = filtrarRepetidas(figuritas);
    }

    ModelAndView mav = new ModelAndView("inventario");
    mav.addObject("figuritas", figuritas);
    mav.addObject("soloRepetidas", soloRepetidas);
    return mav;
  }

  @RequestMapping(path = "/abrir-paquete", method = RequestMethod.GET)
  public ModelAndView abrirUnPaquete(HttpSession session, RedirectAttributes ra) {
    Usuario usuario = (Usuario) session.getAttribute(ATRIBUTO_USUARIO);

    if (usuario == null) {
      return new ModelAndView("redirect:/login");
    }

    if (servicioPerfil != null) {
      usuario = servicioPerfil.otorgarPaquetesDiariosSiCorresponde(usuario.getId());
      session.setAttribute(ATRIBUTO_USUARIO, usuario);
    }

    try {
      ResultadoApertura resultado = paqueteServicio.abrirPaquete(usuario.getId());
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

    if (usuario == null) {
      return new ModelAndView("redirect:/login");
    }

    try {
      paqueteServicio.pegarFigurita(usuario.getId(), idFigurita);
      ra.addFlashAttribute("mensajeExito", "Figurita pegada con exito.");
    } catch (Exception e) {
      ra.addFlashAttribute("error", "No se pudo pegar la figurita.");
    }

    return new ModelAndView("redirect:/inventario");
  }

  private List<InventarioItemDTO> filtrarRepetidas(List<InventarioItemDTO> figuritas) {
    List<InventarioItemDTO> repetidas = new ArrayList<>();

    for (InventarioItemDTO item : figuritas) {
      if (item.isRepetida()) {
        repetidas.add(item);
      }
    }

    return repetidas;
  }
}
