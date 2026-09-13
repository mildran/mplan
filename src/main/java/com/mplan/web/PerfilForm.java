package com.mplan.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class PerfilForm {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no es válido")
    private String email;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    private String nuevaPassword;
    private String confirmarNuevaPassword;

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

    public String getNuevaPassword() {
        return nuevaPassword;
    }

    public void setNuevaPassword(String nuevaPassword) {
        this.nuevaPassword = nuevaPassword;
    }

    public String getConfirmarNuevaPassword() {
        return confirmarNuevaPassword;
    }

    public void setConfirmarNuevaPassword(String confirmarNuevaPassword) {
        this.confirmarNuevaPassword = confirmarNuevaPassword;
    }
}
