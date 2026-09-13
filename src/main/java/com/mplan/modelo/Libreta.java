package com.mplan.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "libretas")
public class Libreta {

    public enum Tamano {
        A4,
        A5
    }

    public enum Estilo {
        RAYADO,
        BLANCO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Enumerated(EnumType.STRING)
    private Tamano tamano;

    @Enumerated(EnumType.STRING)
    private Estilo estilo;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Libreta() {
    }

    public Libreta(String nombre, Tamano tamano, Estilo estilo, Usuario usuario) {
        this.nombre = nombre;
        this.tamano = tamano;
        this.estilo = estilo;
        this.usuario = usuario;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Tamano getTamano() {
        return tamano;
    }

    public void setTamano(Tamano tamano) {
        this.tamano = tamano;
    }

    public Estilo getEstilo() {
        return estilo;
    }

    public void setEstilo(Estilo estilo) {
        this.estilo = estilo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getEtiqueta() {
        String tamanoTexto = tamano == Tamano.A4 ? "A4" : "A5";
        String estiloTexto = estilo == Estilo.RAYADO ? "Rallada" : "Blanco";
        return tamanoTexto + "-" + estiloTexto;
    }
}
