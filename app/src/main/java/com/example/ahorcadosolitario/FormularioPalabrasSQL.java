package com.example.ahorcadosolitario;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class FormularioPalabrasSQL extends AppCompatActivity {
    List<Palabra> palabrasMostradas;
    EditText palabraSeleccionada;
    EditText descripcionSeleccionada;
    Partida p;
    String[] nombrePalabras;
    String[] descripcionPalabras;
    int posicion = -1;
    BBDD_Asistente dbHelper = new BBDD_Asistente(this);

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
        returnIntent.putExtra("partidaNueva", p);
        returnIntent.putExtra("posicionNueva", posicion);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void insertarPalabra(View vista) {
        String palabrasLeida = String.valueOf(palabraSeleccionada.getText());
        String palabraDescripcion = String.valueOf(descripcionSeleccionada.getText());
        if (palabrasLeida.equals("")) {
            Toast.makeText(getApplicationContext(), "Introduce una palabra", Toast.LENGTH_LONG).show();
        } else {
            cargarPalabrasUsuario(new Palabra(palabrasLeida, palabraDescripcion));
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

    public void modificarPalabra(View vista) {
        palabrasMostradas.get(posicion).setNombrePalabra(String.valueOf(palabraSeleccionada.getText()));
        palabrasMostradas.get(posicion).setDescripcion(String.valueOf(descripcionSeleccionada.getText()));
        Toast.makeText(this, "Se ha modificado la palabra", Toast.LENGTH_SHORT).show();
    }

    public void cargarPalabrasUsuario(Palabra palabrasUsuario) {
        palabrasMostradas.add(palabrasUsuario);
    }

    public void insertarSQL(View vista) {
// Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(Estructura_Base_De_Datos.NOMBRE_COLUMNA1, String.valueOf(palabraSeleccionada.getText()));
        values.put(Estructura_Base_De_Datos.NOMBRE_COLUMNA2, String.valueOf(descripcionSeleccionada.getText()));

// Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(Estructura_Base_De_Datos.NOMBRE_TABLA, null, values);

    }

    public void borrarSQL(View vista) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();// Define 'where' part of query.
        String selection = Estructura_Base_De_Datos.NOMBRE_COLUMNA1 + " LIKE ?";
// Specify arguments in placeholder order.
        String[] selectionArgs = {palabraSeleccionada.getText().toString()};
// Issue SQL statement.
        int deletedRows = db.delete(Estructura_Base_De_Datos.NOMBRE_TABLA, selection, selectionArgs);

    }

    public void modificarSQL(View vista) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();// Define 'where' part of query.
        ContentValues values = new ContentValues();
        values.put(Estructura_Base_De_Datos.NOMBRE_COLUMNA2, descripcionSeleccionada.getText().toString());
        // values.put(Estructura_Base_De_Datos.NOMBRE_COLUMNA2, descripcionSeleccionada.getText().toString());
        String selection = Estructura_Base_De_Datos.NOMBRE_COLUMNA1 + " LIKE ?";
        String[] selectionArgs = {palabraSeleccionada.getText().toString()};
        int count = db.update(Estructura_Base_De_Datos.NOMBRE_TABLA, values, selection, selectionArgs);
    }

    public void buscarSQL(View vista) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {Estructura_Base_De_Datos.NOMBRE_COLUMNA1, Estructura_Base_De_Datos.NOMBRE_COLUMNA2};

// Filter results WHERE "title" = 'My Title'
        String selection = Estructura_Base_De_Datos.NOMBRE_COLUMNA1 + " = ?";
        String[] selectionArgs = {palabraSeleccionada.getText().toString()};

// How you want the results sorted in the resulting Cursor
        String sortOrder = Estructura_Base_De_Datos.NOMBRE_COLUMNA2 + descripcionSeleccionada.getText().toString();

        Cursor cursor = db.query(Estructura_Base_De_Datos.NOMBRE_TABLA,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        while (cursor.moveToNext()) {
            String descripcion = cursor.getString(cursor.getColumnIndexOrThrow(Estructura_Base_De_Datos.NOMBRE_COLUMNA2));
            descripcionSeleccionada.setText(descripcion);
        }
        cursor.close();
    }

}




