package com.example.codigo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class InformarDatos extends AppCompatActivity {

    private static final String PROYECTO = "PROYECTO";
    private static final String TAG = "INFORMAR DATOS";

    private ArrayList<String> eventos;
    private Spinner spinnerDni;

    private LinearLayout table;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informar_datos);

        Log.i(PROYECTO + "->" + TAG, "Mostrando Datos");

        SharedPreferences preferences = getSharedPreferences("EventosRegistrados", MODE_PRIVATE);
        Map<String, ?> datos = preferences.getAll();

        this.spinnerDni = (Spinner) findViewById(R.id.spinnerDni);
        this.table = (LinearLayout) findViewById(R.id.table);

        obtenerEventos(datos);
        String[] opciones = obtenerOpciones();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, opciones);
        spinnerDni.setAdapter(adapter);

    }

    private String[] obtenerOpciones() {
        LinkedHashSet<String> aux = new LinkedHashSet<>();
        aux.add("Seleccionar DNI...");
        for (int i = 0; i < this.eventos.size(); i++) {
            aux.add(this.eventos.get(i).split(";")[0]);
        }
        String[] a = new String[aux.size()];
        a = aux.toArray(a);
        return a;
    }

    private void obtenerEventos(Map<String, ?> datos) {
        String[] keys = datos.keySet().toArray(new String[datos.size()]);

        this.eventos = new ArrayList<>();
        for (String index : keys) {
            String aux = (String) datos.get(index);
            this.eventos.add(aux);
        }
    }

    public void listarDatos(View view) {
        String itemSeleccionado = this.spinnerDni.getSelectedItem().toString();
        List<String> elemetrosFiltrados = extraerElementosFiltrados(itemSeleccionado);
        table.removeAllViews();
        if (elemetrosFiltrados.size() == 0) return;

        for (int cantFilas = 0; cantFilas < elemetrosFiltrados.size(); cantFilas++) {
            LinearLayout fila = new LinearLayout(getBaseContext());
            fila.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 300));

            TextView textDni = new TextView(getBaseContext());
            textDni.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 100));
            textDni.setTextColor(Color.WHITE);
            textDni.setText("DNI: " + itemSeleccionado);

            TextView textInfo = new TextView(getBaseContext());
            textInfo.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 100));
            textInfo.setTextColor(Color.WHITE);
            textInfo.setText("DATOS: " + elemetrosFiltrados.get(cantFilas));

            fila.setOrientation(LinearLayout.VERTICAL);
            fila.addView(textDni);
            fila.addView(textInfo);

            table.addView(fila);
        }
    }

    private List<String> recortarElemento(List<String> elemetrosFiltrados, int i) {
        List<String> aux = new ArrayList<>();
        for (String index : elemetrosFiltrados) {
            aux.add(index.split(";")[i]);
        }
        return aux;
    }

    private List<String> extraerElementosFiltrados(String itemSeleccionado) {
        List<String> listAux = new ArrayList<>();
        if (itemSeleccionado.equals("Seleccionar DNI...")) return listAux;

        for (int i = 0; i < this.eventos.size(); i++) {
            String aux = this.eventos.get(i).split(";")[1];
            if (this.eventos.get(i).split(";")[0].equals(itemSeleccionado)) {
                listAux.add(aux);
            }
        }
        return listAux;
    }
}