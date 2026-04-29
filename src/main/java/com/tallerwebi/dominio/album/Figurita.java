package com.tallerwebi.dominio.album;
import javax.persistence.*;


@Entity
public class Figurita {

    @Id //Clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID incremental
    private Long Id;

    private String nombre;
    private String seleccion;
    private Integer score;
    @Enumerated(EnumType.STRING)
    private Rareza rareza;

    public Figurita(String nombre, Rareza rareza) {
        this.nombre = nombre;
        this.rareza = rareza;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getSeleccion() {
        return seleccion;
    }

    public void setSeleccion(String seleccion) {
        this.seleccion = seleccion;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Rareza getRareza() {
        return rareza;
    }

    public void setRareza(Rareza rareza) {
        this.rareza = rareza;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
