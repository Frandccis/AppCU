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
        //datos
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

    private void ClickVerMapa(){
        //Toast.makeText(getBaseContext(), "hola desde mapa", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getBaseContext(), ActivityFotoyMapa.class);

        intent.putExtra(Principal.LAT, getIntent().getDoubleExtra(Principal.LAT, 0));
        intent.putExtra(Principal.LONG, getIntent().getDoubleExtra(Principal.LONG, 0));

        startActivity(intent);
    }

    private void ClickBorrar(){
        //Toast.makeText(getBaseContext(), "hola desde borrar", Toast.LENGTH_SHORT).show();
        Intent respuesta = new Intent();

        respuesta.putExtra(Principal.Did, id);

        setResult(RESULT_OK, respuesta);

        finish();
    }

}