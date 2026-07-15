package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioPerfil;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.album.InventarioItemDTO;
import com.tallerwebi.dominio.album.OfertaIntercambioDTO;
import com.tallerwebi.dominio.album.PropuestaIntercambio;
import com.tallerwebi.dominio.album.ServicioIntercambio;
import com.tallerwebi.dominio.excepcion.IntercambioFiguritasException;
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
public class ControladorIntercambio {

  private static final String ATRIBUTO_USUARIO = "USUARIO";
  private static final String REDIRECT_LOGIN = "redirect:/login";
  private static final String REDIRECT_INTERCAMBIOS = "redirect:/intercambios";
  private static final String ATRIBUTO_MENSAJE_EXITO = "mensajeExito";
  private static final String ATRIBUTO_ERROR = "error";
  private static final int FIGURITAS_POR_PAGINA = 3;

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
  public ModelAndView verIntercambios(
    @RequestParam(
      value = "paginaPropuestasRecibidas",
      defaultValue = "1"
    ) Integer paginaPropuestasRecibidas,
    @RequestParam(
      value = "paginaPropuestasEnviadas",
      defaultValue = "1"
    ) Integer paginaPropuestasEnviadas,
    @RequestParam(value = "paginaMisFiguritas", defaultValue = "1") Integer paginaMisFiguritas,
    @RequestParam(value = "paginaOfertas", defaultValue = "1") Integer paginaOfertas,
    HttpSession session
  ) {
    Usuario usuario = (Usuario) session.getAttribute(ATRIBUTO_USUARIO);

    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    if (servicioPerfil != null) {
      usuario = servicioPerfil.buscarUsuarioPorId(usuario.getId());
      session.setAttribute(ATRIBUTO_USUARIO, usuario);
    }

    ModelAndView mav = new ModelAndView("intercambios");
    List<OfertaIntercambioDTO> ofertas = servicioIntercambio.obtenerOfertasDeOtrosUsuarios(
      usuario.getId()
    );
    List<InventarioItemDTO> misFiguritas =
      servicioIntercambio.obtenerFiguritasPropiasParaIntercambiar(usuario.getId());
    List<PropuestaIntercambio> propuestasRecibidas = servicioIntercambio.obtenerPropuestasRecibidas(
      usuario.getId()
    );
    List<PropuestaIntercambio> propuestasEnviadas = servicioIntercambio.obtenerPropuestasEnviadas(
      usuario.getId()
    );

    agregarPropuestasPaginadas(
      mav,
      propuestasRecibidas,
      propuestasEnviadas,
      paginaPropuestasRecibidas,
      paginaPropuestasEnviadas
    );
    agregarFiguritasPaginadas(mav, misFiguritas, ofertas, paginaMisFiguritas, paginaOfertas);
    return mav;
  }

  private void agregarPropuestasPaginadas(
    ModelAndView mav,
    List<PropuestaIntercambio> propuestasRecibidas,
    List<PropuestaIntercambio> propuestasEnviadas,
    Integer paginaPropuestasRecibidas,
    Integer paginaPropuestasEnviadas
  ) {
    int totalPaginasPropuestasRecibidas = obtenerTotalPaginas(propuestasRecibidas.size());
    int paginaActualPropuestasRecibidas = normalizarPagina(
      paginaPropuestasRecibidas,
      totalPaginasPropuestasRecibidas
    );
    int totalPaginasPropuestasEnviadas = obtenerTotalPaginas(propuestasEnviadas.size());
    int paginaActualPropuestasEnviadas = normalizarPagina(
      paginaPropuestasEnviadas,
      totalPaginasPropuestasEnviadas
    );
    mav.addObject(
      "propuestasRecibidas",
      paginarLista(propuestasRecibidas, paginaActualPropuestasRecibidas)
    );
    mav.addObject("paginaPropuestasRecibidas", paginaActualPropuestasRecibidas);
    mav.addObject("totalPaginasPropuestasRecibidas", totalPaginasPropuestasRecibidas);
    mav.addObject(
      "propuestasEnviadas",
      paginarLista(propuestasEnviadas, paginaActualPropuestasEnviadas)
    );
    mav.addObject("paginaPropuestasEnviadas", paginaActualPropuestasEnviadas);
    mav.addObject("totalPaginasPropuestasEnviadas", totalPaginasPropuestasEnviadas);
  }

  private void agregarFiguritasPaginadas(
    ModelAndView mav,
    List<InventarioItemDTO> misFiguritas,
    List<OfertaIntercambioDTO> ofertas,
    Integer paginaMisFiguritas,
    Integer paginaOfertas
  ) {
    int totalPaginasMisFiguritas = obtenerTotalPaginas(misFiguritas.size());
    int paginaActualMisFiguritas = normalizarPagina(paginaMisFiguritas, totalPaginasMisFiguritas);
    int totalPaginasOfertas = obtenerTotalPaginasOfertas(ofertas);
    int paginaActualOfertas = normalizarPagina(paginaOfertas, totalPaginasOfertas);

    mav.addObject("misFiguritas", paginarLista(misFiguritas, paginaActualMisFiguritas));
    mav.addObject("paginaMisFiguritas", paginaActualMisFiguritas);
    mav.addObject("totalPaginasMisFiguritas", totalPaginasMisFiguritas);
    mav.addObject("ofertas", paginarOfertas(ofertas, paginaActualOfertas));
    mav.addObject("paginaOfertas", paginaActualOfertas);
    mav.addObject("totalPaginasOfertas", totalPaginasOfertas);
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
      redirectAttributes.addFlashAttribute(ATRIBUTO_MENSAJE_EXITO, "Propuesta enviada con exito.");
    } catch (IntercambioFiguritasException e) {
      redirectAttributes.addFlashAttribute(ATRIBUTO_ERROR, e.getMessage());
    }

    return new ModelAndView(REDIRECT_INTERCAMBIOS);
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
      redirectAttributes.addFlashAttribute(
        ATRIBUTO_MENSAJE_EXITO,
        "Intercambio aceptado y realizado."
      );
    } catch (IntercambioFiguritasException e) {
      redirectAttributes.addFlashAttribute(ATRIBUTO_ERROR, e.getMessage());
    }

    return new ModelAndView(REDIRECT_INTERCAMBIOS);
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
      redirectAttributes.addFlashAttribute(ATRIBUTO_MENSAJE_EXITO, "Propuesta rechazada.");
    } catch (IntercambioFiguritasException e) {
      redirectAttributes.addFlashAttribute(ATRIBUTO_ERROR, e.getMessage());
    }

    return new ModelAndView(REDIRECT_INTERCAMBIOS);
  }

  @RequestMapping(path = "/intercambios/propuestas/cancelar", method = RequestMethod.POST)
  public ModelAndView cancelarPropuesta(
    @RequestParam("idPropuesta") Long idPropuesta,
    HttpSession session,
    RedirectAttributes redirectAttributes
  ) {
    Usuario usuario = (Usuario) session.getAttribute(ATRIBUTO_USUARIO);

    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    try {
      servicioIntercambio.cancelarPropuesta(usuario.getId(), idPropuesta);
      redirectAttributes.addFlashAttribute(ATRIBUTO_MENSAJE_EXITO, "Propuesta cancelada.");
    } catch (IntercambioFiguritasException e) {
      redirectAttributes.addFlashAttribute(ATRIBUTO_ERROR, e.getMessage());
    }

    return new ModelAndView(REDIRECT_INTERCAMBIOS);
  }

  private int obtenerTotalPaginasOfertas(List<OfertaIntercambioDTO> ofertas) {
    int cantidadFiguritas = 0;
    for (OfertaIntercambioDTO oferta : ofertas) {
      cantidadFiguritas += oferta.getFiguritasRepetidas().size();
    }

    return obtenerTotalPaginas(cantidadFiguritas);
  }

  private int obtenerTotalPaginas(int cantidadFiguritas) {
    return Math.max(1, (int) Math.ceil((double) cantidadFiguritas / FIGURITAS_POR_PAGINA));
  }

  private int normalizarPagina(Integer pagina, int totalPaginas) {
    if (pagina == null || pagina < 1) {
      return 1;
    }

    return Math.min(pagina, totalPaginas);
  }

  private <T> List<T> paginarLista(List<T> figuritas, int pagina) {
    int indiceInicio = (pagina - 1) * FIGURITAS_POR_PAGINA;
    int indiceFin = Math.min(indiceInicio + FIGURITAS_POR_PAGINA, figuritas.size());

    return new ArrayList<>(figuritas.subList(indiceInicio, indiceFin));
  }

  @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
  private List<OfertaIntercambioDTO> paginarOfertas(
    List<OfertaIntercambioDTO> ofertas,
    int pagina
  ) {
    int indiceInicio = (pagina - 1) * FIGURITAS_POR_PAGINA;
    int indiceFin = indiceInicio + FIGURITAS_POR_PAGINA;
    int indiceActual = 0;
    List<OfertaIntercambioDTO> ofertasPaginadas = new ArrayList<>();

    for (OfertaIntercambioDTO oferta : ofertas) {
      List<InventarioItemDTO> figuritasPaginadas = new ArrayList<>();

      for (InventarioItemDTO figurita : oferta.getFiguritasRepetidas()) {
        if (indiceActual >= indiceInicio && indiceActual < indiceFin) {
          figuritasPaginadas.add(figurita);
        }
        indiceActual++;
      }

      if (!figuritasPaginadas.isEmpty()) {
        ofertasPaginadas.add(new OfertaIntercambioDTO(oferta.getUsuario(), figuritasPaginadas));
      }
    }

    return ofertasPaginadas;
  }

  private void actualizarUsuarioEnSesion(HttpSession session, Long idUsuario) {
    if (servicioPerfil == null) {
      return;
    }

    session.setAttribute(ATRIBUTO_USUARIO, servicioPerfil.buscarUsuarioPorId(idUsuario));
  }
}
