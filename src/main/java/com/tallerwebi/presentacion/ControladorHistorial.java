package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.album.HistorialIntercambioDTO;
import com.tallerwebi.dominio.album.HistorialSobre;
import com.tallerwebi.dominio.album.PaqueteServicio;
import com.tallerwebi.dominio.album.ServicioIntercambio;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorHistorial {

  private static final String ATRIBUTO_USUARIO = "USUARIO";
  private static final String REDIRECT_LOGIN = "redirect:/login";
  private static final int FILAS_POR_PAGINA = 2;

  private final PaqueteServicio paqueteServicio;
  private final ServicioIntercambio servicioIntercambio;

  @Autowired
  public ControladorHistorial(
    PaqueteServicio paqueteServicio,
    ServicioIntercambio servicioIntercambio
  ) {
    this.paqueteServicio = paqueteServicio;
    this.servicioIntercambio = servicioIntercambio;
  }

  @RequestMapping(path = "/historial", method = RequestMethod.GET)
  public ModelAndView verHistorial(
    @RequestParam(value = "paginaSobres", defaultValue = "1") Integer paginaSobres,
    @RequestParam(value = "paginaIntercambios", defaultValue = "1") Integer paginaIntercambios,
    HttpSession session
  ) {
    Usuario usuario = (Usuario) session.getAttribute(ATRIBUTO_USUARIO);

    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    ModelAndView mav = new ModelAndView("historial");
    List<HistorialSobre> historialSobres = paqueteServicio.obtenerHistorialSobres(usuario.getId());
    List<HistorialIntercambioDTO> historialIntercambios =
      servicioIntercambio.obtenerHistorialIntercambios(usuario.getId());
    int totalPaginasSobres = obtenerTotalPaginas(historialSobres.size());
    int paginaActualSobres = normalizarPagina(paginaSobres, totalPaginasSobres);
    int totalPaginasIntercambios = obtenerTotalPaginas(historialIntercambios.size());
    int paginaActualIntercambios = normalizarPagina(paginaIntercambios, totalPaginasIntercambios);

    mav.addObject("historialSobres", paginarLista(historialSobres, paginaActualSobres));
    mav.addObject("paginaSobres", paginaActualSobres);
    mav.addObject("totalPaginasSobres", totalPaginasSobres);
    mav.addObject(
      "historialIntercambios",
      paginarLista(historialIntercambios, paginaActualIntercambios)
    );
    mav.addObject("paginaIntercambios", paginaActualIntercambios);
    mav.addObject("totalPaginasIntercambios", totalPaginasIntercambios);
    return mav;
  }

  private int obtenerTotalPaginas(int cantidadFilas) {
    return Math.max(1, (int) Math.ceil((double) cantidadFilas / FILAS_POR_PAGINA));
  }

  private int normalizarPagina(Integer pagina, int totalPaginas) {
    if (pagina == null || pagina < 1) {
      return 1;
    }

    return Math.min(pagina, totalPaginas);
  }

  private <T> List<T> paginarLista(List<T> items, int pagina) {
    int indiceInicio = (pagina - 1) * FILAS_POR_PAGINA;
    int indiceFin = Math.min(indiceInicio + FILAS_POR_PAGINA, items.size());

    return new ArrayList<>(items.subList(indiceInicio, indiceFin));
  }
}
