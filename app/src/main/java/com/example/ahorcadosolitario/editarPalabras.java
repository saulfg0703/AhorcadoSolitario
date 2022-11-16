package com.example.ahorcadosolitario;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class editarPalabras extends AppCompatActivity {

    private ListView vista;
    List<Palabra> palabrasMostradas;
    Partida p;
    String[] nombrePalabras;
    String[] descripcionPalabras;
    int posicion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_palabras);

        vista = findViewById(R.id.listViewPalabras);

        palabrasMostradas = new ArrayList<>();

        Intent i = getIntent();
        p = (Partida) i.getSerializableExtra("partida");
        if (p != null) {
            palabrasMostradas = p.getPalabras();
        }
        nombrePalabras = new String[palabrasMostradas.size()];
        descripcionPalabras = new String[palabrasMostradas.size()];

        for (int j = 0; j < palabrasMostradas.size(); j++) {
            nombrePalabras[j] = palabrasMostradas.get(j).getNombrePalabra();
            descripcionPalabras[j] = palabrasMostradas.get(j).getDescripcion();
        }

    actualizarListView();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {

                p = (Partida) data.getSerializableExtra("partidaNueva");
                posicion = data.getIntExtra("posicionNueva", 1);
                actualizarListView();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }
    }

    public void volverAtras(View vista) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("partidaNuevaMain",p);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    public void administrarSQL(View vista) {
        int LAUNCH_SECOND_ACTIVITY = 1;
        Intent i = new Intent(getApplicationContext(), FormularioPalabras.class);
        i.putExtra("partida", p);
        i.putExtra("posicion", -1);
        startActivityForResult(i, LAUNCH_SECOND_ACTIVITY);
    }
    public void actualizarListView(){
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, p.getPalabras());
        vista.setAdapter(adapter);
        vista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                int LAUNCH_SECOND_ACTIVITY = 1;
                Intent i = new Intent(getApplicationContext(), FormularioPalabras.class);
                i.putExtra("partida", p);
                i.putExtra("posicion", position);
                startActivityForResult(i, LAUNCH_SECOND_ACTIVITY);
            }
        });
    }
    public void borrarTodas(View vista){
        palabrasMostradas.clear();
        actualizarListView();

    }

}