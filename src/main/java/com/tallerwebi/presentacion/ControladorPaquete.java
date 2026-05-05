package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.album.Figurita;
import com.tallerwebi.dominio.album.PaqueteServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ControladorPaquete {

    private PaqueteServicio paqueteServicio;

    @Autowired
    public ControladorPaquete(PaqueteServicio paqueteServicio) {
        this.paqueteServicio = paqueteServicio;
    }

    @RequestMapping(path = "/abrir-paquete", method = RequestMethod.GET)
    public ModelAndView abrirUnPaquete(RedirectAttributes redirectAttributes) {

        try {
            List<Figurita> figuritasNuevas = paqueteServicio.abrirPaquete();

            redirectAttributes.addFlashAttribute("figuritasNuevas", figuritasNuevas);

            // Post-it, para que el html despliegue el modal con las figuritas nuevas
            redirectAttributes.addFlashAttribute("paqueteAbierto", true);

            return new ModelAndView("redirect:/inventario");

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute("error", "Hubo un problema al abrir el sobre. Intentá ed nuevo");
            return new ModelAndView("redirect:/inventario");
        }
    }
}