package com.example.cu1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class InfoFoto extends AppCompatActivity {

    //Respuesta para borrar una foto
    boolean borrar_foto;
    public static final String BORRAR_FOTO = "BORRAR_FOTO";

    //XML
    private Button BorrarFoto, VerMapa;
    private TextView dX, dY, dZ, LatV, LongV;

    //Datos
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_foto);

        Configurar();

    }

    private void Configurar() {
        //Recogemos los datos pasados de la activity anterior
        id = getIntent().getIntExtra(Principal.Did, -1);

        //botones
        BorrarFoto = findViewById(R.id.button_borrar_foto);
        VerMapa = findViewById(R.id.button_mapa);

        //textviews
        dX = findViewById(R.id.Xdata);
        dY = findViewById(R.id.Ydata);
        dZ = findViewById(R.id.Zdata);

        LatV = findViewById(R.id.Latdata);
        LongV = findViewById(R.id.Longdata);

        //Actualizamos los textviews
        dX.setText("" + getIntent().getFloatExtra(Principal.DX, 0));
        dY.setText("" + getIntent().getFloatExtra(Principal.DY, 0));
        dZ.setText("" + getIntent().getFloatExtra(Principal.DZ, 0));

        LatV.setText("" + getIntent().getDoubleExtra(Principal.LAT, 0));
        LongV.setText("" + getIntent().getDoubleExtra(Principal.LONG, 0));

        //Funciones botones
        VerMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickVerMapa();
            }
        });
        BorrarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickBorrar();
            }
        });
    }

    //Nos abre una nueva activity donde podemos ver la localizacion en la que se tomo la foto
    private void ClickVerMapa(){
        Intent intent = new Intent(getBaseContext(), ActivityFotoyMapa.class);

        //Pasamos las coordenadas GPS para el mapa
        intent.putExtra(Principal.LAT, getIntent().getDoubleExtra(Principal.LAT, 0));
        intent.putExtra(Principal.LONG, getIntent().getDoubleExtra(Principal.LONG, 0));

        //Iniciamos la activity
        startActivity(intent);
    }

    //Se encarga de mandar la instruccion de borrar la foto en la que nos encontramos
    private void ClickBorrar(){
        Intent respuesta = new Intent();

        respuesta.putExtra(Principal.Did, id);

        setResult(RESULT_OK, respuesta);

        finish();
    }

}