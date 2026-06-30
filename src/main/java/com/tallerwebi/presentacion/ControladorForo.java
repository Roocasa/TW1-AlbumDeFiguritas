package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioPerfil;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.amistad.ServicioAmistad;
import com.tallerwebi.dominio.excepcion.IntercambioFiguritasException;
import com.tallerwebi.dominio.foro.ServicioForo;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ControladorForo {

  private static final String ATRIBUTO_USUARIO = "USUARIO";
  private static final String REDIRECT_LOGIN = "redirect:/login";
  private static final String REDIRECT_FORO = "redirect:/foro";
  private static final String ATRIBUTO_MENSAJE_EXITO = "mensajeExito";
  private static final String ATRIBUTO_ERROR = "error";
  private static final String RUTA_PUBLICA_FORO = "/uploads/foro/";
  private static final long TAMANIO_MAXIMO_BYTES = 2L * 1024L * 1024L;
  private static final Set<String> EXTENSIONES_PERMITIDAS = Set.of(
    ".png",
    ".jpg",
    ".jpeg",
    ".gif",
    ".webp"
  );

  private final ServicioForo servicioForo;
  private final ServicioPerfil servicioPerfil;
  private final ServicioAmistad servicioAmistad;
  private final Path directorioFotosForo;

  @Autowired
  public ControladorForo(
    ServicioForo servicioForo,
    ServicioPerfil servicioPerfil,
    ServicioAmistad servicioAmistad
  ) {
    this(
      servicioForo,
      servicioPerfil,
      servicioAmistad,
      Path.of(System.getProperty("user.dir"), "uploads", "foro")
    );
  }

  ControladorForo(
    ServicioForo servicioForo,
    ServicioPerfil servicioPerfil,
    ServicioAmistad servicioAmistad,
    Path directorioFotosForo
  ) {
    this.servicioForo = servicioForo;
    this.servicioPerfil = servicioPerfil;
    this.servicioAmistad = servicioAmistad;
    this.directorioFotosForo = directorioFotosForo.toAbsolutePath().normalize();
  }

  @RequestMapping(path = "/foro", method = RequestMethod.GET)
  public ModelAndView verForo(HttpSession session) {
    Usuario usuario = obtenerUsuarioEnSesion(session);

    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    actualizarUsuarioEnSesion(session, usuario.getId());

    ModelAndView mav = new ModelAndView("foro");
    mav.addObject("publicaciones", servicioForo.obtenerPublicaciones());
    mav.addObject("usuarioActualId", usuario.getId());
    mav.addObject(
      "idsUsuariosRelacionados",
      servicioAmistad.obtenerIdsUsuariosRelacionados(usuario.getId())
    );
    mav.addObject(
      "figuritasParaDonar",
      servicioForo.obtenerFiguritasRepetidasParaDonar(usuario.getId())
    );
    mav.addObject("donaciones", servicioForo.obtenerDonacionesDisponibles(usuario.getId()));
    return mav;
  }

  @RequestMapping(path = "/foro/publicar", method = RequestMethod.POST)
  public ModelAndView publicar(
    @RequestParam(value = "contenido", required = false) String contenido,
    @RequestParam(value = "foto", required = false) MultipartFile foto,
    HttpSession session,
    RedirectAttributes redirectAttributes
  ) {
    Usuario usuario = obtenerUsuarioEnSesion(session);

    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    try {
      String imagenUrl = guardarFotoSiCorresponde(usuario.getId(), foto);
      servicioForo.publicar(usuario.getId(), contenido, imagenUrl);
      redirectAttributes.addFlashAttribute(ATRIBUTO_MENSAJE_EXITO, "Publicacion creada.");
    } catch (IllegalArgumentException | IOException e) {
      redirectAttributes.addFlashAttribute(ATRIBUTO_ERROR, e.getMessage());
    }

    return new ModelAndView(REDIRECT_FORO);
  }

  @RequestMapping(path = "/foro/comentar", method = RequestMethod.POST)
  public ModelAndView comentar(
    @RequestParam("idPublicacion") Long idPublicacion,
    @RequestParam("contenido") String contenido,
    HttpSession session,
    RedirectAttributes redirectAttributes
  ) {
    Usuario usuario = obtenerUsuarioEnSesion(session);

    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    try {
      servicioForo.comentar(usuario.getId(), idPublicacion, contenido);
      redirectAttributes.addFlashAttribute(ATRIBUTO_MENSAJE_EXITO, "Comentario publicado.");
    } catch (IllegalArgumentException e) {
      redirectAttributes.addFlashAttribute(ATRIBUTO_ERROR, e.getMessage());
    }

    return new ModelAndView(REDIRECT_FORO);
  }

  @RequestMapping(path = "/foro/donar", method = RequestMethod.POST)
  public ModelAndView donarFigurita(
    @RequestParam("idFigurita") Long idFigurita,
    HttpSession session,
    RedirectAttributes redirectAttributes
  ) {
    Usuario usuario = obtenerUsuarioEnSesion(session);

    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    try {
      servicioForo.donarFigurita(usuario.getId(), idFigurita);
      redirectAttributes.addFlashAttribute(
        ATRIBUTO_MENSAJE_EXITO,
        "Tu figurita repetida quedo disponible para la comunidad."
      );
    } catch (IntercambioFiguritasException e) {
      redirectAttributes.addFlashAttribute(ATRIBUTO_ERROR, e.getMessage());
    }

    return new ModelAndView(REDIRECT_FORO);
  }

  @RequestMapping(path = "/foro/reclamar", method = RequestMethod.POST)
  public ModelAndView reclamarDonacion(
    @RequestParam("idDonacion") Long idDonacion,
    HttpSession session,
    RedirectAttributes redirectAttributes
  ) {
    Usuario usuario = obtenerUsuarioEnSesion(session);

    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    try {
      servicioForo.reclamarDonacion(usuario.getId(), idDonacion);
      actualizarUsuarioEnSesion(session, usuario.getId());
      redirectAttributes.addFlashAttribute(
        ATRIBUTO_MENSAJE_EXITO,
        "Reclamaste la figurita solidaria. Ya esta en tu inventario."
      );
    } catch (IntercambioFiguritasException e) {
      redirectAttributes.addFlashAttribute(ATRIBUTO_ERROR, e.getMessage());
    }

    return new ModelAndView(REDIRECT_FORO);
  }

  private Usuario obtenerUsuarioEnSesion(HttpSession session) {
    return (Usuario) session.getAttribute(ATRIBUTO_USUARIO);
  }

  private void actualizarUsuarioEnSesion(HttpSession session, Long idUsuario) {
    if (servicioPerfil != null) {
      session.setAttribute(ATRIBUTO_USUARIO, servicioPerfil.buscarUsuarioPorId(idUsuario));
    }
  }

  private String guardarFotoSiCorresponde(Long idUsuario, MultipartFile foto) throws IOException {
    if (foto == null || foto.isEmpty()) {
      return null;
    }

    validarFoto(foto);
    Files.createDirectories(directorioFotosForo);

    String extension = obtenerExtension(foto.getOriginalFilename());
    String nombreArchivo = "foro-" + idUsuario + "-" + UUID.randomUUID() + extension;
    Path destino = directorioFotosForo.resolve(nombreArchivo).normalize();

    if (!destino.startsWith(directorioFotosForo)) {
      throw new IllegalArgumentException("No se pudo guardar la imagen seleccionada.");
    }

    Files.write(
      destino,
      foto.getBytes(),
      StandardOpenOption.CREATE,
      StandardOpenOption.TRUNCATE_EXISTING
    );

    return RUTA_PUBLICA_FORO + nombreArchivo;
  }

  private void validarFoto(MultipartFile foto) {
    if (foto.getSize() > TAMANIO_MAXIMO_BYTES) {
      throw new IllegalArgumentException("La foto no puede superar los 2 MB.");
    }

    String tipoContenido = foto.getContentType();
    if (tipoContenido == null || !tipoContenido.toLowerCase(Locale.ROOT).startsWith("image/")) {
      throw new IllegalArgumentException("Solo podes subir imagenes.");
    }

    if (!EXTENSIONES_PERMITIDAS.contains(obtenerExtension(foto.getOriginalFilename()))) {
      throw new IllegalArgumentException("Formato no valido. Usa JPG, PNG, GIF o WEBP.");
    }
  }

  private String obtenerExtension(String nombreOriginal) {
    if (nombreOriginal == null) {
      return "";
    }

    int indiceUltimoPunto = nombreOriginal.lastIndexOf('.');
    if (indiceUltimoPunto < 0 || indiceUltimoPunto == nombreOriginal.length() - 1) {
      return "";
    }

    return nombreOriginal.substring(indiceUltimoPunto).toLowerCase(Locale.ROOT);
  }
}
