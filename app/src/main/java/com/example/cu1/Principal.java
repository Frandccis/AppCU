package com.example.cu1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import androidx.lifecycle.ViewModelProvider;

import java.util.Base64;
import java.util.List;

public class Principal extends AppCompatActivity {
    private ImageButton AccederCamara;
    private ConexionAPI conexionAPI;
    public static final String accesocamara = "Acceder_a_camara";
    public static final int BITMAP_REQUEST = 1;
    public static final int BORRAR_REQUEST = 2;

    private String usuario;
    //Permisos
    private final String [] permisos = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION};
    private final int CodPermisos = 204;

    //Base de datos
    private FotoModelView BBDD;
    private List<FotoBBDD> fotosBBDD;
    private int contadorId;
    Thread thread;
    public Handler handler;

    //Imagenes
    private LinearLayout linearLayout;
    public static final String LAT = "DATO_LATITUD";
    public static final String LONG = "DATO_LONGITUD";
    public static final String DX = "DATO_X";
    public static final String DY = "DATO_Y";
    public static final String DZ = "DATO_Z";
    public static final String Did = "DATO_ID";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        usuario = getIntent().getStringExtra(MainActivity.USUARIO);
        Log.v("Desde el Thread Main", "El usuario es: " + usuario);

        AccederCamara = findViewById(R.id.AccederCamara);

        conexionAPI = MainActivity.conexionAPI;

        //Imagenes
        linearLayout = findViewById(R.id.layout);
        //listaFotos = new ArrayList<Foto>();

        //BBDD
        BBDD = new ViewModelProvider(this).get(FotoModelView.class);


        //hay que ejecutar esto en otro thread que no sea el main
        thread = new Thread(() -> {
            //Log.v("Desde el Thread Equis", "Holiiiiiiiiiiiii");
            fotosBBDD = BBDD.getFotosBBDD(usuario);

            if (fotosBBDD == null)
                Log.v("Desde el Thread X", "La lista esta vacia o no esiste");

        });

        thread.start();

        runOnUiThread(() -> {
            while (fotosBBDD == null){
                //No hacemos nada
            }

            //Ahora tenemos que ver si la lista no esta vacia, recuperar las fotos
            if(!fotosBBDD.isEmpty()){
                contadorId = fotosBBDD.size();
                //Toast.makeText(getBaseContext(), "Hay " + fotosBBDD.size() + " fotos", Toast.LENGTH_LONG).show();
                for(FotoBBDD foto: fotosBBDD){
                    InsertarImagen(foto.getBytesFoto(), foto.getid());
                }
            }
            else{
                contadorId = 0;
                Toast.makeText(getBaseContext(), "Hay " + fotosBBDD.size() + " fotos", Toast.LENGTH_LONG).show();
            }
        });

        //Avisos
        Toast.makeText(getBaseContext(),
                "Recuerda tener activado el GPS para tomar una foto nueva.",
                Toast.LENGTH_LONG).show();

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

                String Sfoto = data.getStringExtra(Camara.RESPUESTA_FOTO);
                double [] Coordenadas = data.getDoubleArrayExtra(Camara.RESPUESTA_COORDENADAS);
                float [] Brujula = data.getFloatArrayExtra(Camara.RESPUESTA_BRUJULA);

                FotoBBDD foto = new FotoBBDD(contadorId,usuario, Sfoto,                      //--------------Cambiar el usuario por el dado
                        Coordenadas[0], Coordenadas[1], Brujula[0], Brujula[1], Brujula[2]);
                contadorId += 1;


                BBDD.insert(foto);
                InsertarImagen(foto.getBytesFoto(), foto.getid());
                fotosBBDD.add(foto);
/*
                Foto foto = new Foto(data.getStringExtra(Camara.RESPUESTA_FOTO), data.getDoubleArrayExtra(Camara.RESPUESTA_COORDENADAS), data.getFloatArrayExtra(Camara.RESPUESTA_BRUJULA));
                InsertarImagen(foto.getFotoBitmap());
                listaFotos.add(foto);
                Toast.makeText(getBaseContext(), "Ahora hay " + listaFotos.size(), Toast.LENGTH_SHORT).show();



                //GPS
                double [] Coordenadas = foto.getGPS();

                //Brujula
                float [] Brujula = foto.getBrujula();


                //Toast.makeText(getBaseContext(), "Lat: " + Coordenadas[0] + " y Long: " +Coordenadas[1], Toast.LENGTH_SHORT).show();
                //Toast.makeText(getBaseContext(), "X: "+ Brujula[0] + ", Y: " + Brujula[1] + ", Z: " + Brujula[2], Toast.LENGTH_LONG).show();
*/
            }
        }
        else if (requestCode == BORRAR_REQUEST){
            if (resultCode == RESULT_OK){
                int id = data.getIntExtra(Did, -1);

                if( id == -1)
                    Toast.makeText(getBaseContext(), "No se ha podido borrar la foto.",
                            Toast.LENGTH_SHORT).show();
                else {
                    //hay que ejecutar esto en otro thread que no sea el main
                    thread = new Thread(() -> {
                        for (FotoBBDD foto: fotosBBDD){
                            if (foto.getid() == id){
                                BBDD.delete(foto);
                                break;
                            }
                        }

                    });

                    thread.start();
                    Toast.makeText(getBaseContext(), "Foto borrada.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    //Con la imagen dada creamos una nueva ImagenView y la insertamos en el lineal_layout
    //para poder verla
    private void InsertarImagenAux(Bitmap imagen, int id){
        final ImageView nuevaImagen = new ImageView(getBaseContext());
        nuevaImagen.setId(id);
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
                FotoBBDD foto = new FotoBBDD(-1, "NULL","NULL",0,0,0,0,0);

                for (FotoBBDD fotoaux : fotosBBDD){
                    if(fotoaux.getid() == nuevaImagen.getId()){
                        foto = fotoaux;
                        break;
                    }
                }

                //Toast.makeText(getBaseContext(), "He sido clickeada, mi id: " + nuevaImagen.getId(),Toast.LENGTH_SHORT).show();
                //Toast.makeText(getBaseContext(), "He sido clickeada, mi Y: " + fotosBBDD.get(nuevaImagen.getId()).getY(),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getBaseContext(), InfoFoto.class);

                intent.putExtra(LAT, foto.getLatitud());
                intent.putExtra(LONG, foto.getLongitud());

                intent.putExtra(DX,foto.getX());
                intent.putExtra(DY,foto.getY());
                intent.putExtra(DZ,foto.getZ());

                intent.putExtra(Did,foto.getid());

                //fotosBBDD.get(nuevaImagen.getId())
                // intent.putExtra("FOTO_EXTRA", );
                //startActivity(intent);
                startActivityForResult(intent, BORRAR_REQUEST);
            }
        });

        linearLayout.addView(nuevaImagen);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void InsertarImagen( String foto, int id) {
        byte[] respuesta = Base64.getDecoder().decode(foto);
        Bitmap bitmapImage = BitmapFactory.decodeByteArray(respuesta, 0, respuesta.length, null);
        InsertarImagenAux(bitmapImage, id);
    }

}