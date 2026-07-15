package com.tallerwebi.dominio.ranking;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.album.Album;
import org.junit.jupiter.api.Test;

public class RankingColeccionistaDTOTest {

  @Test
  public void siNoTieneFiguritasPegadasDebeMostrarCeroPorCiento() {
    RankingColeccionistaDTO ranking = new RankingColeccionistaDTO(
      1,
      new Usuario(),
      albumConProgreso(0, 600)
    );

    assertThat(ranking.getPorcentajeCompletado(), equalTo(0));
  }

  @Test
  public void siTieneFiguritasPegadasDebeMostrarAlMenosUnoPorCiento() {
    RankingColeccionistaDTO ranking = new RankingColeccionistaDTO(
      1,
      new Usuario(),
      albumConProgreso(3, 600)
    );

    assertThat(ranking.getPorcentajeCompletado(), equalTo(1));
  }

  @Test
  public void siTieneMasFiguritasPegadasQueElTotalDebeMostrarComoMaximoCienPorCiento() {
    RankingColeccionistaDTO ranking = new RankingColeccionistaDTO(
      1,
      new Usuario(),
      albumConProgreso(601, 600)
    );

    assertThat(ranking.getPorcentajeCompletado(), equalTo(100));
  }

  private Album albumConProgreso(int pegadas, int total) {
    Album album = new Album();
    album.setFiguritasPegadas(pegadas);
    album.setTotalFiguritas(total);
    return album;
  }
}
