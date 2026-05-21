package com.tallerwebi.presentacion;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorAlbum {

  private static final Map<String, String> PAISES = crearPaises();
  private static final List<Integer> NUMEROS_DE_FIGURITAS = crearNumerosDeFiguritas();

  @RequestMapping(path = "/album", method = RequestMethod.GET)
  public ModelAndView irAAlbum() {
    return new ModelAndView("album");
  }

  @RequestMapping(path = "/album/pais/{codigo}", method = RequestMethod.GET)
  public ModelAndView verPais(@PathVariable String codigo) {
    String codigoNormalizado = codigo.toUpperCase(Locale.ROOT);
    String nombrePais = PAISES.get(codigoNormalizado);

    if (nombrePais == null) {
      return new ModelAndView("redirect:/album");
    }

    ModelMap modelo = new ModelMap();
    modelo.put("codigoPais", codigoNormalizado);
    modelo.put("nombrePais", nombrePais);
    modelo.put("figuritas", NUMEROS_DE_FIGURITAS);

    return new ModelAndView("album-pais", modelo);
  }

  private static Map<String, String> crearPaises() {
    Map<String, String> paises = new LinkedHashMap<>();
    paises.put("MEX", "Mexico");
    paises.put("CZE", "Republica Checa");
    paises.put("RSA", "Sudafrica");
    paises.put("KOR", "Corea del Sur");
    paises.put("CAN", "Canada");
    paises.put("BIH", "Bosnia y Herzegovina");
    paises.put("QAT", "Qatar");
    paises.put("SUI", "Suiza");
    paises.put("BRA", "Brasil");
    paises.put("HAI", "Haiti");
    paises.put("MAR", "Marruecos");
    paises.put("SCO", "Escocia");
    paises.put("USA", "Estados Unidos");
    paises.put("AUS", "Australia");
    paises.put("PAR", "Paraguay");
    paises.put("TUR", "Turquia");
    paises.put("CUW", "Curazao");
    paises.put("ECU", "Ecuador");
    paises.put("GER", "Alemania");
    paises.put("CIV", "Costa de Marfil");
    paises.put("NED", "Paises Bajos");
    paises.put("JPN", "Japon");
    paises.put("SWE", "Suecia");
    paises.put("TUN", "Tunez");
    paises.put("BEL", "Belgica");
    paises.put("EGY", "Egipto");
    paises.put("IRN", "Iran");
    paises.put("NZL", "Nueva Zelanda");
    paises.put("CPV", "Cabo Verde");
    paises.put("KSA", "Arabia Saudita");
    paises.put("ESP", "Espana");
    paises.put("URU", "Uruguay");
    paises.put("FRA", "Francia");
    paises.put("NOR", "Noruega");
    paises.put("SEN", "Senegal");
    paises.put("IRQ", "Irak");
    paises.put("ALG", "Argelia");
    paises.put("ARG", "Argentina");
    paises.put("AUT", "Austria");
    paises.put("JOR", "Jordania");
    paises.put("COL", "Colombia");
    paises.put("JAM", "Jamaica");
    paises.put("POR", "Portugal");
    paises.put("UZB", "Uzbekistan");
    paises.put("CRO", "Croacia");
    paises.put("ENG", "Inglaterra");
    paises.put("GHA", "Ghana");
    paises.put("PAN", "Panama");
    return paises;
  }

  private static List<Integer> crearNumerosDeFiguritas() {
    List<Integer> numeros = new ArrayList<>();
    for (int numero = 1; numero <= 12; numero++) {
      numeros.add(numero);
    }
    return numeros;
  }
}
