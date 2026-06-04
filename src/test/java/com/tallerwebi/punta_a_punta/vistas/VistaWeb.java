package com.tallerwebi.punta_a_punta.vistas;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import java.net.MalformedURLException;
import java.net.URL;

public class VistaWeb {

  protected static final String BASE_URL = System.getProperty(
    "e2e.baseUrl",
    "http://localhost:8080/spring"
  );
  protected Page page;

  public VistaWeb(Page page) {
    this.page = page;
  }

  public URL obtenerURLActual() throws MalformedURLException {
    URL url = new URL(page.url());
    return url;
  }

  protected String obtenerTextoDelElemento(String selectorCSS) {
    return this.obtenerElemento(selectorCSS).textContent();
  }

  protected void darClickEnElElemento(String selectorCSS) {
    Locator elemento = this.obtenerElemento(selectorCSS);

    elemento.click(
        new Locator.ClickOptions()
            .setForce(true)
    );
}

  protected void escribirEnElElemento(String selectorCSS, String texto) {
    Locator elemento = this.obtenerElemento(selectorCSS);
    elemento.fill(texto);
    elemento.dispatchEvent("input");
    elemento.dispatchEvent("change");
  }

  protected void seleccionarOpcionEnElemento(String selectorCSS, String valor) {
    this.obtenerElemento(selectorCSS).selectOption(valor);
  }

  protected int contarElementos(String selectorCSS) {
    return (int) this.obtenerElemento(selectorCSS).count();
  }

  protected boolean elElementoEsVisible(String selectorCSS) {
    return this.obtenerElemento(selectorCSS).isVisible();
  }

  protected boolean elElementoContieneLaClase(String selectorCSS, String claseCSS) {
    String clases = this.obtenerElemento(selectorCSS).getAttribute("class");
    return clases != null && clases.contains(claseCSS);
  }

  protected String construirURL(String path) {
    return BASE_URL + path;
  }

  protected Locator obtenerElemento(String selectorCSS) {
    return page.locator(selectorCSS);
  }
}
