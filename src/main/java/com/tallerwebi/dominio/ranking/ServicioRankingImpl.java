package com.tallerwebi.dominio.ranking;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.album.Album;
import com.tallerwebi.dominio.album.ServicioAlbum;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("servicioRanking")
@Transactional
public class ServicioRankingImpl implements ServicioRanking {

  private final RepositorioUsuario repositorioUsuario;
  private final ServicioAlbum servicioAlbum;

  @Autowired
  public ServicioRankingImpl(RepositorioUsuario repositorioUsuario, ServicioAlbum servicioAlbum) {
    this.repositorioUsuario = repositorioUsuario;
    this.servicioAlbum = servicioAlbum;
  }

  @Override
  public List<RankingColeccionistaDTO> obtenerRankingColeccionistas() {
    List<RankingColeccionistaDTO> ranking = new ArrayList<>();

    for (Usuario usuario : repositorioUsuario.buscarTodos()) {
      Album album = servicioAlbum.obtenerAlbumActualizado(usuario.getId());
      ranking.add(new RankingColeccionistaDTO(0, usuario, album));
    }

    ranking.sort(crearComparadorRanking());
    return asignarPosiciones(ranking);
  }

  private Comparator<RankingColeccionistaDTO> crearComparadorRanking() {
    return Comparator
      .comparingInt(RankingColeccionistaDTO::getPorcentajeCompletado)
      .reversed()
      .thenComparing(
        Comparator
          .comparingInt((RankingColeccionistaDTO ranking) ->
            ranking.getAlbum().getFiguritasPegadas()
          )
          .reversed()
      )
      .thenComparing(
        Comparator
          .comparingInt((RankingColeccionistaDTO ranking) ->
            ranking.getUsuario().getIntercambiosRealizados()
          )
          .reversed()
      )
      .thenComparing(
        ranking -> ranking.getUsuario().getEmail(),
        Comparator.nullsLast(String::compareTo)
      );
  }

  @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
  private List<RankingColeccionistaDTO> asignarPosiciones(List<RankingColeccionistaDTO> ranking) {
    List<RankingColeccionistaDTO> rankingConPosiciones = new ArrayList<>();

    int posicion = 1;
    for (RankingColeccionistaDTO item : ranking) {
      rankingConPosiciones.add(
        new RankingColeccionistaDTO(posicion, item.getUsuario(), item.getAlbum())
      );
      posicion++;
    }

    return rankingConPosiciones;
  }
}
