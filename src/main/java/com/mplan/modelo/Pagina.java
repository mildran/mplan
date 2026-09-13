package com.mplan.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "paginas")
public class Pagina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "libreta_id")
    private Libreta libreta;

    private int orden;

    @Column(columnDefinition = "TEXT")
    private String contenido;

    public Pagina() {
    }

    public Pagina(Libreta libreta, int orden, String contenido) {
        this.libreta = libreta;
        this.orden = orden;
        this.contenido = contenido;
    }

    public Long getId() {
        return id;
    }

    public Libreta getLibreta() {
        return libreta;
    }

    public void setLibreta(Libreta libreta) {
        this.libreta = libreta;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
}
