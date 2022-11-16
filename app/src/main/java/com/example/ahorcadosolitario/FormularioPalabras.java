package com.example.ahorcadosolitario;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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

    public void insertarPalabra(View vista){
        String palabrasLeida = String.valueOf(palabraSeleccionada.getText());
        String palabraDescripcion = String.valueOf(descripcionSeleccionada.getText());
        if (palabrasLeida.equals("")) {
            Toast.makeText(getApplicationContext(), "Introduce una palabra", Toast.LENGTH_LONG).show();
        } else {
            cargarPalabrasUsuario(new Palabra(palabrasLeida,palabraDescripcion));
            palabraSeleccionada.setText("");
            descripcionSeleccionada.setText("");
            Toast.makeText(this, "Se ha insertado la palabra", Toast.LENGTH_SHORT).show();
        }
    }
    public void borrarPalabra(View vista) {
        palabrasMostradas.remove(posicion);
        palabraSeleccionada.setText("");
        descripcionSeleccionada.setText("");
        Toast.makeText(this, "Se ha borrado la palabra", Toast.LENGTH_SHORT).show();
    }
    public void modificarPalabra(View vista){
        palabrasMostradas.get(posicion).setNombrePalabra(String.valueOf(palabraSeleccionada.getText()));
        palabrasMostradas.get(posicion).setDescripcion(String.valueOf(descripcionSeleccionada.getText()));
        Toast.makeText(this, "Se ha modificado la palabra", Toast.LENGTH_SHORT).show();
    }
    public void cargarPalabrasUsuario(Palabra palabrasUsuario) {
        palabrasMostradas.add(palabrasUsuario);
    }
    public void insertarSQL(View vista){
// Gets the data repository in write mode
        BBDD_Asistente dbHelper = new BBDD_Asistente(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(Estructura_Base_De_Datos.NOMBRE_COLUMNA1,palabraSeleccionada.getText().toString());
        values.put(Estructura_Base_De_Datos.NOMBRE_COLUMNA2,descripcionSeleccionada.getText().toString());

// Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(Estructura_Base_De_Datos.NOMBRE_TABLA, null, values);

    }
    public void borrarSQL(View vista){
// Define 'where' part of query.
        String selection = FeedEntry.COLUMN_NAME_TITLE + " LIKE ?";
// Specify arguments in placeholder order.
        String[] selectionArgs = { "MyTitle" };
// Issue SQL statement.
        int deletedRows = db.delete(FeedEntry.TABLE_NAME, selection, selectionArgs);

    }
    public void modificarSQL(View vista){

    }
}