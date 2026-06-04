package com.tallerwebi.integracion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tallerwebi.integracion.config.HibernateTestConfig;
import com.tallerwebi.integracion.config.SpringWebTestConfig;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = { SpringWebTestConfig.class, HibernateTestConfig.class })
public class ControladorPerfilUploadsTest {

  @Autowired
  private WebApplicationContext wac;

  private MockMvc mockMvc;
  private Path archivoTemporal;

  @BeforeEach
  public void init() throws Exception {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    Path directorio = Path.of(System.getProperty("user.dir"), "uploads", "perfiles");
    Files.createDirectories(directorio);
    this.archivoTemporal = directorio.resolve("perfil-test.txt");
    Files.writeString(this.archivoTemporal, "foto-visible", StandardCharsets.UTF_8);
  }

  @AfterEach
  public void limpiar() throws Exception {
    if (this.archivoTemporal != null) {
      Files.deleteIfExists(this.archivoTemporal);
    }
  }

  @Test
  public void deberiaServirLosArchivosSubidosDelDirectorioUploads() throws Exception {
    String contenido =
      this.mockMvc.perform(get("/uploads/perfiles/perfil-test.txt"))
        .andExpect(status().isOk())
        .andExpect(content().string("foto-visible"))
        .andReturn()
        .getResponse()
        .getContentAsString();

    assertThat(contenido, equalTo("foto-visible"));
  }
}
