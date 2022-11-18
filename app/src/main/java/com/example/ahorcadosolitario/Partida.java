package com.example.ahorcadosolitario;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Partida implements Serializable {

    private int intentos;//variable que gestiona los intentos actuales de la partida
    private ArrayList<Palabra> palabras;
    private ArrayList<Palabra> palabrasNuevas;
    private char[] palabraActual;
    private boolean[] posicionesAcertadas;
    private char letra;
    private int posicion;//posicion en el array list de la palabra con la que se esta jugando


    public Partida(ArrayList<Palabra> pal) {
        //inicializacion arraylist palabras
        palabras = pal;
        //cargarPalabras();
        //inicio primera partida
        elegirPalabraPartida();
    }





    /**
     * Metodo que descubre letras de la palabra, en funcion de la dificultad de la partida
     * facil: 1/3 de las letras + 1
     * Normal: 1/3 de las letras
     * Dificil: 1/3 de las letras -1
     */
    public void descubrirLetras(){
        int cantidadLetras = (palabraActual.length/2);
        int i = 0;
        while(i<cantidadLetras){
            Random aleatorio = new Random();
            int numAleatorio = aleatorio.nextInt(palabraActual.length);
            System.out.println(aleatorio);
            if(!posicionesAcertadas[numAleatorio]){
                posicionesAcertadas[numAleatorio]=true;
                i++;
            }
        }
    }

    /**
     * Metodo para anadir al programa las palabras que ha elegido el usuario
     *
     * @param palabrasUsuario array de cadena de caracteres introducido
     */
    public void cargarPalabrasUsuario(Palabra palabrasUsuario) {
        palabras.add(palabrasUsuario);
    }

    /**
     * Metodo para seleccionar una palabra de forma aleatoria e iniciar una partida
     */
    public void elegirPalabraPartida() {

            posicion = (int) (Math.random() * palabras.size());//posicion palabra aleatoria
            palabraActual = palabras.get(posicion).getNombrePalabra().toCharArray();//array caracteres con la palabra
            posicionesAcertadas = new boolean[palabraActual.length];//array de booleanos con el tamaño
            Arrays.fill(posicionesAcertadas, false);//array booleanos inicado a false, si hay persistencia no es necesario este metodo
            descubrirLetras();
            intentos = (palabraActual.length / 2);//actualiza el valor de la partida que se va a jugar
            mostrarPalabraPartida();//se muestra la palabra seleccionada

    }


    /**
     * Metodo que devuelve una cadena con el estado actual de la palabra
     */
    public String mostrarPalabraPartida() {
        String palabraMostrar = "";
        for (int i = 0; i < palabraActual.length; i++) {
            if (posicionesAcertadas[i]) {
                palabraMostrar += palabraActual[i] + " ";
            } else {
                palabraMostrar += "_" + " ";
            }
        }
        return palabraMostrar;
    }


    /**
     * Metodo que sirve para mostrar el acierto en la palabra o restar intentos
     */
    public String resolverPartida(EditText letraElegida) {
        String cadenaRetorno = "";
        if (letraElegida.getText().toString().equals("")) {//si no se ha elegido ninguna letra
        } else {
            //lectura de la letra
            cadenaRetorno = letraElegida.getText().toString();//la letra en el edit text, formato cadena
            letra = cadenaRetorno.charAt(0);//la letra elegida en formato char

            if (!acertado()) intentos--;

            //se comprueba si se ha ganado o se ha perdido la partida
            comprobarPartida();
        }
        return cadenaRetorno;
    }

    /**
     * Metodo que pone las posiciones acertadas a true
     *
     * @return retorna si se ha acertado o si se ha fallado
     */
    public boolean acertado() {
        //comprobacion de acierto o fallo
        boolean acertado = false;//se da por hecho que no va a acertar la letra
        for (int i = 0; i < palabraActual.length; i++) {
            if (Character.toLowerCase(letra) == Character.toLowerCase(palabraActual[i])) {//se hace la comparacion no case sensitive
                posicionesAcertadas[i] = true;//se pone la posicion correspondiente a true
                acertado = true;//se indica que se ha acertado la letra
            }
        }
        return acertado;
    }


    /**
     * Metodo para comprobar si se ha ganado o si se ha perdido la partida
     */
    public boolean comprobarPartida() {
        boolean ganado = false;
        if (intentos == 0) {
        } else {
            boolean[] comprobacionGanado = new boolean[posicionesAcertadas.length];
            Arrays.fill(comprobacionGanado, true);
            if (Arrays.equals(comprobacionGanado, posicionesAcertadas)) {
                ganado = true;
            }
        }
        return ganado;
    }
    public void palabrasNuevasFicheroTXT(){
        palabrasNuevas = (ArrayList<Palabra>) palabras.clone();
        palabrasNuevas.add(new Palabra("mascarilla","Es un dispositivo diseñado para proteger, al portador, de la inhalación de sustancias peligrosas"));
        palabrasNuevas.add(new Palabra("cojin","Es una especie de almohada cuadrada y ornamentada rellena con lana"));
        palabrasNuevas.add(new Palabra("gorra","Es un accesorio diseñado y creado para cubrir la cabeza y proteger los ojos de la luz natural"));
        palabrasNuevas.add(new Palabra("patinete","Es un vehículo/juguete que consiste en una plataforma alargada sobre dos ruedas en línea y una barra de dirección"));
    }
    /**
     * Metodo para guardar las palabras de la partida en un fichero de texto
     * @param contexto contexto de main activity
     */
    public void guardarPalabrasTXT(Context contexto) {
        String nombreArchivo = "palabras.txt";
        palabrasNuevasFicheroTXT();
        try (FileOutputStream fos = contexto.openFileOutput(nombreArchivo, Context.MODE_PRIVATE)) {
            FileWriter fw = new FileWriter(fos.getFD());
            for (int i = 0; i < palabrasNuevas.size(); i++) {
                //    fw.write(palabras.get(i).getNombrePalabra() + "," + palabras.get(i).getDescripcion() + "\n");
                fw.write(palabrasNuevas.get(i).getNombrePalabra() + "," + palabrasNuevas.get(i).getDescripcion() + "\n");
            }
            fw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
        }
    }

    /**
     * Metodo para cargar las palabras de la partida desde un fichero de texto
     *
     */
    public void cargarPalabrasTXT(Context contexto) {
        String nombreArchivo = "palabras.txt";
        FileInputStream fis = null;
        try {
            fis = contexto.openFileInput(nombreArchivo);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            palabras.clear();
            String line = reader.readLine();
            while (line != null) {
                String[] palabrasRecibidas = line.split(",");
                palabras.add(new Palabra(palabrasRecibidas[0], palabrasRecibidas[1]));
                line = reader.readLine();
            }
            fis.close();
            Toast.makeText(contexto, "Palabras cargadas del fichero de texto", Toast.LENGTH_SHORT).show();


        }catch (NullPointerException npe){
            Toast.makeText(contexto, "No existe ningun fichero", Toast.LENGTH_SHORT).show();
        }catch (IOException e) {
            e.printStackTrace();
        }

    }



    //getters y setters
    public int getIntentos() {
        return intentos;
    }

    public void setIntentos(int intentos) {
        this.intentos = intentos;
    }

    public boolean[] getPosicionesAcertadas() {
        return posicionesAcertadas;
    }

    public void setPosicionesAcertadas(boolean[] posicionesAcertadas) {
        this.posicionesAcertadas = posicionesAcertadas;
    }

    public char[] getPalabraActual() {
        return palabraActual;
    }

    public void setPalabraActual(char[] palabraActual) {
        this.palabraActual = palabraActual;
    }

    public ArrayList<Palabra> getPalabras() {
        return palabras;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPalabras(ArrayList<Palabra> palabras) {
        this.palabras = palabras;
    }


}
