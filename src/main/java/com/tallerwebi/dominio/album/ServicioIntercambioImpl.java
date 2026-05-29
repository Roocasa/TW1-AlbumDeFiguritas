package com.tallerwebi.dominio.album;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.IntercambioFiguritasException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("servicioIntercambio")
@Transactional
public class ServicioIntercambioImpl implements ServicioIntercambio {

  private final RepositorioUsuario repositorioUsuario;
  private final RepositorioInventario repositorioInventario;
  private final RepositorioPropuestaIntercambio repositorioPropuestaIntercambio;
  private final PaqueteServicio paqueteServicio;
  private final ServicioAlbum servicioAlbum;

  @Autowired
  public ServicioIntercambioImpl(
    RepositorioUsuario repositorioUsuario,
    RepositorioInventario repositorioInventario,
    RepositorioPropuestaIntercambio repositorioPropuestaIntercambio,
    PaqueteServicio paqueteServicio,
    ServicioAlbum servicioAlbum
  ) {
    this.repositorioUsuario = repositorioUsuario;
    this.repositorioInventario = repositorioInventario;
    this.repositorioPropuestaIntercambio = repositorioPropuestaIntercambio;
    this.paqueteServicio = paqueteServicio;
    this.servicioAlbum = servicioAlbum;
  }

  @Override
  public List<InventarioItemDTO> obtenerFiguritasPropiasParaIntercambiar(Long idUsuario) {
    return obtenerRepetidas(idUsuario);
  }

  @Override
  public List<OfertaIntercambioDTO> obtenerOfertasDeOtrosUsuarios(Long idUsuario) {
    List<OfertaIntercambioDTO> ofertas = new ArrayList<>();

    for (Usuario usuario : repositorioUsuario.buscarTodosExcepto(idUsuario)) {
      List<InventarioItemDTO> repetidas = obtenerRepetidas(usuario.getId());
      if (!repetidas.isEmpty()) {
        ofertas.add(new OfertaIntercambioDTO(usuario, repetidas));
      }
    }

    return ofertas;
  }

  @Override
  public List<PropuestaIntercambio> obtenerPropuestasRecibidas(Long idUsuario) {
    return repositorioPropuestaIntercambio.buscarPendientesRecibidas(idUsuario);
  }

  @Override
  public List<PropuestaIntercambio> obtenerPropuestasEnviadas(Long idUsuario) {
    return repositorioPropuestaIntercambio.buscarPendientesEnviadas(idUsuario);
  }

  @Override
  public void enviarPropuesta(
    Long idUsuarioOrigen,
    Long idFiguritaOrigen,
    Long idUsuarioDestino,
    Long idFiguritaDestino
  ) throws IntercambioFiguritasException {
    validarUsuarios(idUsuarioOrigen, idUsuarioDestino);

    RelacionFiguritaUsuario relacionOrigen = buscarRelacionRepetida(
      idUsuarioOrigen,
      idFiguritaOrigen
    );
    RelacionFiguritaUsuario relacionDestino = buscarRelacionRepetida( // NOPMD - false positive from PMD dataflow analysis
      idUsuarioDestino,
      idFiguritaDestino
    );

    if (relacionOrigen == null) {
      throw new IntercambioFiguritasException("No tenes esa figurita repetida disponible.");
    }

    if (relacionDestino == null) {
      throw new IntercambioFiguritasException(
        "El otro usuario ya no tiene esa repetida disponible."
      );
    }

    repositorioPropuestaIntercambio.guardar(
      new PropuestaIntercambio(
        relacionOrigen.getPropietario(),
        relacionDestino.getPropietario(),
        relacionOrigen.getFigurita(),
        relacionDestino.getFigurita()
      )
    );
  }

  @Override
  public void aceptarPropuesta(Long idUsuarioReceptor, Long idPropuesta)
    throws IntercambioFiguritasException {
    PropuestaIntercambio propuesta = obtenerPropuestaPendienteParaReceptor(
      idUsuarioReceptor,
      idPropuesta
    );

    intercambiarFiguritas(
      propuesta.getSolicitante().getId(),
      propuesta.getFiguritaOfrecida().getId(),
      propuesta.getReceptor().getId(),
      propuesta.getFiguritaSolicitada().getId()
    );

    propuesta.aceptar();
    repositorioPropuestaIntercambio.modificar(propuesta);
  }

  @Override
  public void rechazarPropuesta(Long idUsuarioReceptor, Long idPropuesta)
    throws IntercambioFiguritasException {
    PropuestaIntercambio propuesta = obtenerPropuestaPendienteParaReceptor(
      idUsuarioReceptor,
      idPropuesta
    );

    propuesta.rechazar();
    repositorioPropuestaIntercambio.modificar(propuesta);
  }

  @Override
  public void intercambiarFiguritas(
    Long idUsuarioOrigen,
    Long idFiguritaOrigen,
    Long idUsuarioDestino,
    Long idFiguritaDestino
  ) throws IntercambioFiguritasException {
    validarUsuarios(idUsuarioOrigen, idUsuarioDestino);

    RelacionFiguritaUsuario relacionOrigen = buscarRelacionRepetida(
      idUsuarioOrigen,
      idFiguritaOrigen
    );
    RelacionFiguritaUsuario relacionDestino = buscarRelacionRepetida( // NOPMD - false positive from PMD dataflow analysis
      idUsuarioDestino,
      idFiguritaDestino
    );

    if (relacionOrigen == null) {
      throw new IntercambioFiguritasException("No tenes esa figurita repetida disponible.");
    }

    if (relacionDestino == null) {
      throw new IntercambioFiguritasException(
        "El otro usuario ya no tiene esa repetida disponible."
      );
    }

    Usuario usuarioOrigen = relacionOrigen.getPropietario();
    Usuario usuarioDestino = relacionDestino.getPropietario();

    relacionOrigen.setPropietario(usuarioDestino);
    relacionDestino.setPropietario(usuarioOrigen);

    repositorioInventario.modificar(relacionOrigen);
    repositorioInventario.modificar(relacionDestino);

    usuarioOrigen.sumarIntercambioRealizado();
    usuarioDestino.sumarIntercambioRealizado();
    repositorioUsuario.modificar(usuarioOrigen);
    repositorioUsuario.modificar(usuarioDestino);

    servicioAlbum.actualizarEstadisticas(idUsuarioOrigen);
    servicioAlbum.actualizarEstadisticas(idUsuarioDestino);
  }

  private void validarUsuarios(Long idUsuarioOrigen, Long idUsuarioDestino)
    throws IntercambioFiguritasException {
    if (idUsuarioOrigen == null || idUsuarioDestino == null) {
      throw new IntercambioFiguritasException("Selecciona usuarios validos para intercambiar.");
    }

    if (idUsuarioOrigen.equals(idUsuarioDestino)) {
      throw new IntercambioFiguritasException("No podes intercambiar figuritas con vos mismo.");
    }
  }

  private List<InventarioItemDTO> obtenerRepetidas(Long idUsuario) {
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

  private RelacionFiguritaUsuario buscarRelacionRepetida(Long idUsuario, Long idFigurita) {
    Usuario usuario = repositorioUsuario.buscarPorId(idUsuario);
    List<RelacionFiguritaUsuario> noPegadas =
      repositorioInventario.buscarFiguritasEnInventarioPorUsuario(usuario);
    List<RelacionFiguritaUsuario> pegadas = repositorioInventario.buscarFiguritasPegadasPorUsuario(
      usuario
    );

    Set<Long> idsPegadas = new HashSet<>();
    for (RelacionFiguritaUsuario pegada : pegadas) {
      idsPegadas.add(pegada.getFigurita().getId());
    }

    List<RelacionFiguritaUsuario> candidatas = new ArrayList<>();
    for (RelacionFiguritaUsuario relacion : noPegadas) {
      if (relacion.getFigurita().getId().equals(idFigurita)) {
        candidatas.add(relacion);
      }
    }

    candidatas.sort(Comparator.comparing(RelacionFiguritaUsuario::getId));
    int indiceCanjeable = idsPegadas.contains(idFigurita) ? 0 : 1;

    return candidatas.size() > indiceCanjeable ? candidatas.get(indiceCanjeable) : null;
  }

  private PropuestaIntercambio obtenerPropuestaPendienteParaReceptor(
    Long idUsuarioReceptor,
    Long idPropuesta
  ) throws IntercambioFiguritasException {
    PropuestaIntercambio propuesta = repositorioPropuestaIntercambio.buscarPorId(idPropuesta);

    if (propuesta == null || !propuesta.isPendiente()) {
      throw new IntercambioFiguritasException("La propuesta ya no esta disponible.");
    }

    if (!propuesta.getReceptor().getId().equals(idUsuarioReceptor)) {
      throw new IntercambioFiguritasException("No podes responder una propuesta de otro usuario.");
    }

    return propuesta;
  }
}
