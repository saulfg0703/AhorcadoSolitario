package com.example.ahorcadosolitario;

import android.provider.BaseColumns;

public class Estructura_Base_De_Datos {
    private Estructura_Base_De_Datos(){

    }
    public static final String NOMBRE_TABLA = "PALABRAS";
    public static final String NOMBRE_COLUMNA1 = "Nombre";
    public static final String NOMBRE_COLUMNA2 = "Descripcion";

    private static final String TIPO_TEXTO = " TEXTO";
    private static final String COMA_SEP = ",";
    protected static final String SQL_CREAR_ENTRADA =
            "CREATE TABLE " + Estructura_Base_De_Datos.NOMBRE_TABLA + " (" +
                    Estructura_Base_De_Datos.NOMBRE_COLUMNA1 + " ID," +
                    Estructura_Base_De_Datos.NOMBRE_COLUMNA2 + TIPO_TEXTO + COMA_SEP + " )";

    protected static final String SQL_BORRAR_ENTRADA =
            "DROP TABLE IF EXISTS " + Estructura_Base_De_Datos.NOMBRE_TABLA;

}
