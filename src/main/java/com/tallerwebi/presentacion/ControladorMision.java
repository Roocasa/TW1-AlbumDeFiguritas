package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioPerfil;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.mision.MisionDefinicion;
import com.tallerwebi.dominio.mision.MisionEstadoDTO;
import com.tallerwebi.dominio.mision.ServicioMision;
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
public class ControladorMision {

  private static final String ATRIBUTO_USUARIO = "USUARIO";
  private static final String REDIRECT_LOGIN = "redirect:/login";
  private static final String REDIRECT_MISIONES = "redirect:/misiones";

  private final ServicioMision servicioMision;
  private final ServicioPerfil servicioPerfil;

  @Autowired
  public ControladorMision(ServicioMision servicioMision, ServicioPerfil servicioPerfil) {
    this.servicioMision = servicioMision;
    this.servicioPerfil = servicioPerfil;
  }

  @RequestMapping(path = "/misiones", method = RequestMethod.GET)
  public ModelAndView irAMisiones(HttpSession session) {
    Usuario usuario = (Usuario) session.getAttribute(ATRIBUTO_USUARIO);

    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    actualizarUsuarioEnSesion(session, usuario.getId());

    ModelAndView mav = new ModelAndView("misiones");
    mav.addObject("misiones", servicioMision.obtenerMisiones(usuario.getId()));
    return mav;
  }

  @RequestMapping(path = "/logros", method = RequestMethod.GET)
  public ModelAndView irALogros(HttpSession session) {
    Usuario usuario = (Usuario) session.getAttribute(ATRIBUTO_USUARIO);

    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    actualizarUsuarioEnSesion(session, usuario.getId());

    List<MisionEstadoDTO> logrosCompletados = new ArrayList<>();
    for (MisionEstadoDTO estado : servicioMision.obtenerMisiones(usuario.getId())) {
      if (estado.isCompletada()) {
        logrosCompletados.add(estado);
      }
    }

    ModelAndView mav = new ModelAndView("logros");
    mav.addObject("logros", logrosCompletados);
    return mav;
  }

  @RequestMapping(path = "/misiones/canjear", method = RequestMethod.POST)
  public ModelAndView canjearMision(
    @RequestParam("codigo") String codigoMision,
    HttpSession session,
    RedirectAttributes redirectAttributes
  ) {
    Usuario usuario = (Usuario) session.getAttribute(ATRIBUTO_USUARIO);

    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    try {
      MisionDefinicion mision = servicioMision.canjearMision(usuario.getId(), codigoMision);
      actualizarUsuarioEnSesion(session, usuario.getId());
      redirectAttributes.addFlashAttribute(
        "mensajeExito",
        "Canjeaste " + mision.getTitulo() + " y ganaste " + mision.getTextoRecompensa() + "."
      );
    } catch (IllegalArgumentException e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
    }

    return new ModelAndView(REDIRECT_MISIONES);
  }

  private void actualizarUsuarioEnSesion(HttpSession session, Long idUsuario) {
    Usuario usuarioActualizado = servicioPerfil.buscarUsuarioPorId(idUsuario);
    session.setAttribute(ATRIBUTO_USUARIO, usuarioActualizado);
  }
}
