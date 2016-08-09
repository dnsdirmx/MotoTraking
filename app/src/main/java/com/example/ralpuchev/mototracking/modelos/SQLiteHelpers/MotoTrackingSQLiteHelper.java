package com.example.ralpuchev.mototracking.modelos.SQLiteHelpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by willo on 14/07/2016.
 */
public class MotoTrackingSQLiteHelper extends SQLiteOpenHelper {
    private String createTableUsuario = "CREATE TABLE 'usuario' " +
            "('id' INTEGER," +
            " 'nombre' VARCHAR, " +
            "'password' TEXT , " +
            "'token' TEXT," +
            " 'remember' INTEGER," +
            " 'logeado' INTEGER NOT NULL)";
    String createTableCliente = "CREATE TABLE 'Cliente' ('idCliente' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'idUbicacion' INTEGER NOT NULL, 'nombre' VARCHAR NOT NULL, 'telefono' VARCHAR NOT NULL, 'direccion' TEXT NOT NULL, 'referencia' TEXT NOT NULL, 'latitud' VARCHAR NOT NULL, 'longitud' VARCHAR NOT NULL)";
    String createTableEntregas = "CREATE TABLE 'Entrega' ('idEntrega' INTEGER PRIMARY KEY NOT NULL, 'descripcion' TEXT NOT NULL, 'idClienteEntrega' INTEGER NOT NULL, 'idClienteRecibe' INTEGER NOT NULL, 'paga' INTEGER NOT NULL, 'tiempoAprox' VARCHAR NOT NULL, 'distanciaAprox' VARCHAR NOT NULL, 'costo' FLOAT NOT NULL, 'status' INTEGER NOT NULL, 'recibido' DATETIME NOT NULL,'idUsuario' INTEGER NOT NULL,'nombreRecibio' VARCHAR,'imgFrente' TEXT,'imgAtras' TEXT)";

    public MotoTrackingSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(createTableUsuario);
        Log.d("bd", "tabla " + createTableUsuario);

        db.execSQL(createTableCliente);
        Log.d("bd", "tabla " + createTableCliente);

        db.execSQL(createTableEntregas);
        Log.d("bd", "tabla " + createTableEntregas);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
