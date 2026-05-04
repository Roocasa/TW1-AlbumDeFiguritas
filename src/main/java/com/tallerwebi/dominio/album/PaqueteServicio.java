package com.tallerwebi.dominio.album;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javax.transaction.Transactional;

@Service("paqueteServicio")
@Transactional
public class PaqueteServicio {

    private RepositorioFigurita repositorioFigurita;
    private static final int FIGURITAS_POR_PAQUETE = 5;

    @Autowired
    public PaqueteServicio(RepositorioFigurita repositorioFigurita) {
        this.repositorioFigurita = repositorioFigurita;
    }

    public List<Figurita> abrirPaquete() {

        List<Rareza> rarezasObtenidas = obtenerRarezasDePaquete();
        List<Figurita> figuritasDelPaquete = new ArrayList<>();

        for (Rareza r : rarezasObtenidas){

            Figurita figuritaObtenida = repositorioFigurita.buscarFiguritaAleatoriaPorRareza(r);

            figuritasDelPaquete.add(figuritaObtenida);
        }
        return figuritasDelPaquete;
    }


    private List<Rareza> obtenerRarezasDePaquete() {

        RuletaFiguritas ruleta = new RuletaFiguritas();
        List<Rareza> rarezasObtenidas = new ArrayList<>();
        Rareza rarezaActual;
        int puntosDeRareza;

        for(int i = 0; i < FIGURITAS_POR_PAQUETE; i ++){

            puntosDeRareza = ThreadLocalRandom.current().nextInt(1, 101);
            rarezaActual = ruleta.calcularRareza(puntosDeRareza);
            rarezasObtenidas.add(rarezaActual);

        }
        /* System.out.println(rarezasObtenidas); */
        return rarezasObtenidas;
    }
}
