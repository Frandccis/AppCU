package com.example.cu1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements MainActivityI {

    private Button Iniciar, Registrarse;
    private EditText Usuario, Contrasenia;
    public  static ConexionAPI conexionAPI;
    public static final String USUARIO = "OBTENER_USUARIO";

                                                                                                    //De momento no se usa
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Iniciar = findViewById(R.id.Ini);
        Registrarse = findViewById(R.id.Reg);
        Usuario = findViewById(R.id.IniUser);
        Contrasenia = findViewById(R.id.IniContrasenia);
        conexionAPI = new ConexionAPI();

    }

    public void IniciarSesion(View view){ //Se usa al pulsar el boton de iniciar sesion
        String Nombre = Usuario.getText().toString();
        String Contra = Contrasenia.getText().toString();
        if (InicioCorrecto(Nombre, Contra)){
            Toast.makeText(getBaseContext(),"Inicio correcto", Toast.LENGTH_LONG).show();
            //pasar a la siguiente activity
            PasarALaSiguienteActivity(conexionAPI);
        }
        else Toast.makeText(getBaseContext(),"Fallo al iniciar sesion", Toast.LENGTH_LONG).show();
    }

    private boolean InicioCorrecto(String nombre, String contra) {
        boolean sol = false;
        sol = Sanear(nombre, contra);

        //Hacer aqui las comprobaciones con la api
        if (sol) {
            conexionAPI.setUsuario(nombre);
            conexionAPI.setContra(contra);

            conexionAPI.IniciarSesion();
            sol = conexionAPI.iniciocorrecto(contra);
        }

        return sol;                                                                                   //Return True si se usa solo sin internet. La variable sol en caso contrario
    }

    public void Registrarse(View view){ //Se usa al pulsar el boton de registarse

        String Nombre = Usuario.getText().toString();
        String Contra = Contrasenia.getText().toString();

        if (RegistroCorrecto(Nombre,Contra)){
            Toast.makeText(getBaseContext(),"Te has registrado correctamente", Toast.LENGTH_LONG).show();
            //Pasar a la siguiente activity
            PasarALaSiguienteActivity(conexionAPI);
        }
        else Toast.makeText(getBaseContext(),"Fallo en el registro", Toast.LENGTH_LONG).show();


    }

    private boolean RegistroCorrecto(String nombre, String contra) {
        if (nombre.length() == 0 || contra.length() == 0){
            Toast.makeText(getBaseContext(),"NI EL NOMBRE NI LA CONTRASEÑA DEBEN SER VACÍOS", Toast.LENGTH_LONG).show();
        }
        else{
            //Sanear
            if (Sanear(nombre,contra)){
                if (UsuarioLibre(nombre)){
                    conexionAPI.setUsuario(nombre);
                    conexionAPI.setContra(contra);
                    conexionAPI.Registrarse();
                    if (conexionAPI.registrocorrecto(nombre,contra))
                        return true;
                }
                else
                    Toast.makeText(getBaseContext(),"Nombre de usuario en uso", Toast.LENGTH_LONG).show();
            }
            else
                Toast.makeText(getBaseContext(),"USA SOLO CARÁCTERES ALFANUMÉRICOS", Toast.LENGTH_LONG).show();
        }

        return false;
    }

    private boolean UsuarioLibre(String nombre) {
        //Comprueba la disponibilidad del nombre de usuario
        conexionAPI.UsuarioLibre(nombre);
        return conexionAPI.UsuarioLibreAux();
    }

    private boolean Sanear(String nombre, String contra) {
        boolean res = false;

        if(nombre.matches("[a-zA-Z0-9]+") && contra.matches("[a-zA-Z0-9]+") )
            res = true;

        return res;
    }

    private void PasarALaSiguienteActivity(ConexionAPI conexionAPI) {
        Intent intent = new Intent(this, Principal.class);
        intent.putExtra(USUARIO, conexionAPI.getUsuario());                                          //Mandar esto a la siguiente activity, sino no sabremos quien es el usuario
        //intent.putExtra(USUARIO, "pepe");
        startActivity(intent);
    }
}