package com.tallerwebi.dominio.album;

public class RuletaFiguritas {

    private static final int MIN_LEYENDA = 96;
    private static final int MIN_ORO = 86;
    private static final int MIN_PLATA = 61;
//    1 a 60 -> Rareza.COMUN
//    61 a 85 -> Rareza.PLATA
//    86 a 95 -> Rareza.ORO
//    96 a 100 -> Rareza.LEYENDA

    public Rareza calcularRareza(int numeroRandom) {

        if(numeroRandom >= MIN_LEYENDA) {
            return Rareza.LEYENDA;
        } else if(numeroRandom >= MIN_ORO) {
            return Rareza.ORO;
        } else if(numeroRandom >= MIN_PLATA) {
            return Rareza.PLATA;
        } else {
            return Rareza.COMUN;
        }
    }
}
