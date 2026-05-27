package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioPerfil;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.album.Figurita;
import com.tallerwebi.dominio.album.InventarioItemDTO;
import com.tallerwebi.dominio.album.PaqueteServicio;
import com.tallerwebi.dominio.album.ResultadoApertura;
import com.tallerwebi.dominio.album.ServicioAlbum;
import com.tallerwebi.dominio.excepcion.CanjeFiguritasException;
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
  private static final String REDIRECT_LOGIN = "redirect:/login";
  private static final String FLASH_ERROR = "error";

  private final PaqueteServicio paqueteServicio;
  private final ServicioPerfil servicioPerfil;
  private final ServicioAlbum servicioAlbum;

  @Autowired
  public ControladorInventario(
    PaqueteServicio paqueteServicio,
    ServicioPerfil servicioPerfil,
    ServicioAlbum servicioAlbum
  ) {
    this.paqueteServicio = paqueteServicio;
    this.servicioPerfil = servicioPerfil;
    this.servicioAlbum = servicioAlbum;
  }

  public ControladorInventario(PaqueteServicio paqueteServicio, ServicioPerfil servicioPerfil) {
    this(paqueteServicio, servicioPerfil, null);
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
      return new ModelAndView(REDIRECT_LOGIN);
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
    mav.addObject("sinSobres", usuario.getPaquetesDisponibles() <= 0);
    mav.addObject(
      "repetidasDisponibles",
      paqueteServicio.obtenerCantidadTotalRepetidas(usuario.getId())
    );
    mav.addObject("canjePaqueteCosto", paqueteServicio.obtenerCostoCanjePaquete());
    mav.addObject("canjeEscudoCosto", paqueteServicio.obtenerCostoCanjeEscudo());
    return mav;
  }

  @RequestMapping(path = "/abrir-paquete", method = RequestMethod.GET)
  public ModelAndView abrirUnPaquete(HttpSession session, RedirectAttributes ra) {
    Usuario usuario = (Usuario) session.getAttribute(ATRIBUTO_USUARIO);

    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
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
      ra.addFlashAttribute(FLASH_ERROR, e.getMessage());
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
      return new ModelAndView(REDIRECT_LOGIN);
    }

    try {
      paqueteServicio.pegarFigurita(usuario.getId(), idFigurita);
      ra.addFlashAttribute("mensajeExito", "Figurita pegada con exito.");
      agregarMensajeAlbumCompletadoSiCorresponde(usuario.getId(), ra);
    } catch (Exception e) {
      ra.addFlashAttribute(FLASH_ERROR, "No se pudo pegar la figurita.");
    }

    return new ModelAndView("redirect:/inventario");
  }

  @RequestMapping(path = "/recompensa-anuncio", method = RequestMethod.GET)
  public ModelAndView otorgarRecompensaPorAnuncio(HttpSession session, RedirectAttributes ra) {
    Usuario usuario = (Usuario) session.getAttribute(ATRIBUTO_USUARIO);

    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    int sobresAntes = usuario.getPaquetesDisponibles();
    Usuario usuarioActualizado = servicioPerfil.otorgarSobrePorAnuncio(usuario.getId());

    if (usuarioActualizado != null) {
      session.setAttribute(ATRIBUTO_USUARIO, usuarioActualizado);
    }

    if (
      sobresAntes <= 0 &&
      usuarioActualizado != null &&
      usuarioActualizado.getPaquetesDisponibles() > sobresAntes
    ) {
      ra.addFlashAttribute("mensajeSobre", "Cerraste el anuncio y te dimos 1 sobre comun.");
    } else {
      ra.addFlashAttribute("mensajeSobre", "Todavia tenes sobres disponibles.");
    }

    return new ModelAndView("redirect:/inventario");
  }

  @RequestMapping(path = "/canjear-repetidas/paquete", method = RequestMethod.GET)
  public ModelAndView canjearRepetidasPorPaquete(HttpSession session, RedirectAttributes ra) {
    Usuario usuario = (Usuario) session.getAttribute(ATRIBUTO_USUARIO);

    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    try {
      paqueteServicio.canjearRepetidasPorPaquete(usuario.getId());
      actualizarUsuarioEnSesion(session, usuario.getId());
      ra.addFlashAttribute("canjePaqueteExitoso", true);
    } catch (CanjeFiguritasException e) {
      ra.addFlashAttribute(FLASH_ERROR, e.getMessage());
    }

    return new ModelAndView("redirect:/inventario?soloRepetidas=true");
  }

  @RequestMapping(path = "/canjear-repetidas/escudo", method = RequestMethod.GET)
  public ModelAndView canjearRepetidasPorEscudo(HttpSession session, RedirectAttributes ra) {
    Usuario usuario = (Usuario) session.getAttribute(ATRIBUTO_USUARIO);

    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    try {
      Figurita escudoGanado = paqueteServicio.canjearRepetidasPorEscudo(usuario.getId());
      actualizarUsuarioEnSesion(session, usuario.getId());
      ra.addFlashAttribute("escudoCanjeado", escudoGanado);
    } catch (CanjeFiguritasException e) {
      ra.addFlashAttribute(FLASH_ERROR, e.getMessage());
    }

    return new ModelAndView("redirect:/inventario?soloRepetidas=true");
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

  private void actualizarUsuarioEnSesion(HttpSession session, Long idUsuario) {
    if (servicioPerfil == null) {
      return;
    }

    Usuario usuarioActualizado = servicioPerfil.buscarUsuarioPorId(idUsuario);
    session.setAttribute(ATRIBUTO_USUARIO, usuarioActualizado);
  }

  private void agregarMensajeAlbumCompletadoSiCorresponde(
    Long idUsuario,
    RedirectAttributes redirectAttributes
  ) {
    if (servicioAlbum == null) {
      return;
    }

    if (servicioAlbum.obtenerAlbumActualizado(idUsuario).getFiguritasFaltantes() == 0) {
      redirectAttributes.addFlashAttribute("albumCompletado", true);
    }
  }
}
