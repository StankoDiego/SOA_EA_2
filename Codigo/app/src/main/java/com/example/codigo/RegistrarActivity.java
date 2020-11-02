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

import java.util.ArrayList;
import java.util.List;

public class RegistrarActivity extends AppCompatActivity {

    private List<TextView> textsViews;
    private List<EditText> editsText;
    private JSONObject paqueteDatos;
    private Handler handlerRegistar;

    private static final String TAG = "REGISTRAR";
    private static final String PROYECTO = "PROYECTO";

    private static final int LONG_MIN_PASS = 8;
    private static final String URI_REGISTRAR = "http://so-unlam.net.ar/api/api/register";
    private static final String PETICION = "POST";
    private static final String HEADER_KEY = "Content-Type";
    private static final String HEADER_VALUE = "application/json";
    private static final String AMBIENTE = "PROD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        textsViews = new ArrayList<TextView>();
        editsText = new ArrayList<EditText>();

        editsText.add((EditText) findViewById(R.id.editTextNombre));
        editsText.add((EditText) findViewById(R.id.editTextApellido));
        editsText.add((EditText) findViewById(R.id.editTextTextDni));
        editsText.add((EditText) findViewById(R.id.editTextEmail));
        editsText.add((EditText) findViewById(R.id.editTextPassword));
        editsText.add((EditText) findViewById(R.id.editTextComision));

        textsViews.add((TextView) findViewById(R.id.textViewNombre));
        textsViews.add((TextView) findViewById(R.id.textViewApellido));
        textsViews.add((TextView) findViewById(R.id.textViewDni));
        textsViews.add((TextView) findViewById(R.id.textViewEmail));
        textsViews.add((TextView) findViewById(R.id.textViewPass));
        textsViews.add((TextView) findViewById(R.id.textViewCom));

        this.handlerRegistar = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle datos = msg.getData();
                try {
                    JSONObject respuesta = new JSONObject(datos.getString("MENSAJE"));
                    String estadoPeticion = respuesta.getString("success");

                    if (estadoPeticion.equals("false")) {
                        Toast.makeText(getApplicationContext(), "Fallo de registro", Toast.LENGTH_SHORT).show();
                        Log.i(PROYECTO + "->" + TAG, "Registro fallido");
                        return;
                    } else {
                        Intent i = new Intent(getBaseContext(), PantallaPrincipal.class);
                        i.putExtra("MENSAJE", datos);
                        Log.i(PROYECTO + "->" + TAG, "Registro exitoso");
                        startActivity(i);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public void eventoRegistrar(View view) {
        if (validarCampos()) return;
        paqueteDatos = new JSONObject();

        try {
            paqueteDatos.put("env", AMBIENTE);
            paqueteDatos.put("name", editsText.get(0).getText().toString());
            paqueteDatos.put("lastname", editsText.get(1).getText().toString());
            paqueteDatos.put("dni", editsText.get(2).getText().toString());
            paqueteDatos.put("email", editsText.get(3).getText().toString());
            paqueteDatos.put("password", editsText.get(4).getText().toString());
            paqueteDatos.put("commission", editsText.get(5).getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (verificarConexion()) {
            Toast.makeText(this, "Conexion: Disponible", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Conexion: No disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.i(PROYECTO + "->" + TAG, "Se va a registrar un usuario");
        HiloConexion hiloRegistrar = new HiloConexion(URI_REGISTRAR, paqueteDatos, handlerRegistar, PETICION, HEADER_KEY, HEADER_VALUE, null, null);
        hiloRegistrar.start();
    }

    private boolean validarCampos() {
        boolean errorDatos = false;

        for (int i = 0; i < editsText.size(); i++) {
            EditText actual = editsText.get(i);
            if (actual.getText().toString().isEmpty()) {
                textsViews.get(i).setText("Completar por favor");
                errorDatos = true;
            } else {
                textsViews.get(i).setText("");
            }
        }

        if (((EditText) findViewById(R.id.editTextPassword)).getText().toString().length() < LONG_MIN_PASS) {
            ((TextView) findViewById(R.id.textViewPass)).setText("Minimo 8 caracteres");
            errorDatos = true;
        }

        if (!((EditText) findViewById(R.id.editTextTextDni)).getText().toString().matches("[0-9]+")) {
            ((TextView) findViewById(R.id.textViewDni)).setText("Solo se admite numeros");
            errorDatos = true;
        }

        if (!((EditText) findViewById(R.id.editTextComision)).getText().toString().matches("[0-9]+")) {
            ((TextView) findViewById(R.id.textViewCom)).setText("Solo se admite numeros");
            errorDatos = true;
        }

        return errorDatos;
    }

    public boolean verificarConexion() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

}

