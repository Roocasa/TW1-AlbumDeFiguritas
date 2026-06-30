package com.tallerwebi.dominio.foro;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.album.InventarioItemDTO;
import com.tallerwebi.dominio.album.PaqueteServicio;
import com.tallerwebi.dominio.album.RelacionFiguritaUsuario;
import com.tallerwebi.dominio.album.RepositorioInventario;
import com.tallerwebi.dominio.album.ServicioAlbum;
import com.tallerwebi.dominio.excepcion.IntercambioFiguritasException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("servicioForo")
@Transactional
@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
public class ServicioForoImpl implements ServicioForo {

  private final RepositorioForo repositorioForo;
  private final RepositorioUsuario repositorioUsuario;
  private final RepositorioInventario repositorioInventario;
  private final PaqueteServicio paqueteServicio;
  private final ServicioAlbum servicioAlbum;

  @Autowired
  public ServicioForoImpl(
    RepositorioForo repositorioForo,
    RepositorioUsuario repositorioUsuario,
    RepositorioInventario repositorioInventario,
    PaqueteServicio paqueteServicio,
    ServicioAlbum servicioAlbum
  ) {
    this.repositorioForo = repositorioForo;
    this.repositorioUsuario = repositorioUsuario;
    this.repositorioInventario = repositorioInventario;
    this.paqueteServicio = paqueteServicio;
    this.servicioAlbum = servicioAlbum;
  }

  @Override
  public List<ForoPublicacionDTO> obtenerPublicaciones() {
    List<ForoPublicacionDTO> publicaciones = new ArrayList<>();

    for (ForoPublicacion publicacion : repositorioForo.buscarPublicaciones()) {
      publicaciones.add(
        new ForoPublicacionDTO(
          publicacion,
          repositorioForo.buscarComentariosDePublicacion(publicacion.getId())
        )
      );
    }

    return publicaciones;
  }

  @Override
  public void publicar(Long idUsuario, String contenido, String imagenUrl) {
    Usuario autor = obtenerUsuario(idUsuario);
    String contenidoLimpio = limpiarTexto(contenido);

    if (contenidoLimpio.isEmpty() && (imagenUrl == null || imagenUrl.isBlank())) {
      throw new IllegalArgumentException("Escribi un mensaje o subi una foto para publicar.");
    }

    repositorioForo.guardarPublicacion(new ForoPublicacion(autor, contenidoLimpio, imagenUrl));
  }

  @Override
  public void comentar(Long idUsuario, Long idPublicacion, String contenido) {
    Usuario autor = obtenerUsuario(idUsuario);
    ForoPublicacion publicacion = repositorioForo.buscarPublicacionPorId(idPublicacion);
    String contenidoLimpio = limpiarTexto(contenido);

    if (publicacion == null) {
      throw new IllegalArgumentException("La publicacion ya no existe.");
    }

    if (contenidoLimpio.isEmpty()) {
      throw new IllegalArgumentException("Escribi un comentario antes de enviarlo.");
    }

    repositorioForo.guardarComentario(new ForoComentario(publicacion, autor, contenidoLimpio));
  }

  @Override
  public List<InventarioItemDTO> obtenerFiguritasRepetidasParaDonar(Long idUsuario) {
    List<InventarioItemDTO> repetidas = new ArrayList<>();

    for (InventarioItemDTO item : paqueteServicio.obtenerFiguritasDelInventario(idUsuario)) {
      if (item.isDisponibleParaIntercambio()) {
        repetidas.add(item);
      }
    }

    repetidas.sort(
      Comparator.comparing(
        item -> item.getFigurita().getOrdenAlbum(),
        Comparator.nullsLast(Integer::compareTo)
      )
    );
    return repetidas;
  }

  @Override
  public List<DonacionSolidaria> obtenerDonacionesDisponibles(Long idUsuario) {
    List<DonacionSolidaria> donaciones = new ArrayList<>();

    for (DonacionSolidaria donacion : repositorioForo.buscarDonacionesDisponibles()) {
      if (!donacion.getDonante().getId().equals(idUsuario)) {
        donaciones.add(donacion);
      }
    }

    return donaciones;
  }

  @Override
  public void donarFigurita(Long idUsuario, Long idFigurita) throws IntercambioFiguritasException {
    Usuario donante = obtenerUsuario(idUsuario);
    RelacionFiguritaUsuario relacion = buscarRelacionRepetida(idUsuario, idFigurita);

    if (relacion == null) {
      throw new IntercambioFiguritasException("Solo podes donar una figurita repetida.");
    }

    if (repositorioForo.buscarDonacionDisponiblePorRelacion(relacion.getId()) != null) {
      throw new IntercambioFiguritasException("Esa figurita ya esta publicada como donacion.");
    }

    repositorioForo.guardarDonacion(new DonacionSolidaria(donante, relacion));
  }

  @Override
  public void reclamarDonacion(Long idUsuario, Long idDonacion)
    throws IntercambioFiguritasException {
    Usuario reclamante = obtenerUsuario(idUsuario);
    DonacionSolidaria donacion = repositorioForo.buscarDonacionPorId(idDonacion);

    validarDonacionReclamable(idUsuario, donacion);

    RelacionFiguritaUsuario relacion = donacion.getRelacionDonada();
    Usuario donante = relacion.getPropietario();

    relacion.setPropietario(reclamante);
    repositorioInventario.modificar(relacion);

    donacion.reclamar(reclamante);
    repositorioForo.modificarDonacion(donacion);

    servicioAlbum.actualizarEstadisticas(donante.getId());
    servicioAlbum.actualizarEstadisticas(reclamante.getId());
  }

  private Usuario obtenerUsuario(Long idUsuario) {
    Usuario usuario = repositorioUsuario.buscarPorId(idUsuario);
    if (usuario == null) {
      throw new IllegalArgumentException("No encontramos tu usuario.");
    }
    return usuario;
  }

  private String limpiarTexto(String texto) {
    return texto == null ? "" : texto.trim();
  }

  private void validarDonacionReclamable(Long idUsuario, DonacionSolidaria donacion)
    throws IntercambioFiguritasException {
    if (donacion == null || !donacion.isDisponible()) {
      throw new IntercambioFiguritasException("La donacion ya no esta disponible.");
    }

    if (donacion.getDonante().getId().equals(idUsuario)) {
      throw new IntercambioFiguritasException("No podes reclamar tu propia donacion.");
    }

    if (
      !donacion.getDonante().getId().equals(donacion.getRelacionDonada().getPropietario().getId())
    ) {
      throw new IntercambioFiguritasException("La figurita donada ya no esta disponible.");
    }
  }

  private RelacionFiguritaUsuario buscarRelacionRepetida(Long idUsuario, Long idFigurita) {
    for (InventarioItemDTO item : obtenerFiguritasRepetidasParaDonar(idUsuario)) {
      if (item.getFigurita().getId().equals(idFigurita)) {
        return repositorioInventario.buscarRelacionDisponible(idUsuario, idFigurita);
      }
    }

    return null;
  }
}
