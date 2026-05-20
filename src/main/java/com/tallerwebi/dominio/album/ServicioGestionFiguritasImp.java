//package com.tallerwebi.dominio.album;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import javax.transaction.Transactional;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service("servicioGestionFiguritas")
//@Transactional
//public class ServicioGestionFiguritasImp implements ServicioGestionFiguritas{
//
//    private RepositorioRelacionFiguritaUsuario repositorioRelacion;
//
//    @Autowired
//    public ServicioGestionFiguritasImp(RepositorioRelacionFiguritaUsuario repositorioRelacion){
//        this.repositorioRelacion = repositorioRelacion;
//    }
//
//    @Override
//    public List<RelacionFiguritaUsuario> obtenerFiguritasRepetidas(Long usuarioId){
//        List<RelacionFiguritaUsuario> figuritasUsuario =
//            repositorioRelacion.buscarPorUsuario(usuarioId);
//
//        Map<Long, Integer> contadorFiguritas =
//            new HashMap<>();
//
//        List<RelacionFiguritaUsuario> repetidas =
//            new ArrayList<>();
//
//        for (RelacionFiguritaUsuario relacion : figuritasUsuario) {
//
//            Long figuritaId = relacion.getFigurita().getId();
//
//            Integer cantidadActual = contadorFiguritas.get(figuritaId);
//
//            if (cantidadActual == null) {
//                contadorFiguritas.put(figuritaId, 1);
//
//            } else {
//                contadorFiguritas.put(figuritaId, cantidadActual + 1);
//                repetidas.add(relacion);
//            }
//        }
//        return repetidas;
//    }
//}
//
