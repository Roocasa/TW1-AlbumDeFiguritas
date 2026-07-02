package com.tallerwebi.dominio.prode;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.Usuario;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("servicioProde")
@Transactional
public class ServicioProdeImpl implements ServicioProde {

  private final RepositorioProde repositorioProde;
  private final RepositorioUsuario repositorioUsuario;
  private final ClienteResultadosMundial clienteResultadosMundial;

  @Autowired
  public ServicioProdeImpl(
    RepositorioProde repositorioProde,
    RepositorioUsuario repositorioUsuario,
    ClienteResultadosMundial clienteResultadosMundial
  ) {
    this.repositorioProde = repositorioProde;
    this.repositorioUsuario = repositorioUsuario;
    this.clienteResultadosMundial = clienteResultadosMundial;
  }

  @Override
  @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
  public List<PartidoProdeDTO> obtenerPartidosConPronosticos(Long idUsuario) {
    Usuario usuario = repositorioUsuario.buscarPorId(idUsuario);
    List<PartidoProdeDTO> partidos = new ArrayList<>();

    for (PartidoProde partido : repositorioProde.buscarPartidos()) {
      partidos.add(
        new PartidoProdeDTO(partido, repositorioProde.buscarPronostico(usuario, partido))
      );
    }

    return partidos;
  }

  @Override
  public int obtenerPuntaje(Long idUsuario) {
    int puntaje = 0;
    for (PronosticoProde pronostico : repositorioProde.buscarPronosticosPorUsuario(idUsuario)) {
      puntaje += pronostico.getPuntos();
    }
    return puntaje;
  }

  @Override
  public void pronosticar(
    Long idUsuario,
    Long idPartido,
    Integer golesLocal,
    Integer golesVisitante
  ) {
    validarGoles(golesLocal, golesVisitante);

    Usuario usuario = repositorioUsuario.buscarPorId(idUsuario);
    PartidoProde partido = repositorioProde.buscarPartido(idPartido);
    validarPronostico(usuario, partido);

    PronosticoProde pronostico = repositorioProde.buscarPronostico(usuario, partido);
    if (pronostico == null) {
      repositorioProde.guardarPronostico(
        new PronosticoProde(usuario, partido, golesLocal, golesVisitante)
      );
      return;
    }

    pronostico.actualizarMarcador(golesLocal, golesVisitante);
    repositorioProde.modificarPronostico(pronostico);
  }

  @Override
  @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
  public int actualizarResultados() {
    int actualizados = 0;
    for (ResultadoPartidoApi resultado : clienteResultadosMundial.obtenerPartidos()) {
      PartidoProde partido = obtenerOCrearPartido(resultado);
      if (resultado.isFinalizado()) {
        partido.actualizarResultado(resultado.getGolesLocal(), resultado.getGolesVisitante());
        repositorioProde.modificarPartido(partido);
        puntuarPronosticos(partido);
        actualizados++;
      }
    }
    return actualizados;
  }

  private PartidoProde obtenerOCrearPartido(ResultadoPartidoApi resultado) {
    PartidoProde partido = repositorioProde.buscarPartidoPorIdApi(resultado.getIdApi());
    if (partido != null) {
      return partido;
    }

    PartidoProde nuevoPartido = new PartidoProde(
      resultado.getIdApi(),
      resultado.getLocal(),
      resultado.getVisitante(),
      resultado.getFecha()
    );
    repositorioProde.guardarPartido(nuevoPartido);
    return nuevoPartido;
  }

  private void puntuarPronosticos(PartidoProde partido) {
    for (PronosticoProde pronostico : repositorioProde.buscarPronosticosPorPartido(partido)) {
      pronostico.puntuar();
      repositorioProde.modificarPronostico(pronostico);
    }
  }

  private void validarGoles(Integer golesLocal, Integer golesVisitante) {
    if (golesLocal == null || golesVisitante == null || golesLocal < 0 || golesVisitante < 0) {
      throw new IllegalArgumentException("Ingresa un marcador valido.");
    }
  }

  private void validarPronostico(Usuario usuario, PartidoProde partido) {
    if (usuario == null) {
      throw new IllegalArgumentException("No encontramos tu usuario.");
    }

    if (partido == null) {
      throw new IllegalArgumentException("El partido seleccionado no existe.");
    }

    if (partido.estaFinalizado()) {
      throw new IllegalArgumentException("El partido ya finalizo.");
    }
  }
}
