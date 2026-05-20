//package com.tallerwebi.dominio.album;
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.equalTo;
//import static org.mockito.Mockito.*;
//import com.tallerwebi.dominio.Usuario;
//
//import java.lang.reflect.Array;
//import java.util.Arrays;
//import java.util.List;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;;
//
//public class ServicioGestionFiguritasImplTest {
//
//    private ServicioGestionFiguritas servicioGestionFiguritas;
//    private RepositorioRelacionFiguritaUsuario repositorioRelacionMock;
//
//    @BeforeEach
//    public void init() {
//        this.repositorioRelacionMock = mock(RepositorioRelacionFiguritaUsuario.class);
//        this.servicioGestionFiguritas = new ServicioGestionFiguritasImp(this.repositorioRelacionMock);
//    }
//
//    @Test
//    public void siHayFiguritasRepetidasSeDetectan(){
//        //preparacion{
//        Usuario usuario = new Usuario();
//        usuario.setId(1L);
//
//        Figurita figurita = new Figurita();
//        figurita.setId(10L);
//
//        RelacionFiguritaUsuario relacion1 = new RelacionFiguritaUsuario(usuario, figurita);
//        RelacionFiguritaUsuario relacion2 = new RelacionFiguritaUsuario(usuario, figurita);
//
//        when(this.repositorioRelacionMock.buscarPorUsuario(1L)).thenReturn(Arrays.asList(relacion1, relacion2));
//
//        //ejecucion
//        List<RelacionFiguritaUsuario> repetidas = this.servicioGestionFiguritas.obtenerFiguritasRepetidas(1L);
//
//        //validacion
//        assertThat(repetidas.size(), equalTo(1));
//        verify(this.repositorioRelacionMock,times(1)).buscarPorUsuario(1L);
//    }
//}
