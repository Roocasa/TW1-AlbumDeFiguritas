package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioPerfil;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.album.Album;
import com.tallerwebi.dominio.album.InventarioItemDTO;
import com.tallerwebi.dominio.album.PaqueteServicio;
import com.tallerwebi.dominio.album.ServicioAlbum;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorPerfil {

  private final ServicioPerfil servicioPerfil;
  private final ServicioAlbum servicioAlbum;
  private final PaqueteServicio paqueteServicio;

  @Autowired
  public ControladorPerfil(
    ServicioPerfil servicioPerfil,
    ServicioAlbum servicioAlbum,
    PaqueteServicio paqueteServicio
  ) {
    this.servicioPerfil = servicioPerfil;
    this.servicioAlbum = servicioAlbum;
    this.paqueteServicio = paqueteServicio;
  }

  public ControladorPerfil(ServicioPerfil servicioPerfil) {
    this(servicioPerfil, null, null);
  }

  @RequestMapping(path = "/perfil", method = RequestMethod.GET)
  @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
  public ModelAndView verPerfil(HttpServletRequest request) {
    HttpSession session = request.getSession();
    String email = (String) session.getAttribute("EMAIL");

    if (email == null) {
      return new ModelAndView("redirect:/login");
    }

    Usuario usuario = obtenerUsuarioActualizado(session, email);

    if (usuario == null) {
      return new ModelAndView("redirect:/login");
    }

    ModelMap modelo = new ModelMap();
    modelo.put("usuario", usuario);

    if (servicioAlbum != null && paqueteServicio != null) {
      Album album = servicioAlbum.obtenerAlbumActualizado(usuario.getId());
      List<InventarioItemDTO> inventario = paqueteServicio.obtenerFiguritasDelInventario(
        usuario.getId()
      );

      modelo.put("album", album);
      modelo.put("figuritasParaPegar", contarFiguritasParaPegar(inventario));
    }

    return new ModelAndView("perfil", modelo);
  }

  private Usuario obtenerUsuarioActualizado(HttpSession session, String email) {
    Usuario usuarioEnSesion = (Usuario) session.getAttribute("USUARIO");

    if (usuarioEnSesion != null) {
      Usuario usuarioActualizado = servicioPerfil.otorgarPaquetesDiariosSiCorresponde(
        usuarioEnSesion.getId()
      );
      session.setAttribute("USUARIO", usuarioActualizado);
      return usuarioActualizado;
    }

    return servicioPerfil.buscarUsuarioPorEmail(email);
  }

  @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
  private int contarFiguritasParaPegar(List<InventarioItemDTO> inventario) {
    int figuritasParaPegar = 0;

    for (InventarioItemDTO item : inventario) {
      if (item.isSePuedePegar()) {
        figuritasParaPegar++;
      }
    }

    return figuritasParaPegar;
  }
}
