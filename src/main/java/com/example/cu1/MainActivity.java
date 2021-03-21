package com.example.cu1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements MainActivityI {

    //Declaramos los botonesy textos de la activity
    private Button Iniciar, Registrarse;
    private EditText Usuario, Contrasenia;

    //La conexión con la API externa para el inicio y registro
    public  static ConexionAPI conexionAPI;

    //Codigo para pasar el nombre de usuario a la siguiente activity
    public static final String USUARIO = "OBTENER_USUARIO";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Creaccion y configuracion de la activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Iniciar = findViewById(R.id.Ini);
        Registrarse = findViewById(R.id.Reg);
        Usuario = findViewById(R.id.IniUser);
        Contrasenia = findViewById(R.id.IniContrasenia);
        conexionAPI = new ConexionAPI();

    }

    //Se ejecuta al pulsar el boton de iniciar sesion
    public void IniciarSesion(View view){
        //Recogemos los datos escritos por el usuario
        String Nombre = Usuario.getText().toString();
        String Contra = Contrasenia.getText().toString();

        //Y comprobamos si los datos son correctos
        if (InicioCorrecto(Nombre, Contra)){
            //Si iniciamos, avisamos al usuario y procedemos a la siguiente activity
            Toast.makeText(getBaseContext(),"Inicio correcto", Toast.LENGTH_LONG).show();
            PasarALaSiguienteActivity(conexionAPI);
        }
        //En caso contrario, avisamos de que ha habido un error
        else Toast.makeText(getBaseContext(),"Fallo al iniciar sesion", Toast.LENGTH_LONG).show();
    }

    private boolean InicioCorrecto(String nombre, String contra) {
        boolean sol = false;
        //Por seguridad saneamos las entradas
        sol = Sanear(nombre, contra);

        //Y comprobamos con la API que todo esta correcto
        if (sol) {
            conexionAPI.setUsuario(nombre);
            conexionAPI.setContra(contra);

            conexionAPI.IniciarSesion();
            sol = conexionAPI.iniciocorrecto(contra);
        }
        /*
        Hacer return de la variable "sol" si esta la API y el docker funcionando,                   <----------- Leer para usar sin la API
        si se va a realizar una prueba sin la API hacer que devuelva True.
         */
        return sol;
    }

    //Se ejecuta al pulsar el boton de registrarse
    public void Registrarse(View view){

        //Recogemos los datos escritos por el usuario
        String Nombre = Usuario.getText().toString();
        String Contra = Contrasenia.getText().toString();

        //Y comprobamos si los datos son validos
        if (RegistroCorrecto(Nombre,Contra)){
            // Si sale bien, avisamos al usuario de que se ha registrado correctamente
            // y pasamos a la siguiente activity con el usuario recien creado
            Toast.makeText(getBaseContext(),"Te has registrado correctamente", Toast.LENGTH_LONG).show();
            PasarALaSiguienteActivity(conexionAPI);
        }
        //En caso contrario avisamos de que ha habido un error
        else Toast.makeText(getBaseContext(),"Fallo en el registro", Toast.LENGTH_LONG).show();


    }

    private boolean RegistroCorrecto(String nombre, String contra) {
        //Aqui nos encargamos de realizar las diferentes comprobaciones necesarias para
        //que el registro se haga correctamente, en caso contrario mostramos el error
        if (nombre.length() == 0 || contra.length() == 0){
            Toast.makeText(getBaseContext(),"NI EL NOMBRE NI LA CONTRASEÑA DEBEN SER VACÍOS", Toast.LENGTH_LONG).show();
        }
        else{
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

    //Comprueba la disponibilidad del nombre de usuario
    private boolean UsuarioLibre(String nombre) {
        conexionAPI.UsuarioLibre(nombre);
        return conexionAPI.UsuarioLibreAux();
    }

    //Funcion auxiliar encargada de evitar un ataque SQL
    private boolean Sanear(String nombre, String contra) {
        boolean res = false;

        if(nombre.matches("[a-zA-Z0-9]+") && contra.matches("[a-zA-Z0-9]+") )
            res = true;

        return res;
    }

    private void PasarALaSiguienteActivity(ConexionAPI conexionAPI) {
        //Pasamos a la siguiente activity y le pasamos el nombre de usuario que
        // a iniciado sesion o se ha registrado correctamente.
        Intent intent = new Intent(this, Principal.class);
        intent.putExtra(USUARIO, conexionAPI.getUsuario());
        startActivity(intent);
    }
}