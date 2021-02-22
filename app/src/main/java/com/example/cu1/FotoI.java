package com.example.cu1;

import android.graphics.Bitmap;

public interface FotoI {
    /*
    Devuelve un String que se encuentra en Base64
    el cual hay que decodificar y pasar a bitmap, para
    ello tenemos una funcion en "Principal" que se llama
    "Decode64toBitmap"
     */
    String getFoto();

    //Devuelve la foto en bitmap listo para usar
    Bitmap getFotoBitmap();

    //Devuelve la Latitud y la Longitud
    double [] getGPS();

    //Devuelve los ejes
    float [] getBrujula();
}
