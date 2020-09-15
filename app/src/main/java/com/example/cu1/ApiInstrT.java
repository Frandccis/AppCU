package com.example.cu1;

public interface ApiInstrT {

    //Instrucciones de MainActivity
    void IniciarSesion(String nombre, String contrasenia);
    boolean iniciocorrecto(String contrasenia);
    void Registrarse (String nombre, String contrasenia);
    boolean registrocorrecto(String u, String c);

}
