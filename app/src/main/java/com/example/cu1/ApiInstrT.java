package com.example.cu1;

public interface ApiInstrT {

    //Instrucciones de MainActivity
    void setUsuario(String usuario);
    void setContra(String contra);
    String getUsuario();
    String getContra();
    void IniciarSesion();
    boolean iniciocorrecto(String contrasenia);
    void Registrarse ();
    boolean registrocorrecto(String u, String c);

}
