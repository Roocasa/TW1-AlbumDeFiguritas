package com.tallerwebi.dominio.album;
import javax.persistence.*;


@Entity
public class Figurita {

    @Id //Clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID incremental
    private Long id;

    private String nombre;
    private String seleccion;
    private Integer score;
    @Enumerated(EnumType.STRING)
    private Rareza rareza;
    private boolean pegada;

    public Figurita() {}

    public Figurita(String nombre, String seleccion, Rareza rareza) {
        this.nombre = nombre;
        this.seleccion = seleccion;
        this.rareza = rareza;
        this.pegada = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void pegar() { this.pegada = true; }

    public boolean isPegada() { return this.pegada; }
}
