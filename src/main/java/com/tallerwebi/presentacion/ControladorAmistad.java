package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.amistad.ServicioAmistad;
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
public class ControladorAmistad {

  private static final String ATRIBUTO_USUARIO = "USUARIO";
  private static final String REDIRECT_LOGIN = "redirect:/login";
  private static final String REDIRECT_AMIGOS = "redirect:/amigos";
  private static final String ATRIBUTO_MENSAJE_EXITO = "mensajeExito";
  private static final String ATRIBUTO_ERROR = "error";
  private static final String PARAM_PAGINA_RECIBIDAS = "paginaSolicitudesRecibidas";
  private static final String PARAM_PAGINA_AMIGOS = "paginaAmigos";
  private static final String PARAM_PAGINA_USUARIOS = "paginaUsuarios";
  private static final String PARAM_PAGINA_ENVIADAS = "paginaSolicitudesEnviadas";
  private static final int ELEMENTOS_POR_PAGINA = 3;

  private final ServicioAmistad servicioAmistad;

  @Autowired
  public ControladorAmistad(ServicioAmistad servicioAmistad) {
    this.servicioAmistad = servicioAmistad;
  }

  @RequestMapping(path = "/amigos", method = RequestMethod.GET)
  public ModelAndView verAmigos(
    @RequestParam(
      value = PARAM_PAGINA_RECIBIDAS,
      defaultValue = "1"
    ) Integer paginaSolicitudesRecibidas,
    @RequestParam(value = PARAM_PAGINA_AMIGOS, defaultValue = "1") Integer paginaAmigos,
    @RequestParam(value = PARAM_PAGINA_USUARIOS, defaultValue = "1") Integer paginaUsuarios,
    @RequestParam(
      value = PARAM_PAGINA_ENVIADAS,
      defaultValue = "1"
    ) Integer paginaSolicitudesEnviadas,
    HttpSession session
  ) {
    Usuario usuario = obtenerUsuarioEnSesion(session);

    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    Long idUsuario = usuario.getId();
    List<?> solicitudesRecibidas = servicioAmistad.obtenerSolicitudesRecibidas(idUsuario);
    List<?> solicitudesEnviadas = servicioAmistad.obtenerSolicitudesEnviadas(idUsuario);
    List<Usuario> amigos = servicioAmistad.obtenerAmigos(idUsuario);
    List<Usuario> usuariosParaAgregar = servicioAmistad.obtenerUsuariosParaAgregar(idUsuario);

    int totalPaginasSolicitudesRecibidas = obtenerTotalPaginas(solicitudesRecibidas.size());
    int totalPaginasSolicitudesEnviadas = obtenerTotalPaginas(solicitudesEnviadas.size());
    int totalPaginasAmigos = obtenerTotalPaginas(amigos.size());
    int totalPaginasUsuarios = obtenerTotalPaginas(usuariosParaAgregar.size());

    int paginaActualSolicitudesRecibidas = normalizarPagina(
      paginaSolicitudesRecibidas,
      totalPaginasSolicitudesRecibidas
    );
    int paginaActualSolicitudesEnviadas = normalizarPagina(
      paginaSolicitudesEnviadas,
      totalPaginasSolicitudesEnviadas
    );
    int paginaActualAmigos = normalizarPagina(paginaAmigos, totalPaginasAmigos);
    int paginaActualUsuarios = normalizarPagina(paginaUsuarios, totalPaginasUsuarios);

    ModelAndView mav = new ModelAndView("amigos");
    mav.addObject(
      "solicitudesRecibidas",
      paginarLista(solicitudesRecibidas, paginaActualSolicitudesRecibidas)
    );
    mav.addObject(
      "solicitudesEnviadas",
      paginarLista(solicitudesEnviadas, paginaActualSolicitudesEnviadas)
    );
    mav.addObject("amigos", paginarLista(amigos, paginaActualAmigos));
    mav.addObject("usuariosParaAgregar", paginarLista(usuariosParaAgregar, paginaActualUsuarios));
    mav.addObject(PARAM_PAGINA_RECIBIDAS, paginaActualSolicitudesRecibidas);
    mav.addObject("totalPaginasSolicitudesRecibidas", totalPaginasSolicitudesRecibidas);
    mav.addObject(PARAM_PAGINA_ENVIADAS, paginaActualSolicitudesEnviadas);
    mav.addObject("totalPaginasSolicitudesEnviadas", totalPaginasSolicitudesEnviadas);
    mav.addObject(PARAM_PAGINA_AMIGOS, paginaActualAmigos);
    mav.addObject("totalPaginasAmigos", totalPaginasAmigos);
    mav.addObject(PARAM_PAGINA_USUARIOS, paginaActualUsuarios);
    mav.addObject("totalPaginasUsuarios", totalPaginasUsuarios);
    return mav;
  }

  @RequestMapping(path = "/amigos/solicitar", method = RequestMethod.POST)
  public ModelAndView enviarSolicitud(
    @RequestParam("idUsuario") Long idUsuario,
    @RequestParam(value = "origen", required = false, defaultValue = "amigos") String origen,
    @RequestParam(
      value = PARAM_PAGINA_RECIBIDAS,
      defaultValue = "1"
    ) Integer paginaSolicitudesRecibidas,
    @RequestParam(value = PARAM_PAGINA_AMIGOS, defaultValue = "1") Integer paginaAmigos,
    @RequestParam(value = PARAM_PAGINA_USUARIOS, defaultValue = "1") Integer paginaUsuarios,
    @RequestParam(
      value = PARAM_PAGINA_ENVIADAS,
      defaultValue = "1"
    ) Integer paginaSolicitudesEnviadas,
    HttpSession session,
    RedirectAttributes redirectAttributes
  ) {
    Usuario usuario = obtenerUsuarioEnSesion(session);

    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    try {
      servicioAmistad.enviarSolicitud(usuario.getId(), idUsuario);
      redirectAttributes.addFlashAttribute(ATRIBUTO_MENSAJE_EXITO, "Solicitud enviada.");
    } catch (IllegalArgumentException e) {
      redirectAttributes.addFlashAttribute(ATRIBUTO_ERROR, e.getMessage());
    }

    agregarParametrosPaginacion(
      redirectAttributes,
      paginaSolicitudesRecibidas,
      paginaAmigos,
      paginaUsuarios,
      paginaSolicitudesEnviadas
    );
    return new ModelAndView(obtenerRedirectOrigen(origen));
  }

  @RequestMapping(path = "/amigos/aceptar", method = RequestMethod.POST)
  public ModelAndView aceptarSolicitud(
    @RequestParam("idSolicitud") Long idSolicitud,
    @RequestParam(
      value = PARAM_PAGINA_RECIBIDAS,
      defaultValue = "1"
    ) Integer paginaSolicitudesRecibidas,
    @RequestParam(value = PARAM_PAGINA_AMIGOS, defaultValue = "1") Integer paginaAmigos,
    @RequestParam(value = PARAM_PAGINA_USUARIOS, defaultValue = "1") Integer paginaUsuarios,
    @RequestParam(
      value = PARAM_PAGINA_ENVIADAS,
      defaultValue = "1"
    ) Integer paginaSolicitudesEnviadas,
    HttpSession session,
    RedirectAttributes redirectAttributes
  ) {
    return responderSolicitud(
      idSolicitud,
      true,
      paginaSolicitudesRecibidas,
      paginaAmigos,
      paginaUsuarios,
      paginaSolicitudesEnviadas,
      session,
      redirectAttributes
    );
  }

  @RequestMapping(path = "/amigos/rechazar", method = RequestMethod.POST)
  public ModelAndView rechazarSolicitud(
    @RequestParam("idSolicitud") Long idSolicitud,
    @RequestParam(
      value = PARAM_PAGINA_RECIBIDAS,
      defaultValue = "1"
    ) Integer paginaSolicitudesRecibidas,
    @RequestParam(value = PARAM_PAGINA_AMIGOS, defaultValue = "1") Integer paginaAmigos,
    @RequestParam(value = PARAM_PAGINA_USUARIOS, defaultValue = "1") Integer paginaUsuarios,
    @RequestParam(
      value = PARAM_PAGINA_ENVIADAS,
      defaultValue = "1"
    ) Integer paginaSolicitudesEnviadas,
    HttpSession session,
    RedirectAttributes redirectAttributes
  ) {
    return responderSolicitud(
      idSolicitud,
      false,
      paginaSolicitudesRecibidas,
      paginaAmigos,
      paginaUsuarios,
      paginaSolicitudesEnviadas,
      session,
      redirectAttributes
    );
  }

  private ModelAndView responderSolicitud(
    Long idSolicitud,
    boolean aceptar,
    Integer paginaSolicitudesRecibidas,
    Integer paginaAmigos,
    Integer paginaUsuarios,
    Integer paginaSolicitudesEnviadas,
    HttpSession session,
    RedirectAttributes redirectAttributes
  ) {
    Usuario usuario = obtenerUsuarioEnSesion(session);

    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    try {
      if (aceptar) {
        servicioAmistad.aceptarSolicitud(usuario.getId(), idSolicitud);
        redirectAttributes.addFlashAttribute(ATRIBUTO_MENSAJE_EXITO, "Solicitud aceptada.");
      } else {
        servicioAmistad.rechazarSolicitud(usuario.getId(), idSolicitud);
        redirectAttributes.addFlashAttribute(ATRIBUTO_MENSAJE_EXITO, "Solicitud rechazada.");
      }
    } catch (IllegalArgumentException e) {
      redirectAttributes.addFlashAttribute(ATRIBUTO_ERROR, e.getMessage());
    }

    agregarParametrosPaginacion(
      redirectAttributes,
      paginaSolicitudesRecibidas,
      paginaAmigos,
      paginaUsuarios,
      paginaSolicitudesEnviadas
    );
    return new ModelAndView(REDIRECT_AMIGOS);
  }

  private String obtenerRedirectOrigen(String origen) {
    if ("foro".equals(origen)) {
      return "redirect:/foro";
    }
    return REDIRECT_AMIGOS;
  }

  private Usuario obtenerUsuarioEnSesion(HttpSession session) {
    return (Usuario) session.getAttribute(ATRIBUTO_USUARIO);
  }

  private int obtenerTotalPaginas(int cantidadElementos) {
    return Math.max(1, (int) Math.ceil((double) cantidadElementos / ELEMENTOS_POR_PAGINA));
  }

  private int normalizarPagina(Integer pagina, int totalPaginas) {
    if (pagina == null || pagina < 1) {
      return 1;
    }

    return Math.min(pagina, totalPaginas);
  }

  @SuppressWarnings("unchecked")
  private <T> List<T> paginarLista(List<?> elementos, int pagina) {
    int indiceInicio = (pagina - 1) * ELEMENTOS_POR_PAGINA;
    int indiceFin = Math.min(indiceInicio + ELEMENTOS_POR_PAGINA, elementos.size());

    return new ArrayList<>((List<T>) elementos.subList(indiceInicio, indiceFin));
  }

  private void agregarParametrosPaginacion(
    RedirectAttributes redirectAttributes,
    Integer paginaSolicitudesRecibidas,
    Integer paginaAmigos,
    Integer paginaUsuarios,
    Integer paginaSolicitudesEnviadas
  ) {
    redirectAttributes.addAttribute(
      PARAM_PAGINA_RECIBIDAS,
      normalizarPagina(paginaSolicitudesRecibidas, Integer.MAX_VALUE)
    );
    redirectAttributes.addAttribute(
      PARAM_PAGINA_AMIGOS,
      normalizarPagina(paginaAmigos, Integer.MAX_VALUE)
    );
    redirectAttributes.addAttribute(
      PARAM_PAGINA_USUARIOS,
      normalizarPagina(paginaUsuarios, Integer.MAX_VALUE)
    );
    redirectAttributes.addAttribute(
      PARAM_PAGINA_ENVIADAS,
      normalizarPagina(paginaSolicitudesEnviadas, Integer.MAX_VALUE)
    );
  }
}
