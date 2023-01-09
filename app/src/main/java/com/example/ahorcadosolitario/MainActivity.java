package com.example.ahorcadosolitario;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView palabrasDisponibles;
    private TextView palabraSeleccionada;
    private TextView intentosRestantes;
    private TextView descripcionPalabra;
    private Button botonResolver;
    private EditText letraElegida;
    private Button otraPalabra;
    private Partida partida;
    private boolean[] palabrasAcertadas;
    private char[] palabra;
    private boolean primeraEjecucion;
    private ArrayList<Palabra> palabrasInicio;
    private BBDD_Asistente dbHelper = new BBDD_Asistente(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //inicializacion vistas
        palabraSeleccionada = findViewById(R.id.palabra);
        intentosRestantes = findViewById(R.id.intentos);
        botonResolver = findViewById(R.id.adivinar);
        letraElegida = findViewById(R.id.letra);
        otraPalabra = findViewById(R.id.nuevo);
        palabrasDisponibles = findViewById(R.id.palabrasDisponibles);
        descripcionPalabra = findViewById(R.id.descripcionPalabra);
        palabrasInicio = new ArrayList<>();

        cargaInicial();
        //se crea la partida
        partida = new Partida(palabrasInicio);

        actualizarPalabras();

        //se realizan acciones de la partida
        mostrarPalabra();
        mostrarDescripcion();
        calcularIntentos(partida.getIntentos());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuVerPalabra:
                Toast.makeText(this,"La palabra actual es: "+partida.getPalabras().get(partida.getPosicion()), Toast.LENGTH_LONG).show();
                return true;
            case R.id.menuAnadirPalabras:
                anadirPalabras();
                return true;
            case R.id.menuMostrarPalabras:
                mostrarPalabrasPartida();
                return true;
            case R.id.menuImportarTXT:
                partida.cargarPalabrasTXT(this);
                Toast.makeText(this, "Palabras importadas a la partida", Toast.LENGTH_SHORT).show();
                actualizarPalabras();
                return true;
            case R.id.menuExportarTXT:
                partida.guardarPalabrasTXT(this);
                Toast.makeText(this, "Palabras exportadas al fichero de texto", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menuImportarSQL:
                importarSQL();
                Toast.makeText(this, "Palabras importadas a la partida", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menuExportarSQL:
                exportarSQL();
                Toast.makeText(this, "Palabras exportadas a la base de datos", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menuImportarObj:
                importarFicheroObjeto();
                Toast.makeText(this, "Palabras importadas a la partida", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menuExportarObj:
                exportarFicheroObjeto();
                Toast.makeText(this, "Palabras exportadas al fichero binario", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menuImportarMongoDB:
                getMongo();
                Toast.makeText(this, "Palabras importadas a la partida", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menuSalirAplicacion:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Metodo que muestra la palabra con la que se esta jugando en su estado actual
     */
    public void mostrarPalabra() {
        palabraSeleccionada.setText(partida.mostrarPalabraPartida());
    }

    /**
     * Metodo que sirve para mostrar el acierto en la palabra o restar intentos
     *
     * @param vista boton adivinar
     */
    public void resolver(View vista) {
        String cadena = partida.resolverPartida(letraElegida);
        if (cadena.equals("")) {//si no se ha elegido ninguna letra
            Toast.makeText(this, "No has introducido ninguna letra", Toast.LENGTH_LONG).show();
        } else {
            letraElegida.setText("");//se ayuda al usuario a que no tenga que borrar la letra para escribir otra

            if (!partida.acertado()) {//solo se ejecutara si no se ha acertado letra
                Toast.makeText(this, "Has fallado", Toast.LENGTH_LONG).show();
                calcularIntentos(partida.getIntentos());
            }

            //se muestra la palabra en su estado actual tras elegir letra
            mostrarPalabra();
            mostrarDescripcion();

            //se comprueba si se ha ganado o se ha perdido la partida
            comprobar();
        }
    }

    /**
     * Metodo para comprobar si se ha ganado o si se ha perdido la partida
     */
    public void comprobar() {
        if (partida.getIntentos()==0) {
            botonResolver.setEnabled(false);//deshabilita el boton al perder
            mostrarDialogo("Has perdido");
        } else {
            if (partida.comprobarPartida()) {
                botonResolver.setEnabled(false);//deshabilita el boton al perder
                mostrarDialogo("Has ganado");
            }
        }
    }

    /**
     * Metodo que muestra el alertdialog al ganar o perder la partida y permite jugar otra
     *
     * @param estadoPartida mensaje si se ha ganado o no
     */
    public void mostrarDialogo(String estadoPartida) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(estadoPartida);
        builder.setMessage(estadoPartida + ", acepta para jugar otra partida");
        builder.setMessage("Has utilizado " + ((partida.getPalabraActual().length/2) - (partida.getIntentos())) + " intentos");
        builder.setPositiveButton("Jugar otra partida", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                otraPartida(null);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Metodo para actualizar el textview de los intentosActualizados a los actuales
     *
     * @param intentosActualizados intentosActualizados a mostrar
     */
    public void calcularIntentos(int intentosActualizados) {
        intentosRestantes.setText(String.valueOf(intentosActualizados));
    }

    /**
     * Metodo para actualizar las palabras disponibles en la partida
     */
    public void actualizarPalabras(){
        palabrasDisponibles.setText("Palabras disponibles: "+partida.getPalabras().size());
    }
    public void mostrarDescripcion(){
        try {
            descripcionPalabra.setText(partida.getPalabras().get(partida.getPosicion()).getDescripcion());
        }catch(IndexOutOfBoundsException ioobe){
            Toast.makeText(this, "No hay palabras cargadas para adivinar.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Metodo para jugar otra partida con una palabra aleatoria
     *
     * @param vista boton que ejecuta el evento
     */
    public void otraPartida(View vista) {
        try {
            if (!botonResolver.isEnabled()) {
                botonResolver.setEnabled(true);//habilita el boton al empezar una partida si estaba deshabilitado
            }
            partida.elegirPalabraPartida();
            mostrarPalabra();
            mostrarDescripcion();
            calcularIntentos(partida.getIntentos());
        }catch(IndexOutOfBoundsException ioobe){
            Toast.makeText(this, "No hay palabras cargadas en el array.", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Metodo para anadir palabras a la partida
     */
    public void anadirPalabras(){
        LinearLayout layout = new LinearLayout(this);
        EditText palabrasIntroducidas = new EditText(this);
        EditText palabrasDescripcion = new EditText(this);
        AlertDialog.Builder builderPalabras = new AlertDialog.Builder(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(palabrasIntroducidas);
        layout.addView(palabrasDescripcion);
        builderPalabras.setView(layout);
        builderPalabras.setTitle("Añadir palabra");
        builderPalabras.setMessage("Introduzca la palabra que quiere añadir y su descripcion");
        builderPalabras.setPositiveButton("Añadir palabra", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String palabrasLeida = palabrasIntroducidas.getText().toString();
                String palabraDescripcion = palabrasDescripcion.getText().toString();
                if (palabrasLeida.equals("")) {
                    Toast.makeText(MainActivity.this, "Introduce una palabra", Toast.LENGTH_LONG).show();
                } else {
                    partida.cargarPalabrasUsuario(new Palabra(palabrasLeida,palabraDescripcion));
                    actualizarPalabras();
                }
            }
        });
        AlertDialog dialogPalabras = builderPalabras.create();
        dialogPalabras.show();
    }

    /**
     * Metodo para mostrar las palabras de la partida en un list view
     */
    public void mostrarPalabrasPartida(){
        int LAUNCH_SECOND_ACTIVITY = 1;
        Intent i = new Intent(getApplicationContext(), editarPalabras.class);
        i.putExtra("partida", partida);
        startActivityForResult(i, LAUNCH_SECOND_ACTIVITY);
    }

    public void cargaInicial(){
        String[]nombrePalabras = {"pantalla","cielo","ordenador"};
        String[]descripcion = {"Dispositivo de salida que representa visualmente la información.","Espacio en el que se mueven los astros.","Maquina encargada de procesar datos."};
        for (int i = 0; i < nombrePalabras.length; i++) {
            palabrasInicio.add(new Palabra(nombrePalabras[i],descripcion[i]));
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == 1) {
                if (resultCode == Activity.RESULT_OK) {
                    partida = (Partida) data.getSerializableExtra("partidaNuevaMain");
                    actualizarPalabras();
                    palabraSeleccionada.setText("");
                    descripcionPalabra.setText("");
                    intentosRestantes.setText("0");

                }
                if (resultCode == Activity.RESULT_CANCELED) {
                    // Write your code if there's no result
                }
            }
    }
    public void importarSQL() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {Estructura_Base_De_Datos.NOMBRE_COLUMNA1, Estructura_Base_De_Datos.NOMBRE_COLUMNA2};
        Cursor cursor = db.query(Estructura_Base_De_Datos.NOMBRE_TABLA,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        while (cursor.moveToNext()) {
            String nombre = cursor.getString((cursor.getColumnIndexOrThrow(Estructura_Base_De_Datos.NOMBRE_COLUMNA1)));
            String descripcion = cursor.getString(cursor.getColumnIndexOrThrow(Estructura_Base_De_Datos.NOMBRE_COLUMNA2));
            partida.getPalabras().add(new Palabra(nombre,descripcion));
            actualizarPalabras();
        }
        cursor.close();
    }
    public void exportarSQL(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        for (Palabra palabras: partida.getPalabras()) {
            values.put(Estructura_Base_De_Datos.NOMBRE_COLUMNA1, String.valueOf(palabras.getNombrePalabra()));
            values.put(Estructura_Base_De_Datos.NOMBRE_COLUMNA2, String.valueOf(palabras.getDescripcion()));
            db.insert(Estructura_Base_De_Datos.NOMBRE_TABLA,null,values);
        }
        actualizarPalabras();
    }
    public void importarFicheroObjeto(){
        FileInputStream fis = null;
        try{
            fis = getApplicationContext().openFileInput("palabras.dat");
            try{
                ObjectInputStream ois = new ObjectInputStream(fis);
                while(true){
                    try{
                        Palabra p = (Palabra) ois.readObject();
                        partida.getPalabras().add(p);
                    }catch(IOException e){
                        break;
                    }catch (ClassNotFoundException cnfe){
                        cnfe.printStackTrace();
                    }
                    actualizarPalabras();
                }
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void exportarFicheroObjeto() {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try{
            fos = getApplicationContext().openFileOutput("palabras.dat", Context.MODE_APPEND);
            try{
                oos = new ObjectOutputStream(fos);
                for (Palabra palabrasEscribir: partida.getPalabras()) {
                    oos.writeObject(palabrasEscribir);
                }

                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    private void getMongo()
    { // mongod --port 27017 --dbpath C:\MongoDB\data\db --bind_ip_all

        class GetMONGO extends AsyncTask<Void, Void, String> {
            //this method will be called before execution
            //you can display a progress bar or something
            //so that user can understand that he should wait
            //as network operation may take some time
            //Document doc;
            protected void onPreExecute() {
                super.onPreExecute();
            }
            //this method will be called after execution
            //so here we are displaying a toast with the json string
            @Override
            protected void onPostExecute(String jsonStr) {
                super.onPostExecute(jsonStr);
                actualizarPalabras();
            }
            //in this method we are fetching the json string
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    String uri = "mongodb://192.168.167.72:27017";
                    MongoClient mongoClient = MongoClients.create(uri);
                    MongoDatabase db = mongoClient.getDatabase("AdivinaPalabras");
                    MongoCollection<Document> collection = db.getCollection("palabras");

                    collection.find().forEach(doc -> {
                        System.out.println(doc.toJson());
                        String nombre = null;
                        String descripcion= null;
                        JSONObject jsonObject = new JSONObject(doc);
                        try {
                            nombre = jsonObject.getString("nombre");
                            descripcion = jsonObject.getString("descripcion");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Palabra temp = new Palabra(nombre,descripcion);
                        partida.getPalabras().add(temp);


                    });

                    return uri;

                } catch (Exception e) {e.printStackTrace();e.getMessage();
                    return null;
                }

            }

        }

        //creating asynctask object and executing it
        GetMONGO getMongo = new GetMONGO();
        getMongo.execute();

    }

}