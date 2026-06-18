package com.tallerwebi.dominio.mision;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.album.Album;
import com.tallerwebi.dominio.album.PaqueteServicio;
import com.tallerwebi.dominio.album.RepositorioFigurita;
import com.tallerwebi.dominio.album.RepositorioHistorialSobre;
import com.tallerwebi.dominio.album.ServicioAlbum;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("servicioMision")
@Transactional
public class ServicioMisionImpl implements ServicioMision {

  private static final String ABRIR_PRIMER_SOBRE = "ABRIR_PRIMER_SOBRE";
  private static final String PEGAR_DIEZ_FIGURITAS = "PEGAR_DIEZ_FIGURITAS";
  private static final String HACER_PRIMER_INTERCAMBIO = "HACER_PRIMER_INTERCAMBIO";
  private static final String JUNTAR_CINCO_REPETIDAS = "JUNTAR_CINCO_REPETIDAS";
  private static final String COMPLETAR_MEDIO_ALBUM = "COMPLETAR_MEDIO_ALBUM";
  private static final int MINIMO_OBJETIVO_ALBUM = 1;

  private final RepositorioUsuario repositorioUsuario;
  private final RepositorioMisionUsuario repositorioMisionUsuario;
  private final RepositorioHistorialSobre repositorioHistorialSobre;
  private final RepositorioFigurita repositorioFigurita;
  private final PaqueteServicio paqueteServicio;
  private final ServicioAlbum servicioAlbum;

  @Autowired
  public ServicioMisionImpl(
    RepositorioUsuario repositorioUsuario,
    RepositorioMisionUsuario repositorioMisionUsuario,
    RepositorioHistorialSobre repositorioHistorialSobre,
    RepositorioFigurita repositorioFigurita,
    PaqueteServicio paqueteServicio,
    ServicioAlbum servicioAlbum
  ) {
    this.repositorioUsuario = repositorioUsuario;
    this.repositorioMisionUsuario = repositorioMisionUsuario;
    this.repositorioHistorialSobre = repositorioHistorialSobre;
    this.repositorioFigurita = repositorioFigurita;
    this.paqueteServicio = paqueteServicio;
    this.servicioAlbum = servicioAlbum;
  }

  @Override
  public List<MisionEstadoDTO> obtenerMisiones(Long idUsuario) {
    Set<String> misionesCanjeadas = obtenerCodigosCanjeados(idUsuario); // NOPMD
    List<MisionEstadoDTO> estados = new ArrayList<>();

    for (MisionDefinicion mision : crearCatalogoMisiones()) {
      int progreso = calcularProgreso(idUsuario, mision.getCodigo());
      estados.add(
        new MisionEstadoDTO(
          mision,
          progreso,
          progreso >= mision.getObjetivo(),
          misionesCanjeadas.contains(mision.getCodigo())
        )
      );
    }

    return estados;
  }

  @Override
  public MisionDefinicion canjearMision(Long idUsuario, String codigoMision) {
    MisionDefinicion mision = buscarMision(codigoMision);
    if (mision == null) {
      throw new IllegalArgumentException("La mision seleccionada no existe.");
    }

    if (repositorioMisionUsuario.buscarPorUsuarioYCodigo(idUsuario, codigoMision) != null) {
      throw new IllegalArgumentException("Esta mision ya fue canjeada.");
    }

    int progreso = calcularProgreso(idUsuario, codigoMision);
    if (progreso < mision.getObjetivo()) {
      throw new IllegalArgumentException("Todavia no completaste esta mision.");
    }

    Usuario usuario = repositorioUsuario.buscarPorId(idUsuario);
    otorgarRecompensa(usuario, mision);
    repositorioUsuario.modificar(usuario);
    repositorioMisionUsuario.guardar(new MisionUsuario(usuario, codigoMision));
    return mision;
  }

  private List<MisionDefinicion> crearCatalogoMisiones() {
    return Arrays.asList(
      new MisionDefinicion(
        ABRIR_PRIMER_SOBRE,
        "Primer sobre",
        "Abri tu primer sobre del album.",
        1,
        TipoRecompensaMision.MONEDAS,
        25
      ),
      new MisionDefinicion(
        PEGAR_DIEZ_FIGURITAS,
        "Album en marcha",
        "Pega 10 figuritas en tu album.",
        10,
        TipoRecompensaMision.SOBRE_COMUN,
        1
      ),
      new MisionDefinicion(
        HACER_PRIMER_INTERCAMBIO,
        "Primer intercambio",
        "Completa tu primer intercambio con otro usuario.",
        1,
        TipoRecompensaMision.MONEDAS,
        50
      ),
      new MisionDefinicion(
        JUNTAR_CINCO_REPETIDAS,
        "Material para canjear",
        "Junta 5 figuritas repetidas disponibles.",
        5,
        TipoRecompensaMision.SOBRE_COMUN,
        1
      ),
      new MisionDefinicion(
        COMPLETAR_MEDIO_ALBUM,
        "Medio album",
        "Pega la mitad de las figuritas del album.",
        calcularMitadDelAlbum(),
        TipoRecompensaMision.SOBRE_COMUN,
        2
      )
    );
  }

  private int calcularMitadDelAlbum() {
    long totalFiguritas = repositorioFigurita.contarFiguritas();
    if (totalFiguritas <= MINIMO_OBJETIVO_ALBUM) {
      return MINIMO_OBJETIVO_ALBUM;
    }

    return (int) Math.ceil(totalFiguritas / 2.0);
  }

  private Set<String> obtenerCodigosCanjeados(Long idUsuario) {
    Set<String> codigos = new HashSet<>();

    for (MisionUsuario misionUsuario : repositorioMisionUsuario.buscarPorUsuario(idUsuario)) {
      codigos.add(misionUsuario.getCodigoMision());
    }

    return codigos;
  }

  private int calcularProgreso(Long idUsuario, String codigoMision) {
    if (ABRIR_PRIMER_SOBRE.equals(codigoMision)) {
      return repositorioHistorialSobre.buscarPorUsuario(idUsuario).size();
    }

    if (PEGAR_DIEZ_FIGURITAS.equals(codigoMision)) {
      return obtenerCantidadPegadas(idUsuario);
    }

    if (HACER_PRIMER_INTERCAMBIO.equals(codigoMision)) {
      Usuario usuario = repositorioUsuario.buscarPorId(idUsuario);
      return usuario == null ? 0 : usuario.getIntercambiosRealizados();
    }

    if (JUNTAR_CINCO_REPETIDAS.equals(codigoMision)) {
      return paqueteServicio.obtenerCantidadTotalRepetidas(idUsuario);
    }

    if (COMPLETAR_MEDIO_ALBUM.equals(codigoMision)) {
      return obtenerCantidadPegadas(idUsuario);
    }

    return 0;
  }

  private int obtenerCantidadPegadas(Long idUsuario) {
    Album album = servicioAlbum.obtenerAlbumActualizado(idUsuario);
    return album == null || album.getFiguritasPegadas() == null ? 0 : album.getFiguritasPegadas();
  }

  private MisionDefinicion buscarMision(String codigoMision) {
    for (MisionDefinicion mision : crearCatalogoMisiones()) {
      if (mision.getCodigo().equals(codigoMision)) {
        return mision;
      }
    }

    return null;
  }

  private void otorgarRecompensa(Usuario usuario, MisionDefinicion mision) {
    if (mision.getTipoRecompensa() == TipoRecompensaMision.MONEDAS) {
      usuario.sumarMonedas(mision.getCantidadRecompensa());
      return;
    }

    if (mision.getTipoRecompensa() == TipoRecompensaMision.SOBRE_COMUN) {
      usuario.sumarPaquetesComunes(mision.getCantidadRecompensa());
      return;
    }

    usuario.sumarPaquetesComunes(mision.getCantidadRecompensa());
  }
}
