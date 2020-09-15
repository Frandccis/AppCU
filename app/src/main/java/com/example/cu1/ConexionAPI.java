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

    private boolean SePuedeContinuar, SesionIniciada, Registrado;


    public ConexionAPI() {
        this.usuario = "";
        this.contra = "";
        SesionIniciada = false;
        Registrado = false;
        SePuedeContinuar = true;
    }

    public ConexionAPI(String usuario, String contra) {
        this.usuario = usuario;
        this.contra = contra;
        SesionIniciada = false;
        Registrado = false;
        SePuedeContinuar = true;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public void setContra(String contra) {
        this.contra = contra;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getContra() {
        return contra;
    }

    @Override
    public void IniciarSesion() {

        String url = "http://10.0.2.2:5000/InicioSesion";
        String json = "{\"usuario\":\""+usuario+"\"," +
                        "\"contrasenia\":\""+contra+"\"," +
                        "\"tipo\": \"Ini\" }";
        postini(url,json);

    }

    @Override
    public boolean iniciocorrecto(String contrasenia) {
        dormir();
        return SesionIniciada;

    }

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

                if (respuesta.length()  == 4){
                        SesionIniciada = true;
                }
                else SesionIniciada = false;

                SePuedeContinuar = true;
            }

        });


    }

    @Override
    public void Registrarse() {

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

                int i = respuesta.length();

                //Esto queda
                if (respuesta.length()  == 4){
                    Registrado = true;
                }
                else Registrado = false;

                SePuedeContinuar = true;
            }
        });
    }

    @Override
    public boolean registrocorrecto( String u, String c) {
        dormir();
        return Registrado;
    }
}
