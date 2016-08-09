package com.example.ralpuchev.mototracking.modelos;

import android.content.Context;

/**
 * Created by ralpuchev on 14/07/16.
 */
public class BaseDeDatos {
    public static final Integer BD_VERSION = 1;
    public static final String BD_NAME = "mototracking";

    private static BaseDeDatos ourInstance = new BaseDeDatos();
    public  Context context;
    private BaseDeDatos()
    {
        this.context = null;
    }

    public static BaseDeDatos getInstance()
    {
        return ourInstance;
    }
    public static void setContext(Context context)
    {
        ourInstance.context = context;
    }

    public Context getContext() {
        return context;
    }
}
