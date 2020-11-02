package com.example.codigo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private EditText editTextUsuario;
    private EditText editTextContraseña;

    private TextView textViewUsuario;
    private TextView textViewContraseña;

    private JSONObject paqueteDatos;
    private Handler handlerMain;
    private static final String URI_LOGIN = "http://so-unlam.net.ar/api/api/login";
    private static final String TAG = "MAIN";
    private static final String PROYECTO = "PROYECTO";
    private static final String PETICION = "POST";
    private static final String HEADER_KEY = "Content-Type";
    private static final String HEADER_VALUE = "application/json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextUsuario = (EditText) findViewById(R.id.editTextEmail);
        editTextContraseña = (EditText) findViewById(R.id.editTextContraseña);

        textViewUsuario = (TextView) findViewById(R.id.textViewEmail);
        textViewContraseña = (TextView) findViewById(R.id.textViewContraseña);

        paqueteDatos = new JSONObject();


        this.handlerMain = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle datos = msg.getData();
                try {
                    JSONObject respuesta = new JSONObject(datos.getString("MENSAJE"));
                    String estadoPeticion = respuesta.getString("success");

                    if (estadoPeticion.equals("false")) {
                        Toast.makeText(getApplicationContext(), "Fallo de credenciales", Toast.LENGTH_SHORT).show();
                        Log.i(PROYECTO + "->" + TAG, "Login fallido");
                        return;
                    } else {
                        Intent i = new Intent(getBaseContext(), PantallaPrincipal.class);
                        i.putExtra("MENSAJE", datos);
                        Log.i(PROYECTO + "->" + TAG, "Login exitoso");
                        startActivity(i);
                        finish();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public void eventoIngresar(View view) {
        boolean camposVacios = false;

        String usuario = editTextUsuario.getText().toString();
        String pass = editTextContraseña.getText().toString();

        if (usuario.isEmpty()) {
            textViewUsuario.setText("Completar por favor");
            camposVacios = true;
        } else {
            textViewUsuario.setText("");
        }

        if (pass.isEmpty()) {
            textViewContraseña.setText("Completar por favor");
            camposVacios = true;
        } else {
            textViewContraseña.setText("");
        }

        if (camposVacios) return;

        paqueteDatos = new JSONObject();

        try {
            paqueteDatos.put("email", usuario);
            paqueteDatos.put("password", pass);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (verificarConexion()) {
            Toast.makeText(this, "Conexion: Disponible", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Conexion: No disponible", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i(PROYECTO + "->" + TAG, "Se va a loguear un usuario");
        HiloConexion hiloLogin = new HiloConexion(URI_LOGIN, paqueteDatos, handlerMain, PETICION, HEADER_KEY, HEADER_VALUE, null, null);
        hiloLogin.start();
    }

    public void eventoCrearUsuario(View view) {
        Log.i(PROYECTO + "->" + TAG, "Ingresando a pantalla de registro");
        Intent intentRegistrarActivity = new Intent(this, RegistrarActivity.class);
        startActivity(intentRegistrarActivity);
    }

    private boolean verificarConexion() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }
}


