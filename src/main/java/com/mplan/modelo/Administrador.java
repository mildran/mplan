package com.mplan.modelo;

public class Administrador extends Usuario{

    public Administrador(String usuario, String password, String email){
        super(usuario, password, email);
    }

    public String devolverTipo(){
        return "ADMINISTRADOR";
    }

}