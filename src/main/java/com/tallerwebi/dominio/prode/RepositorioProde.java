package com.tallerwebi.dominio.prode;

import com.tallerwebi.dominio.Usuario;
import java.util.List;

public interface RepositorioProde {
  List<PartidoProde> buscarPartidos();
  PartidoProde buscarPartido(Long id);
  PartidoProde buscarPartidoPorIdApi(Long idApi);
  void guardarPartido(PartidoProde partido);
  void modificarPartido(PartidoProde partido);
  PronosticoProde buscarPronostico(Usuario usuario, PartidoProde partido);
  List<PronosticoProde> buscarPronosticosPorUsuario(Long idUsuario);
  List<PronosticoProde> buscarPronosticosPorPartido(PartidoProde partido);
  void guardarPronostico(PronosticoProde pronostico);
  void modificarPronostico(PronosticoProde pronostico);
}
