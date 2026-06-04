package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioFotoPerfil;
import com.tallerwebi.dominio.ServicioPerfil;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.album.Album;
import com.tallerwebi.dominio.album.InventarioItemDTO;
import com.tallerwebi.dominio.album.PaqueteServicio;
import com.tallerwebi.dominio.album.ServicioAlbum;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorPerfil {

  private static final String ATRIBUTO_EMAIL = "EMAIL";
  private static final String ATRIBUTO_USUARIO = "USUARIO";
  private static final String REDIRECT_LOGIN = "redirect:/login";
  private final ServicioPerfil servicioPerfil;
  private final ServicioAlbum servicioAlbum;
  private final PaqueteServicio paqueteServicio;
  private final ServicioFotoPerfil servicioFotoPerfil;

  @Autowired
  public ControladorPerfil(
    ServicioPerfil servicioPerfil,
    ServicioAlbum servicioAlbum,
    PaqueteServicio paqueteServicio,
    ServicioFotoPerfil servicioFotoPerfil
  ) {
    this.servicioPerfil = servicioPerfil;
    this.servicioAlbum = servicioAlbum;
    this.paqueteServicio = paqueteServicio;
    this.servicioFotoPerfil = servicioFotoPerfil;
  }

  public ControladorPerfil(ServicioPerfil servicioPerfil) {
    this(servicioPerfil, null, null, null);
  }

  public ControladorPerfil(ServicioPerfil servicioPerfil, ServicioFotoPerfil servicioFotoPerfil) {
    this(servicioPerfil, null, null, servicioFotoPerfil);
  }

  @RequestMapping(path = "/perfil", method = RequestMethod.GET)
  @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
  public ModelAndView verPerfil(HttpServletRequest request) {
    HttpSession session = request.getSession();
    Usuario usuario = obtenerUsuarioActualizado(session);
    if (usuario == null) return new ModelAndView(REDIRECT_LOGIN);
    return crearVistaPerfil(usuario, null, null, null, null, null, null);
  }

  @RequestMapping(path = "/perfil/foto", method = RequestMethod.POST)
  public ModelAndView actualizarFotoPerfil(
    @RequestParam("fotoPerfil") MultipartFile fotoPerfil,
    HttpServletRequest request
  ) {
    HttpSession session = request.getSession();
    Usuario usuario = obtenerUsuarioAutenticado(session);
    if (usuario == null) return new ModelAndView(REDIRECT_LOGIN);

    if (servicioFotoPerfil == null) {
      return crearVistaPerfil(
        usuario,
        null,
        "No se pudo preparar la carga de imagenes.",
        null,
        null,
        null,
        null
      );
    }

    try {
      String nuevaRuta = servicioFotoPerfil.guardarFoto(
        usuario.getId(),
        fotoPerfil.getOriginalFilename(),
        fotoPerfil.getContentType(),
        fotoPerfil.getBytes(),
        usuario.getFotoPerfil()
      );

      Usuario usuarioActualizado = servicioPerfil.actualizarFotoPerfil(usuario.getId(), nuevaRuta);
      actualizarSesionUsuario(session, usuarioActualizado);
      return crearVistaPerfil(
        usuarioActualizado,
        "La foto de perfil se actualizo correctamente.",
        null,
        null,
        null,
        null,
        null
      );
    } catch (IllegalArgumentException e) {
      return crearVistaPerfil(usuario, null, e.getMessage(), null, null, null, null);
    } catch (IOException e) {
      return crearVistaPerfil(
        usuario,
        null,
        "No pudimos guardar la imagen. Intenta nuevamente.",
        null,
        null,
        null,
        null
      );
    }
  }

  @RequestMapping(path = "/perfil/foto/eliminar", method = RequestMethod.POST)
  public ModelAndView eliminarFotoPerfil(HttpServletRequest request) {
    HttpSession session = request.getSession();
    Usuario usuario = obtenerUsuarioAutenticado(session);
    if (usuario == null) return new ModelAndView(REDIRECT_LOGIN);

    try {
      if (servicioFotoPerfil != null) {
        servicioFotoPerfil.eliminarFoto(usuario.getFotoPerfil());
      }

      Usuario usuarioActualizado = servicioPerfil.eliminarFotoPerfil(usuario.getId());
      actualizarSesionUsuario(session, usuarioActualizado);
      return crearVistaPerfil(
        usuarioActualizado,
        "La foto de perfil se elimino correctamente.",
        null,
        null,
        null,
        null,
        null
      );
    } catch (IOException e) {
      return crearVistaPerfil(
        usuario,
        null,
        "No pudimos eliminar la foto actual. Intenta nuevamente.",
        null,
        null,
        null,
        null
      );
    }
  }

  @RequestMapping(path = "/perfil/email", method = RequestMethod.POST)
  public ModelAndView actualizarEmail(
    @RequestParam("nuevoEmail") String nuevoEmail,
    @RequestParam("passwordActual") String passwordActual,
    HttpServletRequest request
  ) {
    HttpSession session = request.getSession();
    Usuario usuario = obtenerUsuarioAutenticado(session);
    if (usuario == null) return new ModelAndView(REDIRECT_LOGIN);

    try {
      Usuario usuarioActualizado = servicioPerfil.actualizarEmail(
        usuario.getId(),
        nuevoEmail,
        passwordActual
      );
      actualizarSesionUsuario(session, usuarioActualizado);
      return crearVistaPerfil(
        usuarioActualizado,
        null,
        null,
        "Tu email se actualizo correctamente.",
        null,
        null,
        null
      );
    } catch (UsuarioExistente e) {
      return crearVistaPerfil(
        usuario,
        null,
        null,
        null,
        "Ese email ya esta registrado por otra cuenta.",
        null,
        null
      );
    } catch (IllegalArgumentException | SecurityException e) {
      return crearVistaPerfil(usuario, null, null, null, e.getMessage(), null, null);
    }
  }

  @RequestMapping(path = "/perfil/password", method = RequestMethod.POST)
  public ModelAndView actualizarPassword(
    @RequestParam("passwordActual") String passwordActual,
    @RequestParam("nuevaPassword") String nuevaPassword,
    @RequestParam("confirmacionPassword") String confirmacionPassword,
    HttpServletRequest request
  ) {
    HttpSession session = request.getSession();
    Usuario usuario = obtenerUsuarioAutenticado(session);
    if (usuario == null) return new ModelAndView(REDIRECT_LOGIN);

    try {
      Usuario usuarioActualizado = servicioPerfil.actualizarPassword(
        usuario.getId(),
        passwordActual,
        nuevaPassword,
        confirmacionPassword
      );
      actualizarSesionUsuario(session, usuarioActualizado);
      return crearVistaPerfil(
        usuarioActualizado,
        null,
        null,
        null,
        null,
        "Tu contrasena se actualizo correctamente.",
        null
      );
    } catch (IllegalArgumentException | SecurityException e) {
      return crearVistaPerfil(usuario, null, null, null, null, null, e.getMessage());
    }
  }

  private ModelAndView crearVistaPerfil(
    Usuario usuario,
    String mensajeFoto,
    String errorFoto,
    String mensajeEmail,
    String errorEmail,
    String mensajePassword,
    String errorPassword
  ) {
    ModelMap modelo = new ModelMap();
    modelo.put("usuario", usuario);
    modelo.put("mensajeFoto", mensajeFoto);
    modelo.put("errorFoto", errorFoto);
    modelo.put("mensajeEmail", mensajeEmail);
    modelo.put("errorEmail", errorEmail);
    modelo.put("mensajePassword", mensajePassword);
    modelo.put("errorPassword", errorPassword);

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

  private Usuario obtenerUsuarioActualizado(HttpSession session) {
    Usuario usuarioEnSesion = (Usuario) session.getAttribute(ATRIBUTO_USUARIO);

    if (usuarioEnSesion != null) {
      Usuario usuarioActualizado = servicioPerfil.otorgarPaquetesDiariosSiCorresponde(
        usuarioEnSesion.getId()
      );
      actualizarSesionUsuario(session, usuarioActualizado);
      return usuarioActualizado;
    }

    String email = (String) session.getAttribute(ATRIBUTO_EMAIL);
    if (email == null) {
      return null;
    }

    Usuario usuario = servicioPerfil.buscarUsuarioPorEmail(email);
    if (usuario == null) {
      return null;
    }

    Usuario usuarioActualizado = servicioPerfil.otorgarPaquetesDiariosSiCorresponde(
      usuario.getId()
    );
    actualizarSesionUsuario(session, usuarioActualizado);
    return usuarioActualizado;
  }

  private Usuario obtenerUsuarioAutenticado(HttpSession session) {
    Usuario usuarioEnSesion = (Usuario) session.getAttribute(ATRIBUTO_USUARIO);
    if (usuarioEnSesion != null) {
      return servicioPerfil.buscarUsuarioPorId(usuarioEnSesion.getId());
    }

    String email = (String) session.getAttribute(ATRIBUTO_EMAIL);
    if (email == null) {
      return null;
    }

    return servicioPerfil.buscarUsuarioPorEmail(email);
  }

  private void actualizarSesionUsuario(HttpSession session, Usuario usuario) {
    session.setAttribute(ATRIBUTO_USUARIO, usuario);
    session.setAttribute(ATRIBUTO_EMAIL, usuario.getEmail());
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
