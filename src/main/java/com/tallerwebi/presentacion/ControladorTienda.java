package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioMercadoPago;
import com.tallerwebi.dominio.ServicioPerfil;
import com.tallerwebi.dominio.Usuario;
import java.util.HashSet;
import java.util.Set;
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
  private static final String FLASH_CONFETTI_PAGO = "mostrarConfettiPago";
  private static final String ESTADO_APROBADO = "approved";
  private static final String PAGOS_ACREDITADOS = "PAGOS_MERCADO_PAGO_ACREDITADOS";
  private static final String PAQUETE_PENDIENTE = "PAQUETE_MERCADO_PAGO_PENDIENTE";
  private static final int PARTES_REFERENCIA_MERCADO_PAGO = 2;

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
      session.setAttribute(PAQUETE_PENDIENTE, paquete);
      return new ModelAndView("redirect:" + urlDePago);
    } catch (IllegalArgumentException | IllegalStateException e) {
      ra.addFlashAttribute(FLASH_ERROR, e.getMessage());
    }

    return new ModelAndView(REDIRECT_TIENDA);
  }

  @RequestMapping(path = "/mercado-pago/acreditar-pendiente", method = RequestMethod.POST)
  public ModelAndView acreditarPagoPendiente(
    @RequestParam(value = "paquete", required = false) String paquete,
    HttpSession session,
    RedirectAttributes ra
  ) {
    Usuario usuario = (Usuario) session.getAttribute(ATRIBUTO_USUARIO);

    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    try {
      String codigoPaquete = obtenerPaquetePendiente(paquete, session);
      if (!servicioMercadoPago.existePagoAprobado(usuario.getId(), codigoPaquete)) {
        ra.addFlashAttribute(FLASH_ERROR, "Todavia no encontramos un pago aprobado.");
        return new ModelAndView(REDIRECT_TIENDA);
      }

      Usuario usuarioActualizado = servicioPerfil.comprarMonedas(usuario.getId(), codigoPaquete);
      session.setAttribute(ATRIBUTO_USUARIO, usuarioActualizado);
      session.removeAttribute(PAQUETE_PENDIENTE);
      ra.addFlashAttribute("mensajeSobre", "Pago aprobado: se acreditaron tus monedas.");
      ra.addFlashAttribute(FLASH_CONFETTI_PAGO, true);
    } catch (IllegalArgumentException | IllegalStateException excepcion) {
      ra.addFlashAttribute(FLASH_ERROR, excepcion.getMessage());
    }

    return new ModelAndView(REDIRECT_TIENDA);
  }

  @RequestMapping(path = "/mercado-pago/retorno", method = RequestMethod.GET)
  @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
  public ModelAndView retornoMercadoPago(
    @RequestParam(value = "status", required = false) String status,
    @RequestParam(value = "collection_status", required = false) String collectionStatus,
    @RequestParam(value = "external_reference", required = false) String referencia,
    @RequestParam(value = "paquete", required = false) String paqueteRetorno,
    @RequestParam(value = "payment_id", required = false) String paymentId,
    @RequestParam(value = "collection_id", required = false) String collectionId,
    HttpSession session,
    RedirectAttributes ra
  ) {
    Usuario usuario = (Usuario) session.getAttribute(ATRIBUTO_USUARIO);

    if (!pagoEstaAprobado(status, collectionStatus)) {
      ra.addFlashAttribute(FLASH_ERROR, "El pago no fue aprobado por Mercado Pago.");
      return redirigirATienda();
    }

    try {
      if (pagoYaFueAcreditado(session, paymentId, collectionId)) {
        ra.addFlashAttribute("mensajeSobre", "Ese pago ya habia sido acreditado.");
        return redirigirATienda();
      }

      Long idUsuario = obtenerIdUsuarioParaAcreditar(usuario, referencia);
      String paquete = obtenerPaqueteParaAcreditar(referencia, paqueteRetorno, idUsuario);
      Usuario usuarioActualizado = servicioPerfil.comprarMonedas(idUsuario, paquete);
      if (usuario != null) {
        session.setAttribute(ATRIBUTO_USUARIO, usuarioActualizado);
      }
      registrarPagoAcreditado(session, paymentId, collectionId);
      ra.addFlashAttribute("mensajeSobre", "Pago aprobado: se acreditaron tus monedas.");
      ra.addFlashAttribute(FLASH_CONFETTI_PAGO, true);
    } catch (IllegalArgumentException e) {
      ra.addFlashAttribute(FLASH_ERROR, e.getMessage());
    }

    return redirigirATienda();
  }

  private String obtenerPaqueteDesdeReferencia(String referencia, Long idUsuario) {
    if (referencia == null || referencia.trim().isEmpty()) {
      throw new IllegalArgumentException("No pudimos identificar la compra de Mercado Pago.");
    }

    String[] partes = referencia.split(":", 2);
    if (
      partes.length != PARTES_REFERENCIA_MERCADO_PAGO ||
      !String.valueOf(idUsuario).equals(partes[0])
    ) {
      throw new IllegalArgumentException("La compra no corresponde al usuario actual.");
    }

    return partes[1];
  }

  private Long obtenerIdUsuarioParaAcreditar(Usuario usuario, String referencia) {
    if (usuarioTieneId(usuario)) {
      return usuario.getId();
    }

    try {
      return Long.valueOf(obtenerPartesReferencia(referencia)[0]);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("No pudimos identificar el usuario de Mercado Pago.", e);
    }
  }

  private boolean usuarioTieneId(Usuario usuario) {
    return usuario != null && usuario.getId() != null;
  }

  private String[] obtenerPartesReferencia(String referencia) {
    if (referencia == null || referencia.trim().isEmpty()) {
      throw new IllegalArgumentException("No pudimos identificar el usuario de Mercado Pago.");
    }

    String[] partes = referencia.split(":", PARTES_REFERENCIA_MERCADO_PAGO);
    if (partes.length != PARTES_REFERENCIA_MERCADO_PAGO) {
      throw new IllegalArgumentException("No pudimos identificar el usuario de Mercado Pago.");
    }

    return partes;
  }

  private String obtenerPaqueteParaAcreditar(
    String referencia,
    String paqueteRetorno,
    Long idUsuario
  ) {
    if (referencia != null && !referencia.trim().isEmpty()) {
      return obtenerPaqueteDesdeReferencia(referencia, idUsuario);
    }

    if (paqueteRetorno == null || paqueteRetorno.trim().isEmpty()) {
      throw new IllegalArgumentException("No pudimos identificar la compra de Mercado Pago.");
    }

    return paqueteRetorno.trim();
  }

  private ModelAndView redirigirATienda() {
    if (servicioMercadoPago != null) {
      String urlFinal = servicioMercadoPago.obtenerUrlFinalDespuesDelPago();
      if (urlFinal != null && !urlFinal.trim().isEmpty()) {
        return new ModelAndView("redirect:" + urlFinal.trim());
      }
    }

    return new ModelAndView(REDIRECT_TIENDA);
  }

  private boolean pagoEstaAprobado(String status, String collectionStatus) {
    return (
      ESTADO_APROBADO.equalsIgnoreCase(status) || ESTADO_APROBADO.equalsIgnoreCase(collectionStatus)
    );
  }

  @SuppressWarnings("unchecked")
  private boolean pagoYaFueAcreditado(HttpSession session, String paymentId, String collectionId) {
    String identificadorPago = obtenerIdentificadorPago(paymentId, collectionId);
    if (identificadorPago == null) {
      return false;
    }

    Set<String> pagosAcreditados = (Set<String>) session.getAttribute(PAGOS_ACREDITADOS);
    return pagosAcreditados != null && pagosAcreditados.contains(identificadorPago);
  }

  @SuppressWarnings("unchecked")
  private void registrarPagoAcreditado(HttpSession session, String paymentId, String collectionId) {
    String identificadorPago = obtenerIdentificadorPago(paymentId, collectionId);
    if (identificadorPago == null) {
      return;
    }

    Set<String> pagosAcreditados = (Set<String>) session.getAttribute(PAGOS_ACREDITADOS);
    if (pagosAcreditados == null) {
      pagosAcreditados = new HashSet<>();
      session.setAttribute(PAGOS_ACREDITADOS, pagosAcreditados);
    }

    pagosAcreditados.add(identificadorPago);
  }

  private String obtenerIdentificadorPago(String paymentId, String collectionId) {
    if (paymentId != null && !paymentId.trim().isEmpty()) {
      return paymentId.trim();
    }

    if (collectionId != null && !collectionId.trim().isEmpty()) {
      return collectionId.trim();
    }

    return null;
  }

  private String obtenerPaquetePendiente(String paquete, HttpSession session) {
    if (paquete != null && !paquete.trim().isEmpty()) {
      return paquete.trim();
    }

    String paquetePendiente = (String) session.getAttribute(PAQUETE_PENDIENTE);
    if (paquetePendiente == null || paquetePendiente.trim().isEmpty()) {
      throw new IllegalArgumentException("No encontramos una compra pendiente para acreditar.");
    }

    return paquetePendiente;
  }

  private String obtenerBaseUrl(HttpServletRequest request) {
    StringBuffer url = request.getRequestURL();
    String uri = request.getRequestURI();
    return url.substring(0, url.length() - uri.length()) + request.getContextPath();
  }
}
