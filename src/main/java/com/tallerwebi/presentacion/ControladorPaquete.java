package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.album.Figurita;
import com.tallerwebi.dominio.album.PaqueteServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class ControladorPaquete {

    private PaqueteServicio paqueteServicio;

    @Autowired
    public ControladorPaquete(PaqueteServicio paqueteServicio) {
        this.paqueteServicio = paqueteServicio;
    }

    @RequestMapping(path = "/abrir-paquete", method = RequestMethod.GET)
    public ModelAndView abrirUnPaquete() {

        ModelMap modelo = new ModelMap();

        try {

            List<Figurita> figuritasNuevas = paqueteServicio.abrirPaquete();

            // 3. Metemos la lista de figuritas en la caja de cartón, con la etiqueta "figuritas"
            modelo.put("figuritas", figuritasNuevas);

            // 4. Mandamos la caja a la vista que se llama "paquete-abierto.html"
            return new ModelAndView("paquete-abierto", modelo);

        } catch (Exception e) {
            // Mentalidad Senior: Siempre atajar posibles errores por si se cae la base de datos
            modelo.put("error", "Hubo un problema al abrir el sobre. Intentá de nuevo.");
            return new ModelAndView("inventario", modelo); // Lo devolvemos a su inventario con un mensaje
        }
    }
}