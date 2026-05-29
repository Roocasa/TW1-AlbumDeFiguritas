package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioPerfil;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.album.ServicioIntercambio;
import com.tallerwebi.dominio.excepcion.IntercambioFiguritasException;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ControladorIntercambio {

  private static final String ATRIBUTO_USUARIO = "USUARIO";
  private static final String REDIRECT_LOGIN = "redirect:/login";

  private final ServicioIntercambio servicioIntercambio;
  private final ServicioPerfil servicioPerfil;

  @Autowired
  public ControladorIntercambio(
    ServicioIntercambio servicioIntercambio,
    ServicioPerfil servicioPerfil
  ) {
    this.servicioIntercambio = servicioIntercambio;
    this.servicioPerfil = servicioPerfil;
  }

  @RequestMapping(path = "/intercambios", method = RequestMethod.GET)
  public ModelAndView verIntercambios(HttpSession session) {
    Usuario usuario = (Usuario) session.getAttribute(ATRIBUTO_USUARIO);

    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    if (servicioPerfil != null) {
      usuario = servicioPerfil.buscarUsuarioPorId(usuario.getId());
      session.setAttribute(ATRIBUTO_USUARIO, usuario);
    }

    ModelAndView mav = new ModelAndView("intercambios");
    mav.addObject(
      "misFiguritas",
      servicioIntercambio.obtenerFiguritasPropiasParaIntercambiar(usuario.getId())
    );
    mav.addObject("ofertas", servicioIntercambio.obtenerOfertasDeOtrosUsuarios(usuario.getId()));
    mav.addObject(
      "propuestasRecibidas",
      servicioIntercambio.obtenerPropuestasRecibidas(usuario.getId())
    );
    mav.addObject(
      "propuestasEnviadas",
      servicioIntercambio.obtenerPropuestasEnviadas(usuario.getId())
    );
    return mav;
  }

  @RequestMapping(path = "/intercambiar", method = RequestMethod.POST)
  public ModelAndView enviarPropuesta(
    @RequestParam("miFiguritaId") Long miFiguritaId,
    @RequestParam("usuarioDestinoId") Long usuarioDestinoId,
    @RequestParam("figuritaDestinoId") Long figuritaDestinoId,
    HttpSession session,
    RedirectAttributes redirectAttributes
  ) {
    Usuario usuario = (Usuario) session.getAttribute(ATRIBUTO_USUARIO);

    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    try {
      servicioIntercambio.enviarPropuesta(
        usuario.getId(),
        miFiguritaId,
        usuarioDestinoId,
        figuritaDestinoId
      );
      actualizarUsuarioEnSesion(session, usuario.getId());
      redirectAttributes.addFlashAttribute("mensajeExito", "Propuesta enviada con exito.");
    } catch (IntercambioFiguritasException e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
    }

    return new ModelAndView("redirect:/intercambios");
  }

  @RequestMapping(path = "/intercambios/propuestas/aceptar", method = RequestMethod.POST)
  public ModelAndView aceptarPropuesta(
    @RequestParam("idPropuesta") Long idPropuesta,
    HttpSession session,
    RedirectAttributes redirectAttributes
  ) {
    Usuario usuario = (Usuario) session.getAttribute(ATRIBUTO_USUARIO);

    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    try {
      servicioIntercambio.aceptarPropuesta(usuario.getId(), idPropuesta);
      actualizarUsuarioEnSesion(session, usuario.getId());
      redirectAttributes.addFlashAttribute("mensajeExito", "Intercambio aceptado y realizado.");
    } catch (IntercambioFiguritasException e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
    }

    return new ModelAndView("redirect:/intercambios");
  }

  @RequestMapping(path = "/intercambios/propuestas/rechazar", method = RequestMethod.POST)
  public ModelAndView rechazarPropuesta(
    @RequestParam("idPropuesta") Long idPropuesta,
    HttpSession session,
    RedirectAttributes redirectAttributes
  ) {
    Usuario usuario = (Usuario) session.getAttribute(ATRIBUTO_USUARIO);

    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    try {
      servicioIntercambio.rechazarPropuesta(usuario.getId(), idPropuesta);
      redirectAttributes.addFlashAttribute("mensajeExito", "Propuesta rechazada.");
    } catch (IntercambioFiguritasException e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
    }

    return new ModelAndView("redirect:/intercambios");
  }

  private void actualizarUsuarioEnSesion(HttpSession session, Long idUsuario) {
    if (servicioPerfil == null) {
      return;
    }

    session.setAttribute(ATRIBUTO_USUARIO, servicioPerfil.buscarUsuarioPorId(idUsuario));
  }
}
