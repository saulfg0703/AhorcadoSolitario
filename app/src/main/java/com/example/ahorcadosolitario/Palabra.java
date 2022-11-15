package com.example.ahorcadosolitario;

import java.io.Serializable;

public class Palabra implements Serializable {
    private String nombrePalabra;
    private String descripcion;

    public Palabra(String nombrePalabra, String descripcion) {
        this.nombrePalabra = nombrePalabra;
        this.descripcion = descripcion;
    }
    public Palabra(){

    }

    public String getNombrePalabra() {
        return nombrePalabra;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setNombrePalabra(String nombrePalabra) {
        this.nombrePalabra = nombrePalabra;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return nombrePalabra;
    }
}