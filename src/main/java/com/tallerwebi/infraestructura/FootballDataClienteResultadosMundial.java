package com.tallerwebi.infraestructura;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tallerwebi.dominio.prode.ClienteResultadosMundial;
import com.tallerwebi.dominio.prode.ResultadoPartidoApi;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

@Component("clienteResultadosMundial")
public class FootballDataClienteResultadosMundial implements ClienteResultadosMundial {

  private static final String TOKEN_ENV = "FOOTBALL_DATA_TOKEN";
  private static final String TOKEN_PROPERTY = "football.data.token";
  private static final ZoneId ZONA_HORARIA_ARGENTINA = ZoneId.of("America/Argentina/Buenos_Aires");
  private static final String ENDPOINT =
    "https://api.football-data.org/v4/competitions/WC/matches?season=2026";

  @Override
  public List<ResultadoPartidoApi> obtenerPartidos() {
    String token = obtenerToken();
    if (token == null) {
      return Collections.emptyList();
    }

    try {
      HttpURLConnection conexion = abrirConexion(token);
      if (conexion.getResponseCode() != HttpURLConnection.HTTP_OK) {
        return Collections.emptyList();
      }
      return parsearRespuesta(conexion);
    } catch (IOException e) {
      return Collections.emptyList();
    }
  }

  private HttpURLConnection abrirConexion(String token) throws IOException {
    HttpURLConnection conexion = (HttpURLConnection) new URL(ENDPOINT).openConnection();
    conexion.setRequestMethod("GET");
    conexion.setRequestProperty("X-Auth-Token", token);
    conexion.setConnectTimeout(5000);
    conexion.setReadTimeout(5000);
    return conexion;
  }

  private List<ResultadoPartidoApi> parsearRespuesta(HttpURLConnection conexion)
    throws IOException {
    JsonObject respuesta = JsonParser
      .parseReader(new InputStreamReader(conexion.getInputStream(), StandardCharsets.UTF_8))
      .getAsJsonObject();
    JsonArray matches = respuesta.getAsJsonArray("matches");
    List<ResultadoPartidoApi> resultados = new ArrayList<>();

    for (JsonElement elemento : matches) {
      resultados.add(parsearPartido(elemento.getAsJsonObject()));
    }

    return resultados;
  }

  private ResultadoPartidoApi parsearPartido(JsonObject match) {
    JsonObject score = match.getAsJsonObject("score").getAsJsonObject("fullTime");
    boolean finalizado = "FINISHED".equals(match.get("status").getAsString());
    Integer golesLocal = obtenerEntero(score, "home");
    Integer golesVisitante = obtenerEntero(score, "away");

    return new ResultadoPartidoApi(
      match.get("id").getAsLong(),
      match.getAsJsonObject("homeTeam").get("name").getAsString(),
      match.getAsJsonObject("awayTeam").get("name").getAsString(),
      OffsetDateTime
        .parse(match.get("utcDate").getAsString())
        .atZoneSameInstant(ZONA_HORARIA_ARGENTINA)
        .toLocalDateTime(),
      finalizado,
      golesLocal,
      golesVisitante
    );
  }

  private Integer obtenerEntero(JsonObject objeto, String propiedad) {
    return objeto.get(propiedad).isJsonNull() ? null : objeto.get(propiedad).getAsInt();
  }

  private String obtenerToken() {
    String token = normalizar(System.getenv(TOKEN_ENV));
    if (token != null) {
      return token;
    }
    return normalizar(System.getProperty(TOKEN_PROPERTY));
  }

  private String normalizar(String valor) {
    return valor == null || valor.trim().isEmpty() ? null : valor.trim();
  }
}
