package com.example.cu1;

public interface ApiInstrT {

    //Instrucciones para la configuracion de la conexion con la API
    // y para la comunicacion con la misma
    void setUsuario(String usuario);
    void setContra(String contra);
    String getUsuario();
    void IniciarSesion();
    boolean iniciocorrecto(String contrasenia);
    void Registrarse ();
    boolean registrocorrecto(String u, String c);

}
