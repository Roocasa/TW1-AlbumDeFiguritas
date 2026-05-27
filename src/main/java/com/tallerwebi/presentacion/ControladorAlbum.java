package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioPerfil;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.album.AlbumSlotDTO;
import com.tallerwebi.dominio.album.Pais;
import com.tallerwebi.dominio.album.PaqueteServicio;
import com.tallerwebi.dominio.album.ServicioAlbum;
import com.tallerwebi.dominio.album.ServicioPais;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ControladorAlbum {

  private static final List<Integer> NUMEROS_DE_FIGURITAS = crearNumerosDeFiguritas();
  private final ServicioPais servicioPais;
  private final ServicioAlbum servicioAlbum;
  private final PaqueteServicio paqueteServicio;
  private final ServicioPerfil servicioPerfil;

  private static final String ATRIBUTO_USUARIO = "USUARIO";

  @Autowired
  public ControladorAlbum(
    ServicioPais servicioPais,
    ServicioAlbum servicioAlbum,
    PaqueteServicio paqueteServicio,
    ServicioPerfil servicioPerfil
  ) {
    this.servicioPais = servicioPais;
    this.servicioAlbum = servicioAlbum;
    this.paqueteServicio = paqueteServicio;
    this.servicioPerfil = servicioPerfil;
  }

  public ControladorAlbum(
    ServicioPais servicioPais,
    ServicioAlbum servicioAlbum,
    PaqueteServicio paqueteServicio
  ) {
    this(servicioPais, servicioAlbum, paqueteServicio, null);
  }

  @RequestMapping(path = "/album", method = RequestMethod.GET)
  public ModelAndView irAAlbum(
    HttpSession session,
    @RequestParam(value = "grupo", required = false) String grupo,
    @RequestParam(value = "pais", required = false) String pais
  ) {
    Usuario usuario = (Usuario) session.getAttribute(ATRIBUTO_USUARIO);

    if (usuario == null) {
      return new ModelAndView("redirect:/login");
    }

    if (servicioPerfil != null) {
      usuario = servicioPerfil.otorgarPaquetesDiariosSiCorresponde(usuario.getId());
      session.setAttribute(ATRIBUTO_USUARIO, usuario);
    }

    List<Pais> paises = servicioPais.buscarPaises(grupo, pais);
    Map<String, List<Pais>> paisesPorGrupo = servicioPais.agruparPorGrupo(paises);
    Map<String, List<AlbumSlotDTO>> albumSlotsPorPais = obtenerSlotsPorPais(paises, usuario);

    ModelMap modelo = new ModelMap();
    modelo.put("paisesPorGrupo", paisesPorGrupo);
    modelo.put("grupos", servicioPais.listarGrupos());
    modelo.put("grupoSeleccionado", grupo);
    modelo.put("paisBuscado", pais);
    modelo.put("figuritas", NUMEROS_DE_FIGURITAS);
    modelo.put("album", servicioAlbum.obtenerAlbumActualizado(usuario.getId()));
    modelo.put("pegadasPorPais", servicioAlbum.obtenerPegadasPorPais(usuario.getId()));
    modelo.put("pendientesPorPais", servicioAlbum.obtenerPendientesPorPais(usuario.getId()));
    modelo.put("albumSlotsPorPais", albumSlotsPorPais);

    return new ModelAndView("album", modelo);
  }

  @RequestMapping(path = "/album/pais/{codigo}", method = RequestMethod.GET)
  public ModelAndView verPais(@PathVariable String codigo, HttpSession session) {
    Usuario usuario = (Usuario) session.getAttribute(ATRIBUTO_USUARIO);

    if (usuario == null) {
      return new ModelAndView("redirect:/login");
    }

    if (servicioPerfil != null) {
      usuario = servicioPerfil.otorgarPaquetesDiariosSiCorresponde(usuario.getId());
      session.setAttribute(ATRIBUTO_USUARIO, usuario);
    }

    Pais pais = servicioPais.buscarPorCodigo(codigo);

    if (pais == null) {
      return new ModelAndView("redirect:/album");
    }

    List<AlbumSlotDTO> slots = servicioAlbum.obtenerSlotsPorPais(usuario.getId(), pais.getCodigo());
    List<Pais> paisesOrdenados = servicioPais.buscarPaises(null, null);
    int indicePaisActual = buscarIndicePais(paisesOrdenados, pais.getCodigo());
    Pais paisAnterior = indicePaisActual > 0 ? paisesOrdenados.get(indicePaisActual - 1) : null;
    Pais paisSiguiente = indicePaisActual >= 0 && indicePaisActual < paisesOrdenados.size() - 1
      ? paisesOrdenados.get(indicePaisActual + 1)
      : null;

    ModelMap modelo = new ModelMap();
    modelo.put("codigoPais", pais.getCodigo());
    modelo.put("codigoBandera", pais.getCodigoBandera());
    modelo.put("nombrePais", pais.getNombre());
    modelo.put("figuritas", NUMEROS_DE_FIGURITAS);
    modelo.put("albumSlots", slots);
    modelo.put("album", servicioAlbum.obtenerAlbumActualizado(usuario.getId()));
    modelo.put("paisAnterior", paisAnterior);
    modelo.put("paisSiguiente", paisSiguiente);

    return new ModelAndView("album-pais", modelo);
  }

  @RequestMapping(path = "/album/pais/{codigo}/pegar/{idFigurita}", method = RequestMethod.GET)
  public ModelAndView pegarDesdeElAlbum(
    @PathVariable String codigo,
    @PathVariable Long idFigurita,
    HttpSession session,
    RedirectAttributes ra
  ) {
    Usuario usuario = (Usuario) session.getAttribute(ATRIBUTO_USUARIO);

    if (usuario == null) {
      return new ModelAndView("redirect:/login");
    }

    if (servicioPerfil != null) {
      usuario = servicioPerfil.otorgarPaquetesDiariosSiCorresponde(usuario.getId());
      session.setAttribute(ATRIBUTO_USUARIO, usuario);
    }

    try {
      paqueteServicio.pegarFigurita(usuario.getId(), idFigurita);
    } catch (RuntimeException e) {
      ra.addFlashAttribute("error", e.getMessage());
    }

    return new ModelAndView("redirect:/album/pais/" + codigo + "#figurita-" + idFigurita);
  }

  private static List<Integer> crearNumerosDeFiguritas() {
    List<Integer> numeros = new ArrayList<>();
    for (int numero = 1; numero <= 12; numero++) {
      numeros.add(numero);
    }
    return numeros;
  }

  private int buscarIndicePais(List<Pais> paisesOrdenados, String codigoPais) {
    for (int i = 0; i < paisesOrdenados.size(); i++) {
      if (paisesOrdenados.get(i).getCodigo().equals(codigoPais)) {
        return i;
      }
    }

    return -1;
  }

  private Map<String, List<AlbumSlotDTO>> obtenerSlotsPorPais(List<Pais> paises, Usuario usuario) {
    Map<String, List<AlbumSlotDTO>> slotsPorPais = new HashMap<>();

    for (Pais pais : paises) {
      slotsPorPais.put(
        pais.getCodigo(),
        servicioAlbum.obtenerSlotsPorPais(usuario.getId(), pais.getCodigo())
      );
    }

    return slotsPorPais;
  }
}
