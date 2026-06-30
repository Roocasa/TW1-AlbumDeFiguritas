package com.tallerwebi.dominio.amistad;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.Usuario;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("servicioAmistad")
@Transactional
@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
public class ServicioAmistadImpl implements ServicioAmistad {

  private final RepositorioAmistad repositorioAmistad;
  private final RepositorioUsuario repositorioUsuario;

  @Autowired
  public ServicioAmistadImpl(
    RepositorioAmistad repositorioAmistad,
    RepositorioUsuario repositorioUsuario
  ) {
    this.repositorioAmistad = repositorioAmistad;
    this.repositorioUsuario = repositorioUsuario;
  }

  @Override
  public void enviarSolicitud(Long idSolicitante, Long idReceptor) {
    validarUsuariosDistintos(idSolicitante, idReceptor);
    Usuario solicitante = obtenerUsuario(idSolicitante);
    Usuario receptor = obtenerUsuario(idReceptor);
    SolicitudAmistad existente = repositorioAmistad.buscarEntreUsuarios(idSolicitante, idReceptor);

    if (existente == null) {
      repositorioAmistad.guardar(new SolicitudAmistad(solicitante, receptor));
      return;
    }

    if (existente.isAceptada()) {
      throw new IllegalArgumentException("Ya son amigos.");
    }

    if (existente.isPendiente()) {
      throw new IllegalArgumentException("Ya hay una solicitud pendiente.");
    }

    existente.reactivar(solicitante, receptor);
    repositorioAmistad.modificar(existente);
  }

  @Override
  public void aceptarSolicitud(Long idUsuario, Long idSolicitud) {
    SolicitudAmistad solicitud = obtenerSolicitudPendienteDelReceptor(idUsuario, idSolicitud);
    solicitud.aceptar();
    repositorioAmistad.modificar(solicitud);
  }

  @Override
  public void rechazarSolicitud(Long idUsuario, Long idSolicitud) {
    SolicitudAmistad solicitud = obtenerSolicitudPendienteDelReceptor(idUsuario, idSolicitud);
    solicitud.rechazar();
    repositorioAmistad.modificar(solicitud);
  }

  @Override
  public List<SolicitudAmistad> obtenerSolicitudesRecibidas(Long idUsuario) {
    return repositorioAmistad.buscarPendientesRecibidas(idUsuario);
  }

  @Override
  public List<SolicitudAmistad> obtenerSolicitudesEnviadas(Long idUsuario) {
    return repositorioAmistad.buscarPendientesEnviadas(idUsuario);
  }

  @Override
  public List<Usuario> obtenerAmigos(Long idUsuario) {
    List<Usuario> amigos = new ArrayList<>();

    for (SolicitudAmistad solicitud : repositorioAmistad.buscarAceptadas(idUsuario)) {
      amigos.add(obtenerOtroUsuario(idUsuario, solicitud));
    }

    return amigos;
  }

  @Override
  public List<Usuario> obtenerUsuariosParaAgregar(Long idUsuario) {
    Set<Long> idsRelacionados = obtenerIdsUsuariosRelacionados(idUsuario);
    List<Usuario> usuarios = new ArrayList<>();

    for (Usuario usuario : repositorioUsuario.buscarTodosExcepto(idUsuario)) {
      if (!idsRelacionados.contains(usuario.getId())) {
        usuarios.add(usuario);
      }
    }

    return usuarios;
  }

  @Override
  public Set<Long> obtenerIdsUsuariosRelacionados(Long idUsuario) {
    Set<Long> idsRelacionados = new HashSet<>();

    for (SolicitudAmistad solicitud : repositorioAmistad.buscarRelacionadas(idUsuario)) {
      if (!EstadoSolicitudAmistad.RECHAZADA.equals(solicitud.getEstado())) {
        idsRelacionados.add(obtenerOtroUsuario(idUsuario, solicitud).getId());
      }
    }

    return idsRelacionados;
  }

  private SolicitudAmistad obtenerSolicitudPendienteDelReceptor(Long idUsuario, Long idSolicitud) {
    SolicitudAmistad solicitud = repositorioAmistad.buscarPorId(idSolicitud);

    if (solicitud == null || !solicitud.isPendiente()) {
      throw new IllegalArgumentException("La solicitud ya no esta pendiente.");
    }

    if (!solicitud.getReceptor().getId().equals(idUsuario)) {
      throw new IllegalArgumentException("No podes responder esta solicitud.");
    }

    return solicitud;
  }

  private Usuario obtenerUsuario(Long idUsuario) {
    Usuario usuario = repositorioUsuario.buscarPorId(idUsuario);
    if (usuario == null) {
      throw new IllegalArgumentException("No encontramos el usuario.");
    }
    return usuario;
  }

  private void validarUsuariosDistintos(Long idSolicitante, Long idReceptor) {
    if (idSolicitante == null || idReceptor == null || idSolicitante.equals(idReceptor)) {
      throw new IllegalArgumentException("No podes enviarte una solicitud a vos mismo.");
    }
  }

  private Usuario obtenerOtroUsuario(Long idUsuario, SolicitudAmistad solicitud) {
    if (solicitud.getSolicitante().getId().equals(idUsuario)) {
      return solicitud.getReceptor();
    }
    return solicitud.getSolicitante();
  }
}
