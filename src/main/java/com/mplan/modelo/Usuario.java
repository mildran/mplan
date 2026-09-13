package com.mplan.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table (name ="usuarios")
public class Usuario {

    public enum Rol {
        USUARIO,
        ADMINISTRADOR
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String usuario;
    private String password;
    private String email;
    private String nombre;
    private String apellido;
    private boolean aprobado=false;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    public Usuario() {}

    public Usuario(String usuario, String password, String email, Rol rol) {
        this.usuario = usuario;
        this.password = password;
        this.email = email;
        this.rol = rol;
    }

    public Long getId() {
        return id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public boolean isAprobado() {
        return aprobado;
    }
    public void setAprobado(boolean aprobado) {
        this.aprobado = aprobado;
    }
}