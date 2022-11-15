package com.example.ahorcadosolitario;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class FormularioPalabras extends AppCompatActivity {
    List<Palabra> palabrasMostradas;
    EditText palabraSeleccionada;
    EditText descripcionSeleccionada;
    Partida p;
    String[] nombrePalabras;
    String[] descripcionPalabras;
    int posicion = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_palabras);
        palabraSeleccionada = findViewById(R.id.nombrePalabra);
        descripcionSeleccionada = findViewById(R.id.descripcionTextPalabras);
        palabrasMostradas = new ArrayList<>();
        nombrePalabras = new String[palabrasMostradas.size()];
        descripcionPalabras = new String[palabrasMostradas.size()];
        Bundle datos = getIntent().getExtras();
        Intent i = getIntent();
        p = (Partida) i.getSerializableExtra("partida");
        if (p != null) {
            palabrasMostradas = p.getPalabras();
            posicion = datos.getInt("posicion");
        }

        if (posicion != -1) {
            palabraSeleccionada.setText(p.getPalabras().get(posicion).getNombrePalabra());
            descripcionSeleccionada.setText(p.getPalabras().get(posicion).getDescripcion());
        }


    }

    public void volver(View vista) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("partidaNueva",p);
        returnIntent.putExtra("posicionNueva",posicion);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }


    public void borrarPalabra(View vista) {
        palabrasMostradas.remove(posicion);
        palabraSeleccionada.setText("");
        descripcionSeleccionada.setText("");
    }
    public void modificarPalabra(View vista){
        palabrasMostradas.get(posicion).setNombrePalabra(String.valueOf(palabraSeleccionada.getText()));
        palabrasMostradas.get(posicion).setDescripcion(String.valueOf(descripcionSeleccionada.getText()));
    }
}