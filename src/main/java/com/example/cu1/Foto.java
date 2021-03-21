package com.example.cu1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Base64;

public class Foto implements FotoI{

    private String foto;
    private double [] GPS;
    private float [] Brujula;

    public Foto(String foto, double [] GPS, float [] Brujula) {
        this.foto = foto;
        this.GPS = GPS;
        this.Brujula = Brujula;
    }

    @Override
    public String getFoto() {
        return this.foto;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Bitmap getFotoBitmap() {
        byte[] respuesta = Base64.getDecoder().decode(this.foto);
        Bitmap bitmapImage = BitmapFactory.decodeByteArray(respuesta, 0, respuesta.length, null);
        return bitmapImage;
    }

    @Override
    public double[] getGPS() {
        return this.GPS;
    }

    @Override
    public float[] getBrujula() {
        return this.Brujula;
    }
}
