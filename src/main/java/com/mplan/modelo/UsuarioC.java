package com.mplan.modelo;

public class UsuarioC extends Usuario{

public UsuarioC(String usuario, String password, String email){
    super(usuario, password, email);
}

    public String devolverTipo(){
    return "USUARIOC";
}

}


