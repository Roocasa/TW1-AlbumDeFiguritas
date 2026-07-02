package com.tallerwebi.dominio;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Properties;
import org.springframework.stereotype.Service;

@Service("servicioMercadoPago")
public class ServicioMercadoPagoImpl implements ServicioMercadoPago {

  private static final String ACCESS_TOKEN_ENV = "MERCADOPAGO_ACCESS_TOKEN";
  private static final String BASE_URL_ENV = "MERCADOPAGO_BASE_URL";
  private static final String ACCESS_TOKEN_PROPERTY = "mercadopago.access.token";
  private static final String BASE_URL_PROPERTY = "mercadopago.base.url";
  private static final String LOCAL_PROPERTIES = "mercadopago.local.properties";
  private static final String MONEDA = "ARS";
  private static final String AUTO_RETURN_APPROVED = "approved";
  private static final String RUTA_RETORNO = "/mercado-pago/retorno";

  @Override
  public String crearUrlDePago(PaqueteMonedas paquete, Usuario usuario, String baseUrl) {
    validarDatos(paquete, usuario);

    String accessToken = obtenerConfiguracion(ACCESS_TOKEN_ENV, ACCESS_TOKEN_PROPERTY);
    if (accessToken == null) {
      throw new IllegalStateException(
        "Configura MERCADOPAGO_ACCESS_TOKEN, mercadopago.access.token o mercadopago.local.properties."
      );
    }

    try {
      MercadoPagoConfig.setAccessToken(accessToken);
      Preference preference = new PreferenceClient()
        .create(crearPreferencia(paquete, usuario, baseUrl));
      return preference.getInitPoint();
    } catch (MPApiException excepcion) {
      throw new IllegalStateException(crearMensajeApiMercadoPago(excepcion), excepcion);
    } catch (MPException excepcion) {
      throw new IllegalStateException(
        "No pudimos iniciar el pago con Mercado Pago: " + excepcion.getMessage(),
        excepcion
      );
    }
  }

  private PreferenceRequest crearPreferencia(
    PaqueteMonedas paquete,
    Usuario usuario,
    String baseUrl
  ) {
    String baseUrlRetorno = obtenerBaseUrl(baseUrl);
    PreferenceItemRequest item = PreferenceItemRequest
      .builder()
      .id(paquete.getCodigo())
      .title(paquete.getNombre())
      .description(paquete.getCantidadMonedas() + " monedas para Album 2026")
      .quantity(1)
      .currencyId(MONEDA)
      .unitPrice(BigDecimal.valueOf(paquete.getPrecioPesos()))
      .build();

    PreferenceBackUrlsRequest backUrls = crearBackUrls(baseUrlRetorno);

    PreferenceRequest.PreferenceRequestBuilder preferenceBuilder = PreferenceRequest
      .builder()
      .items(Collections.singletonList(item))
      .backUrls(backUrls)
      .externalReference(usuario.getId() + ":" + paquete.getCodigo());

    if (puedeVolverAutomaticamente(baseUrlRetorno)) {
      preferenceBuilder.autoReturn(AUTO_RETURN_APPROVED);
    }

    return preferenceBuilder.build();
  }

  private PreferenceBackUrlsRequest crearBackUrls(String baseUrlRetorno) {
    String urlRetorno = baseUrlRetorno + RUTA_RETORNO;

    return PreferenceBackUrlsRequest
      .builder()
      .success(urlRetorno)
      .pending(urlRetorno)
      .failure(urlRetorno)
      .build();
  }

  private String obtenerBaseUrl(String baseUrl) {
    String baseUrlConfigurada = obtenerConfiguracion(BASE_URL_ENV, BASE_URL_PROPERTY);
    String url = baseUrlConfigurada != null ? baseUrlConfigurada : baseUrl;

    if (url == null || url.trim().isEmpty()) {
      throw new IllegalStateException("No pudimos determinar la URL base para Mercado Pago.");
    }

    url = url.trim();
    return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
  }

  private boolean puedeVolverAutomaticamente(String baseUrlRetorno) {
    return baseUrlRetorno.startsWith("https://") && !baseUrlRetorno.contains("localhost");
  }

  private String obtenerConfiguracion(String nombreVariable, String nombrePropiedad) {
    String valor = normalizar(System.getenv(nombreVariable));
    if (valor != null) {
      return valor;
    }

    valor = normalizar(System.getProperty(nombrePropiedad));
    if (valor != null) {
      return valor;
    }

    return normalizar(obtenerPropiedadLocal(nombrePropiedad));
  }

  private String obtenerPropiedadLocal(String nombrePropiedad) {
    Path ruta = Path.of(System.getProperty("user.dir"), LOCAL_PROPERTIES);
    if (!Files.exists(ruta)) {
      return null;
    }

    Properties properties = new Properties();
    try (InputStream inputStream = Files.newInputStream(ruta)) {
      properties.load(inputStream);
      return properties.getProperty(nombrePropiedad);
    } catch (IOException e) {
      throw new IllegalStateException("No pudimos leer mercadopago.local.properties.", e);
    }
  }

  private String normalizar(String valor) {
    return valor == null || valor.trim().isEmpty() ? null : valor.trim();
  }

  private String crearMensajeApiMercadoPago(MPApiException excepcion) {
    String detalle = excepcion.getApiResponse() != null
      ? excepcion.getApiResponse().getContent()
      : excepcion.getMessage();
    if (detalle == null || detalle.trim().isEmpty()) {
      detalle = excepcion.getMessage();
    }

    return "Mercado Pago rechazo la preferencia (" + excepcion.getStatusCode() + "): " + detalle;
  }

  private void validarDatos(PaqueteMonedas paquete, Usuario usuario) {
    if (paquete == null) {
      throw new IllegalArgumentException("El paquete de monedas seleccionado no existe.");
    }

    if (usuario == null || usuario.getId() == null) {
      throw new IllegalArgumentException("No encontramos tu usuario.");
    }
  }
}
