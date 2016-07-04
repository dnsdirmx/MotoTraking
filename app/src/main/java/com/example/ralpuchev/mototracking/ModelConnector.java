package com.example.ralpuchev.mototracking;

import android.os.AsyncTask;

import java.util.ArrayList;

/**
 * Created by ralpuchev on 27/03/16.
 */

public class ModelConnector {
    private static ModelConnector ourInstance = new ModelConnector();
    private static String enlace_motos;
    private static String opciones;
    private static int tarea_actual = 0;
    private static ArrayList<Summoner> summoners = null;

    public static ModelConnector getInstance() {
        return ourInstance;
    }

    private ModelConnector() {
        enlace_motos = "http://sistema.enviaexpress.mx/ms/EnlaceMotosMovil.php";
        opciones = "";
        summoners = new ArrayList<Summoner>();
    }

    public static void setRespuesta(String respuesta, int numero_respuesta){
        summoners.get(numero_respuesta).procesaRespuesta(respuesta);
        summoners.set(numero_respuesta, null);
    }

    public static void setOpciones(String ops){
        opciones = ops;
    }

    public static void setSummoner(Summoner summoner){
        if(summoners == null) summoners = new ArrayList<Summoner>();
        summoners.add(tarea_actual, summoner);
    }

    public static synchronized void getRespuestaDeServidor(String ops, Summoner summoner){
        setOpciones(ops);
        setSummoner(summoner);
        com.example.ralpuchev.mototracking.AsyncTask nueva_tarea = new com.example.ralpuchev.mototracking.AsyncTask();
        nueva_tarea.setNumeroDeRespuesta(tarea_actual);
        tarea_actual++;
        nueva_tarea.execute(getUrl());
    }

    private static String getUrl(){
        return enlace_motos+"?"+opciones;
    }
}
