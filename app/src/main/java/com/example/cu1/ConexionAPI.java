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

    private boolean SePuedeContinuar;

    public ConexionAPI() {
        usuario = "";
        contra = "";
        SePuedeContinuar = true;
    }

    @Override
    public void IniciarSesion(String nombre, String contrasenia) {
        usuario = nombre;
        contra = contrasenia;

        String url = "http://10.0.2.2:5000/InicioSesion";
        String json = "{\"usuario\":\""+usuario+"\"," +
                        "\"contrasenia\":\""+contra+"\"," +
                        "\"tipo\": \"Ini\" }";
        postini(url,json);

    }

    @Override
    public boolean iniciocorrecto(String contrasenia) {

        return contrasenia == contra;

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
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String respuesta =  response.body().string();
                Log.e("RespuestaApi",respuesta);
                contra = respuesta.substring(3,respuesta.length()-3);
                SePuedeContinuar = true;
            }
        });

    }

    @Override
    public void Registrarse(String nombre, String contrasenia) {
        usuario = nombre;
        contra = contrasenia;

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
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String respuesta =  response.body().string();
                Log.e("RespuestaApi",respuesta);
                SePuedeContinuar = true;

                //Esto queda
            }
        });
    }

    @Override
    public boolean registrocorrecto( String u, String c) {
        return u == usuario && c == contra;
    }
}
