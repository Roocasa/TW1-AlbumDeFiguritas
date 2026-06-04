package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ServicioFotoPerfilImplTest {

  @TempDir
  Path tempDir;

  @Test
  public void deberiaGuardarUnaFotoYDevolverLaRutaPublica() throws IOException {
    ServicioFotoPerfilImpl servicioFotoPerfil = new ServicioFotoPerfilImpl(tempDir);

    String ruta = servicioFotoPerfil.guardarFoto(
      3L,
      "avatar.png",
      "image/png",
      "contenido".getBytes(StandardCharsets.UTF_8),
      null
    );

    Path archivoGuardado = tempDir.resolve(ruta.substring("/uploads/perfiles/".length()));

    assertThat(ruta.startsWith("/uploads/perfiles/"), is(true));
    assertThat(Files.exists(archivoGuardado), is(true));
  }

  @Test
  public void deberiaEliminarLaFotoAnteriorAlGuardarUnaNueva() throws IOException {
    ServicioFotoPerfilImpl servicioFotoPerfil = new ServicioFotoPerfilImpl(tempDir);
    String rutaAnterior = servicioFotoPerfil.guardarFoto(
      3L,
      "avatar.png",
      "image/png",
      "primera".getBytes(StandardCharsets.UTF_8),
      null
    );

    Path archivoAnterior = tempDir.resolve(rutaAnterior.substring("/uploads/perfiles/".length()));

    String rutaNueva = servicioFotoPerfil.guardarFoto(
      3L,
      "avatar.jpg",
      "image/jpeg",
      "segunda".getBytes(StandardCharsets.UTF_8),
      rutaAnterior
    );

    assertThat(Files.exists(archivoAnterior), is(false));
    assertThat(rutaNueva.equals(rutaAnterior), is(false));
  }

  @Test
  public void deberiaIgnorarRutasExternasAlEliminarFoto() throws IOException {
    ServicioFotoPerfilImpl servicioFotoPerfil = new ServicioFotoPerfilImpl(tempDir);
    Path archivoExterno = tempDir.resolve("externo.txt");
    Files.writeString(archivoExterno, "no borrar", StandardCharsets.UTF_8);

    servicioFotoPerfil.eliminarFoto("/otra/ruta/externo.txt");

    assertThat(Files.exists(archivoExterno), equalTo(true));
  }

  @Test
  public void noDeberiaGuardarArchivosConExtensionNoPermitida() {
    ServicioFotoPerfilImpl servicioFotoPerfil = new ServicioFotoPerfilImpl(tempDir);

    Assertions.assertThrows(
      IllegalArgumentException.class,
      () ->
        servicioFotoPerfil.guardarFoto(
          7L,
          "avatar.svg",
          "image/svg+xml",
          "contenido".getBytes(StandardCharsets.UTF_8),
          null
        )
    );
  }
}
