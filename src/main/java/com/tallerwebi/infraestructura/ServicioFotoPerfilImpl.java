package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.ServicioFotoPerfil;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service("servicioFotoPerfil")
public class ServicioFotoPerfilImpl implements ServicioFotoPerfil {

  private static final long TAMANIO_MAXIMO_BYTES = 2L * 1024L * 1024L;
  private static final String RUTA_PUBLICA_BASE = "/uploads/perfiles/";
  private static final Set<String> EXTENSIONES_PERMITIDAS = Set.of(
    ".png",
    ".jpg",
    ".jpeg",
    ".gif",
    ".webp"
  );

  private final Path directorioFotos;

  public ServicioFotoPerfilImpl() {
    this(Path.of(System.getProperty("user.dir"), "uploads", "perfiles"));
  }

  ServicioFotoPerfilImpl(Path directorioFotos) {
    this.directorioFotos = directorioFotos.toAbsolutePath().normalize();
  }

  @Override
  public String guardarFoto(
    Long idUsuario,
    String nombreOriginal,
    String tipoContenido,
    byte[] contenido,
    String fotoActual
  ) throws IOException {
    validarImagen(nombreOriginal, tipoContenido, contenido);
    Files.createDirectories(directorioFotos);

    String extension = obtenerExtension(nombreOriginal);
    String nombreArchivo = "usuario-" + idUsuario + "-" + UUID.randomUUID() + extension;
    Path destino = directorioFotos.resolve(nombreArchivo).normalize();

    if (!destino.startsWith(directorioFotos)) {
      throw new IllegalArgumentException("No se pudo guardar la imagen seleccionada.");
    }

    Files.write(
      destino,
      contenido,
      StandardOpenOption.CREATE,
      StandardOpenOption.TRUNCATE_EXISTING
    );

    eliminarFoto(fotoActual);
    return RUTA_PUBLICA_BASE + nombreArchivo;
  }

  @Override
  public void eliminarFoto(String rutaFoto) throws IOException {
    if (rutaFoto == null || rutaFoto.isBlank() || !rutaFoto.startsWith(RUTA_PUBLICA_BASE)) {
      return;
    }

    String nombreArchivo = rutaFoto.substring(RUTA_PUBLICA_BASE.length());
    if (
      nombreArchivo.isBlank() ||
      nombreArchivo.contains("/") ||
      nombreArchivo.contains("\\") ||
      nombreArchivo.contains("..")
    ) {
      return;
    }

    Path archivo = directorioFotos.resolve(nombreArchivo).normalize();
    if (!archivo.startsWith(directorioFotos)) {
      return;
    }

    Files.deleteIfExists(archivo);
  }

  private void validarImagen(String nombreOriginal, String tipoContenido, byte[] contenido) {
    validarContenidoPresente(contenido);
    validarTamanio(contenido);
    validarTipoContenido(tipoContenido);
    validarExtensionPermitida(nombreOriginal);
  }

  private void validarContenidoPresente(byte[] contenido) {
    if (contenido == null || contenido.length == 0) {
      throw new IllegalArgumentException("Selecciona una imagen para subir.");
    }
  }

  private void validarTamanio(byte[] contenido) {
    if (contenido.length > TAMANIO_MAXIMO_BYTES) {
      throw new IllegalArgumentException("La foto no puede superar los 2 MB.");
    }
  }

  private void validarTipoContenido(String tipoContenido) {
    if (tipoContenido == null || !tipoContenido.toLowerCase(Locale.ROOT).startsWith("image/")) {
      throw new IllegalArgumentException("Solo puedes subir imagenes.");
    }
  }

  private void validarExtensionPermitida(String nombreOriginal) {
    String extension = obtenerExtension(nombreOriginal);
    if (!EXTENSIONES_PERMITIDAS.contains(extension)) {
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
