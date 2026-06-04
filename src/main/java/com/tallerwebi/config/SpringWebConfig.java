package com.tallerwebi.config;

import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;

@EnableWebMvc
@Configuration
@ComponentScan(
  { "com.tallerwebi.presentacion", "com.tallerwebi.dominio", "com.tallerwebi.infraestructura" }
)
public class SpringWebConfig implements WebMvcConfigurer {

  // Spring + Thymeleaf need this
  @Autowired
  private ApplicationContext applicationContext;

  @Override
  public void addResourceHandlers(final ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/css/**").addResourceLocations("/resources/core/css/");
    registry.addResourceHandler("/js/**").addResourceLocations("/resources/core/js/");
    registry.addResourceHandler("/img/**").addResourceLocations("/resources/core/img/");
    registry.addResourceHandler("/uploads/**").addResourceLocations(obtenerRutaPublicaUploads());
    registry.addResourceHandler("/webjars/**").addResourceLocations("/webjars/");
  }

  // https://www.thymeleaf.org/doc/tutorials/3.0/thymeleafspring.html
  // Spring + Thymeleaf
  @Bean
  public SpringResourceTemplateResolver templateResolver() {
    // SpringResourceTemplateResolver automatically integrates with Spring's own
    // resource resolution infrastructure, which is highly recommended.
    SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
    templateResolver.setApplicationContext(this.applicationContext);
    templateResolver.setPrefix("/WEB-INF/views/thymeleaf/");
    templateResolver.setSuffix(".html");
    // HTML is the default value, added here for the sake of clarity.
    templateResolver.setTemplateMode(TemplateMode.HTML);
    templateResolver.setCharacterEncoding("UTF-8");
    // Template cache is true by default. Set to false if you want
    // templates to be automatically updated when modified.
    templateResolver.setCacheable(false);
    return templateResolver;
  }

  // Spring + Thymeleaf
  @Bean
  public SpringTemplateEngine templateEngine() {
    // SpringTemplateEngine automatically applies SpringStandardDialect and
    // enables Spring's own MessageSource message resolution mechanisms.
    SpringTemplateEngine templateEngine = new SpringTemplateEngine();
    templateEngine.setTemplateResolver(templateResolver());
    // Enabling the SpringEL compiler with Spring 4.2.4 or newer can
    // speed up execution in most scenarios, but might be incompatible
    // with specific cases when expressions in one template are reused
    // across different data types, so this flag is "false" by default
    // for safer backwards compatibility.
    templateEngine.setEnableSpringELCompiler(true);
    return templateEngine;
  }

  // Spring + Thymeleaf
  // Configure Thymeleaf View Resolver
  @Bean
  public ThymeleafViewResolver viewResolver() {
    ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
    viewResolver.setTemplateEngine(templateEngine());
    return viewResolver;
  }

  @Bean(name = "multipartResolver")
  public CommonsMultipartResolver multipartResolver() {
    CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
    multipartResolver.setDefaultEncoding("UTF-8");
    multipartResolver.setMaxUploadSize(2L * 1024L * 1024L);
    return multipartResolver;
  }

  private String obtenerRutaPublicaUploads() {
    String rutaUploads = Path
      .of(System.getProperty("user.dir"), "uploads")
      .toAbsolutePath()
      .toUri()
      .toString();
    return rutaUploads.endsWith("/") ? rutaUploads : rutaUploads + "/";
  }
}
