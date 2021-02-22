package com.example.cu1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class Principal extends AppCompatActivity {
    private ImageButton AccederCamara;
    private ConexionAPI conexionAPI;
    public static final String accesocamara = "Acceder_a_camara";
    public static final int BITMAP_REQUEST = 1;

    //Permisos
    private final String [] permisos = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION}; //Aniadir permisos para gps luego
    private final int CodPermisos = 204;

    //Prueba de imagenes
    private ImageView imagenpruebas;
    private LinearLayout linearLayout;
    private int contadorImagenes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        AccederCamara = findViewById(R.id.AccederCamara);
        conexionAPI = MainActivity.conexionAPI;
        //imagenpruebas = findViewById(R.id.imageView);
        linearLayout = findViewById(R.id.layout);

        //Permisos
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //Tenemos los permisos

        }
        else{
            //No tenemos permisos hay que pedirlos

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(permisos, CodPermisos);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case CodPermisos:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(grantResults[1] == PackageManager.PERMISSION_GRANTED){
                        //Continuamos con la configuracion

                    }
                    else {
                        Toast.makeText(getBaseContext(), "Necesita permisos del GPS!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                    break;
                }
                else {
                    Toast.makeText(getBaseContext(), "Necesita permisos de la camara!", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

    public void AccederACamara (View view){
            Intent intent = new Intent(this, Camara.class);
            startActivityForResult(intent, BITMAP_REQUEST);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BITMAP_REQUEST) {
            if (resultCode == RESULT_OK) {
                //Guardamos la nueva foto



                Foto foto = new Foto(data.getStringExtra(Camara.RESPUESTA_FOTO), data.getDoubleArrayExtra(Camara.RESPUESTA_COORDENADAS), data.getFloatArrayExtra(Camara.RESPUESTA_BRUJULA));
                InsertarImagen(foto.getFotoBitmap());

                //imagenpruebas.setImageBitmap(foto.getFotoBitmap());



                //GPS
                double [] Coordenadas = foto.getGPS();

                //Brujula
                float [] Brujula = foto.getBrujula();


                Toast.makeText(getBaseContext(), "Lat: " + Coordenadas[0] + " y Long: " +Coordenadas[1], Toast.LENGTH_SHORT).show();
                Toast.makeText(getBaseContext(), "X: "+ Brujula[0] + ", Y: " + Brujula[1] + ", Z: " + Brujula[2], Toast.LENGTH_LONG).show();

            }
        }
    }

    //Con la imagen dada creamos una nueva ImagenView y la insertamos en el lineal_layout
    //para poder verla
    private void InsertarImagen(Bitmap imagen){
        final ImageView nuevaImagen = new ImageView(getBaseContext());
        nuevaImagen.setId(contadorImagenes);
        contadorImagenes += 1;
        nuevaImagen.setImageBitmap(imagen);
/*
Intento de modificar el tamaño fallido, cambia el tamanio de las imagenes directamente en la camara
        LinearLayout.LayoutParams params = (TableRow.LayoutParams) nuevaImagen.getLayoutParams();
        params.width = 200;
        params.height = 200;

        nuevaImagen.setLayoutParams(params);
*/
        nuevaImagen.setPadding(0,10,0,10);

        //Cuando hagamos click en la imagen correspondiente pasaremos a una nueva activity con
        //los datos de la imagen correspondiente
        nuevaImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(), "He sido clickeada, mi id: " + nuevaImagen.getId(),Toast.LENGTH_SHORT).show();
            }
        });



        linearLayout.addView(nuevaImagen);
    }

}