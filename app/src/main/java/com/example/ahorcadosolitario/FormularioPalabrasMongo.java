package com.example.ahorcadosolitario;

import static com.mongodb.client.model.Filters.eq;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.mongodb.MongoBulkWriteException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class FormularioPalabrasMongo extends AppCompatActivity {
    private MongoCollection<Document>coleccionPalabras;
    private MongoDatabase db;
    private MongoClient cliente;
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
        setContentView(R.layout.activity_formulario_palabras_mongo);
        getMongo();
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

    private void getMongo() {
        // mongod --port 27017 --dbpath C:\MongoDB\data\db --bind_ip_all

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

            }
            //in this method we are fetching the json string
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    String uri = "mongodb://192.168.56.1";
                    cliente = MongoClients.create(uri);
                    db = cliente.getDatabase("adivinaPalabras");
                    coleccionPalabras = db.getCollection("palabras");

                    coleccionPalabras.find().forEach(doc -> {
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
                        p.getPalabras().add(temp);


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

    public void insertarMongo(View vista) {
        class insertarMONGO extends AsyncTask<Void, Void, String>{
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected String doInBackground(Void... voids) {
            try {
                String uri = "mongodb://192.168.56.1";
                cliente = MongoClients.create(uri);
                db = cliente.getDatabase("adivinaPalabras");
                coleccionPalabras = db.getCollection("palabras");

                Document nuevaPalabra = new Document("nombre", String.valueOf(palabraSeleccionada.getText())).append("descripcion", String.valueOf(descripcionSeleccionada.getText()));
                try {
                    coleccionPalabras.insertOne(nuevaPalabra);
                } catch (MongoWriteException e) {
                    e.printStackTrace();
                }
            } finally {

            }
                return null;


        }

    }
    //creating asynctask object and executing it
    insertarMONGO getMongo = new insertarMONGO();
        getMongo.execute();


    }

    public void borrarMongo(View vista) {
        class borrarMONGO extends AsyncTask<Void, Void, String>{
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    String uri = "mongodb://192.168.56.1";
                    cliente = MongoClients.create(uri);
                    db = cliente.getDatabase("adivinaPalabras");
                    coleccionPalabras = db.getCollection("palabras");

                    DeleteResult palabra = coleccionPalabras.deleteOne(Filters.eq("nombre",String.valueOf(palabraSeleccionada.getText())));
                    try {
                        coleccionPalabras.deleteOne(eq("nombre",palabra));
                    } catch (MongoWriteException e) {
                        e.printStackTrace();
                    } catch( Exception ef){
                        ef.printStackTrace();
                    }
                } finally {

                }
                return null;


            }
        }
        //creating asynctask object and executing it
        borrarMONGO getMongo = new borrarMONGO();
        getMongo.execute();


    }

    public void modificarMongo(View vista) {
        class modificarMONGO extends AsyncTask<Void, Void, String>{
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    String uri = "mongodb://192.168.56.1";
                    cliente = MongoClients.create(uri);
                    db = cliente.getDatabase("adivinaPalabras");
                    coleccionPalabras = db.getCollection("palabras");


                    try {
                        UpdateResult resultado = coleccionPalabras.updateOne(Filters.eq("nombre",String.valueOf(palabraSeleccionada.getText())),Updates.set("descripcion",String.valueOf(descripcionSeleccionada.getText())),new UpdateOptions().upsert(false));

                    } catch (MongoWriteException e) {
                        e.printStackTrace();
                    }
                } finally {

                }
                return null;


            }
        }
//creating asynctask object and executing it
        modificarMONGO getMongo = new modificarMONGO();
        getMongo.execute();



}
}





