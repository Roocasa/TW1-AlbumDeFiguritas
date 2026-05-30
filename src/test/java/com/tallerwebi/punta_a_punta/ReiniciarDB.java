package com.tallerwebi.punta_a_punta;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Set;

public class ReiniciarDB {

  public static final String CLAVE_POR_DEFECTO = "123456789";
  public static final String EMAIL_USUARIO_BASE = "test@unlam.edu.ar";
  public static final String EMAIL_USUARIO_LEGACY = "eze@test.com";
  public static final String EMAIL_USUARIO_ALBUM_COMPLETO = "albumcompleto@test.com";
  public static final String EMAIL_USUARIO_PEGADO = "e2e-pegado@test.com";
  public static final String EMAIL_USUARIO_CANJE = "e2e-canje@test.com";

  private static final String SQL_REINICIO =
    "SET FOREIGN_KEY_CHECKS = 0;" +
    "TRUNCATE TABLE PropuestaIntercambio;" +
    "TRUNCATE TABLE RelacionFiguritaUsuario;" +
    "TRUNCATE TABLE Album;" +
    "TRUNCATE TABLE Usuario;" +
    "INSERT INTO Usuario " +
    "(email, password, paquetes, paquetesPremium, intercambiosRealizados, activo, rol, pais, fecha_ultimo_regalo_diario) " +
    "VALUES " +
    "('test@unlam.edu.ar', '123456789', 3, 0, 0, true, 'USER', 'Argentina', CURDATE())," +
    "('eze@test.com', '123456789', 3, 0, 0, true, 'USER', 'Argentina', CURDATE())," +
    "('albumcompleto@test.com', '123456789', 0, 0, 0, true, 'USER', 'Argentina', CURDATE())," +
    "('e2e-pegado@test.com', '123456789', 0, 0, 0, true, 'USER', 'Argentina', CURDATE())," +
    "('e2e-canje@test.com', '123456789', 0, 0, 0, true, 'USER', 'Argentina', CURDATE());" +
    "INSERT INTO RelacionFiguritaUsuario (usuario_id, figurita_id, estaPegadaEnElAlbum) " +
    "SELECT usuario.id, figurita.id, true " +
    "FROM Usuario usuario " +
    "CROSS JOIN Figurita figurita " +
    "WHERE usuario.email = 'albumcompleto@test.com';" +
    "INSERT INTO Album " +
    "(usuario_id, total_figuritas, figuritas_pegadas, figuritas_faltantes, figuritas_repetidas) " +
    "SELECT usuario.id, 576, 576, 0, 0 " +
    "FROM Usuario usuario " +
    "WHERE usuario.email = 'albumcompleto@test.com';" +
    "INSERT INTO RelacionFiguritaUsuario (usuario_id, figurita_id, estaPegadaEnElAlbum) " +
    "SELECT usuario.id, 433, false " +
    "FROM Usuario usuario " +
    "WHERE usuario.email = 'e2e-pegado@test.com';" +
    "INSERT INTO RelacionFiguritaUsuario (usuario_id, figurita_id, estaPegadaEnElAlbum) " +
    "SELECT usuario.id, 434, false " +
    "FROM Usuario usuario " +
    "JOIN (" +
    "SELECT 1 AS repetida " +
    "UNION ALL SELECT 2 " +
    "UNION ALL SELECT 3 " +
    "UNION ALL SELECT 4 " +
    "UNION ALL SELECT 5 " +
    "UNION ALL SELECT 6 " +
    "UNION ALL SELECT 7 " +
    "UNION ALL SELECT 8" +
    ") repetidas " +
    "WHERE usuario.email = 'e2e-canje@test.com';" +
    "SET FOREIGN_KEY_CHECKS = 1;";

  public static void limpiarBaseDeDatos() {
    try {
      Process process = crearProcesoDeReseteo().redirectErrorStream(true).start();
      String salida = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8)
        .trim();
      int exitCode = process.waitFor();

      if (exitCode != 0) {
        throw new IllegalStateException(
          "Error al limpiar la base de datos. Exit code: " + exitCode + ". " + salida
        );
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Error ejecutando el reseteo de la base de datos.", e);
    } catch (IOException e) {
      throw new IllegalStateException("Error ejecutando el reseteo de la base de datos.", e);
    }
  }

  private static ProcessBuilder crearProcesoDeReseteo() {
    String dbHost = obtenerVariableDeEntorno("DB_HOST", "localhost");
    String dbPort = obtenerVariableDeEntorno("DB_PORT", "3306");
    String dbName = obtenerVariableDeEntorno("DB_NAME", "tallerwebi");
    String dbUser = obtenerVariableDeEntorno("DB_USER", "user");
    String dbPassword = obtenerVariableDeEntorno("DB_PASSWORD", "user");
    String nombreContenedor = resolverNombreDeContenedorMysql();

    return new ProcessBuilder(
      "docker",
      "exec",
      nombreContenedor,
      "mysql",
      "-h",
      dbHost,
      "-P",
      dbPort,
      "-u",
      dbUser,
      "-p" + dbPassword,
      dbName,
      "-e",
      SQL_REINICIO
    );
  }

  private static String obtenerVariableDeEntorno(String clave, String valorPorDefecto) {
    String valor = System.getenv(clave);
    return valor != null ? valor : valorPorDefecto;
  }

  private static String resolverNombreDeContenedorMysql() {
    Set<String> candidatos = new LinkedHashSet<>();

    String contenedorPorPropiedad = System.getProperty("e2e.mysqlContainer");
    if (contenedorPorPropiedad != null && !contenedorPorPropiedad.isBlank()) {
      candidatos.add(contenedorPorPropiedad);
    }

    String contenedorPorEntorno = System.getenv("E2E_MYSQL_CONTAINER");
    if (contenedorPorEntorno != null && !contenedorPorEntorno.isBlank()) {
      candidatos.add(contenedorPorEntorno);
    }

    candidatos.add("tallerwebi-mysql");
    candidatos.add("mysql-container");

    for (String candidato : candidatos) {
      if (existeContenedor(candidato)) {
        return candidato;
      }
    }

    throw new IllegalStateException(
      "No se encontro un contenedor MySQL valido. Se probaron: " + String.join(", ", candidatos)
    );
  }

  private static boolean existeContenedor(String nombreContenedor) {
    try {
      Process process = new ProcessBuilder(
        "docker",
        "container",
        "inspect",
        "--format",
        "{{.Id}}",
        nombreContenedor
      )
        .redirectErrorStream(true)
        .start();
      String salida = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8)
        .trim();
      int exitCode = process.waitFor();
      return exitCode == 0 && !salida.isBlank();
    } catch (IOException e) {
      throw new IllegalStateException("No se pudo ejecutar Docker para validar contenedores.", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Se interrumpio la validacion del contenedor MySQL.", e);
    }
  }
}
