package com.example.cu1;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ConexionAPI implements ApiInstrT {
    private String usuario;
    private String contra;
    private static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    private OkHttpClient client = new OkHttpClient();

    private boolean SePuedeContinuar, SesionIniciada, Registrado, UsrLibre;


    //Constructor vacio, por temas de sincronizacion es imposible crear una instancia
    // directamente con los datos de usuario y contrasenia
    public ConexionAPI() {
        this.usuario = "";
        this.contra = "";
        SesionIniciada = false;
        Registrado = false;
        SePuedeContinuar = true;
        UsrLibre = false;
    }

    //Establece el nombre de usuario
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    //Establece la contrasenia
    public void setContra(String contra) {
        this.contra = contra;
    }

    //Devuelve el nombre de usuario
    public String getUsuario() {
        return usuario;
    }

    @Override
    //Iniciamos sesion, devuelve true o false dependiendo de la respuesta de la API
    public void IniciarSesion() {

        //Para comunicarnos necesitamos el json con los datos y la URL
        String url = "http://10.0.2.2:5000/InicioSesion";
        String json = "{\"usuario\":\""+usuario+"\"," +
                        "\"contrasenia\":\""+contra+"\"," +
                        "\"tipo\": \"Ini\" }";
        postini(url,json);

    }

    @Override
    //Devuelve True o False dependiendo de la respueta dada por la API,
    //hay que pedirla despues por temas de sincronizacion
    public boolean iniciocorrecto(String contrasenia) {
        dormir();
        return SesionIniciada;

    }

    //Semaforo para la sincronizacion entre la respuestas de la API
    // y los datos devueltos a la MainActivity
    private void dormir() {
        while (!SePuedeContinuar){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void postini(String url, String json) {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        //Parte asincrona
        SePuedeContinuar = false;
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                String mMessage = e.getMessage();
                Log.w("failure Response", mMessage);
                SesionIniciada = false;
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String respuesta =  response.body().string();
                Log.e("RespuestaApi",respuesta);
                //La API nos devolvera true si tanto el usuario como la contrasenia asociada
                //al mismo coinciden, false en otro caso.

                if (respuesta.length()  == 4){
                        SesionIniciada = true;
                }
                else SesionIniciada = false;

                SePuedeContinuar = true;
            }

        });


    }

    @Override
    //Nos registramos, devuelve true o false dependiendo de la respuesta de la API
    public void Registrarse() {
        //Para comunicarnos necesitamos el json con los datos y la URL
        String url = "http://10.0.2.2:5000/InicioSesion";
        String json = "{\"usuario\":\""+usuario+"\"," +
                "\"contrasenia\":\""+contra+"\"," +
                "\"tipo\": \"Reg\" }";
        postreg(url,json);

    }

    private void postreg(String url, String json){
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        //Parte asincrona
        SePuedeContinuar = false;
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                String mMessage = e.getMessage();
                Log.w("failure Response", mMessage);
                Registrado = false;
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String respuesta =  response.body().string();
                Log.e("RespuestaApi",respuesta);

                //Si no existe ningun otro usuario con el mismo nombre, se crea y se le asigna la
                //contrasenia que ha elegido, si funciona sin errores devuelve true, false en
                //caso contrario.
                if (respuesta.length()  == 4){
                    Registrado = true;
                }
                else Registrado = false;

                SePuedeContinuar = true;
            }
        });
    }

    @Override
    //Devuelve True o False dependiendo de la respueta dada por la API,
    //hay que pedirla despues por temas de sincronizacion
    public boolean registrocorrecto( String u, String c) {
        dormir();
        return Registrado;
    }

    //Se comprueba que el usuario introducido para registrarse no existe aun en la BBDD de la API
    public void UsuarioLibre(String user){
        String url = "http://10.0.2.2:5000/ComprobarUsuario";
        String json = "{\"nombre\":\""+user+"\"}";
        postusr(url,json);
    }

    private void postusr(String url, String json) {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        //Parte asincrona
        SePuedeContinuar = false;
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                String mMessage = e.getMessage();
                Log.w("failure Response", mMessage);
                UsrLibre = false;
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String respuesta =  response.body().string();
                Log.e("RespuestaApi",respuesta);

                //Devuelve True si el usuario no se encuentra en la BBDD, es decir, esta libre
                //False si ya esta registrado
                if (respuesta.length()  == 4){
                    UsrLibre = true;
                }
                else UsrLibre = false;

                SePuedeContinuar = true;
            }
        });
    }

    //Devuelve True o False dependiendo de la respueta dada por la API,
    //hay que pedirla despues por temas de sincronizacion
    public boolean UsuarioLibreAux() {
        dormir();
        return UsrLibre;
    }
}
