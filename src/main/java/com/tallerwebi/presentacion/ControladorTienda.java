package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioMercadoPago;
import com.tallerwebi.dominio.ServicioPerfil;
import com.tallerwebi.dominio.Usuario;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ControladorTienda {

  private static final String ATRIBUTO_USUARIO = "USUARIO";
  private static final String REDIRECT_LOGIN = "redirect:/login";
  private static final String REDIRECT_TIENDA = "redirect:/tienda";
  private static final String FLASH_ERROR = "error";
  private static final String ESTADO_APROBADO = "approved";

  private final ServicioPerfil servicioPerfil;
  private final ServicioMercadoPago servicioMercadoPago;

  @Autowired
  public ControladorTienda(ServicioPerfil servicioPerfil, ServicioMercadoPago servicioMercadoPago) {
    this.servicioPerfil = servicioPerfil;
    this.servicioMercadoPago = servicioMercadoPago;
  }

  public ControladorTienda(ServicioPerfil servicioPerfil) {
    this(servicioPerfil, null);
  }

  @RequestMapping(path = "/tienda", method = RequestMethod.GET)
  public ModelAndView verTienda(HttpSession session) {
    Usuario usuario = (Usuario) session.getAttribute(ATRIBUTO_USUARIO);

    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    ModelAndView mav = new ModelAndView("tienda");
    mav.addObject("costoSobreMonedas", servicioPerfil.obtenerCostoSobreEnMonedas());
    mav.addObject("paquetesMonedas", servicioPerfil.obtenerPaquetesMonedas());
    return mav;
  }

  @RequestMapping(path = "/comprar-monedas", method = RequestMethod.GET)
  public ModelAndView comprarMonedas(
    @RequestParam("paquete") String paquete,
    HttpSession session,
    HttpServletRequest request,
    RedirectAttributes ra
  ) {
    Usuario usuario = (Usuario) session.getAttribute(ATRIBUTO_USUARIO);

    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    try {
      if (servicioMercadoPago == null) {
        throw new IllegalStateException("Mercado Pago no esta configurado.");
      }

      String urlDePago = servicioMercadoPago.crearUrlDePago(
        servicioPerfil.obtenerPaqueteMonedas(paquete),
        usuario,
        obtenerBaseUrl(request)
      );
      return new ModelAndView("redirect:" + urlDePago);
    } catch (IllegalArgumentException | IllegalStateException e) {
      ra.addFlashAttribute(FLASH_ERROR, e.getMessage());
    }

    return new ModelAndView(REDIRECT_TIENDA);
  }

  @RequestMapping(path = "/mercado-pago/retorno", method = RequestMethod.GET)
  public ModelAndView retornoMercadoPago(
    @RequestParam(value = "status", required = false) String status,
    @RequestParam(value = "external_reference", required = false) String referencia,
    HttpSession session,
    RedirectAttributes ra
  ) {
    Usuario usuario = (Usuario) session.getAttribute(ATRIBUTO_USUARIO);

    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    if (!ESTADO_APROBADO.equalsIgnoreCase(status)) {
      ra.addFlashAttribute(FLASH_ERROR, "El pago no fue aprobado por Mercado Pago.");
      return new ModelAndView(REDIRECT_TIENDA);
    }

    try {
      String paquete = obtenerPaqueteDesdeReferencia(referencia, usuario.getId());
      Usuario usuarioActualizado = servicioPerfil.comprarMonedas(usuario.getId(), paquete);
      session.setAttribute(ATRIBUTO_USUARIO, usuarioActualizado);
      ra.addFlashAttribute("mensajeSobre", "Pago aprobado: se acreditaron tus monedas.");
    } catch (IllegalArgumentException e) {
      ra.addFlashAttribute(FLASH_ERROR, e.getMessage());
    }

    return new ModelAndView(REDIRECT_TIENDA);
  }

  private String obtenerPaqueteDesdeReferencia(String referencia, Long idUsuario) {
    if (referencia == null || referencia.trim().isEmpty()) {
      throw new IllegalArgumentException("No pudimos identificar la compra de Mercado Pago.");
    }

    String[] partes = referencia.split(":", 2);
    if (partes.length != 2 || !String.valueOf(idUsuario).equals(partes[0])) {
      throw new IllegalArgumentException("La compra no corresponde al usuario actual.");
    }

    return partes[1];
  }

  private String obtenerBaseUrl(HttpServletRequest request) {
    StringBuffer url = request.getRequestURL();
    String uri = request.getRequestURI();
    return url.substring(0, url.length() - uri.length()) + request.getContextPath();
  }
}
