package com.tallerwebi.dominio.album;

public class RuletaFiguritas {

//    1 a 60 -> Rareza.COMUN
//    61 a 85 -> Rareza.PLATA
//    86 a 95 -> Rareza.ORO
//    96 a 100 -> Rareza.LEYENDA

    public Rareza calcularRareza(int numeroRandom) {

        if(numeroRandom > 95) {
            return Rareza.LEYENDA;
        } else if(numeroRandom > 85) {
            return Rareza.ORO;
        } else if(numeroRandom > 60) {
            return Rareza.PLATA;
        } else {
            return Rareza.COMUN;
        }







    }
}
