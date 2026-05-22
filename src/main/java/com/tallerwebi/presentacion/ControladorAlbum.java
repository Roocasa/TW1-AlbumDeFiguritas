package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.album.Pais;
import com.tallerwebi.dominio.album.ServicioPais;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorAlbum {

  private static final List<Integer> NUMEROS_DE_FIGURITAS = crearNumerosDeFiguritas();
  private final ServicioPais servicioPais;

  @Autowired
  public ControladorAlbum(ServicioPais servicioPais) {
    this.servicioPais = servicioPais;
  }

  @RequestMapping(path = "/album", method = RequestMethod.GET)
  public ModelAndView irAAlbum(
    @RequestParam(value = "grupo", required = false) String grupo,
    @RequestParam(value = "pais", required = false) String pais
  ) {
    List<Pais> paises = servicioPais.buscarPaises(grupo, pais);
    Map<String, List<Pais>> paisesPorGrupo = servicioPais.agruparPorGrupo(paises);

    ModelMap modelo = new ModelMap();
    modelo.put("paisesPorGrupo", paisesPorGrupo);
    modelo.put("grupos", servicioPais.listarGrupos());
    modelo.put("grupoSeleccionado", grupo);
    modelo.put("paisBuscado", pais);
    modelo.put("figuritas", NUMEROS_DE_FIGURITAS);

    return new ModelAndView("album", modelo);
  }

  @RequestMapping(path = "/album/pais/{codigo}", method = RequestMethod.GET)
  public ModelAndView verPais(@PathVariable String codigo) {
    Pais pais = servicioPais.buscarPorCodigo(codigo);

    if (pais == null) {
      return new ModelAndView("redirect:/album");
    }

    ModelMap modelo = new ModelMap();
    modelo.put("codigoPais", pais.getCodigo());
    modelo.put("nombrePais", pais.getNombre());
    modelo.put("figuritas", NUMEROS_DE_FIGURITAS);

    return new ModelAndView("album-pais", modelo);
  }

  private static List<Integer> crearNumerosDeFiguritas() {
    List<Integer> numeros = new ArrayList<>();
    for (int numero = 1; numero <= 12; numero++) {
      numeros.add(numero);
    }
    return numeros;
  }
}
