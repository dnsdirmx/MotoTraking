package com.example.ralpuchev.mototracking.modelos;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.ralpuchev.mototracking.modelos.SQLiteHelpers.MotoTrackingSQLiteHelper;

/**
 * Created by willo on 14/07/2016.
 */
public class Usuario {
    Integer id = -1;
    String nombre;
    String password;
    String token;
    boolean remember;
    boolean logueado;

    public boolean isLogueado() {
        return logueado;
    }

    public void setLogueado(boolean logueado) {
        this.logueado = logueado;
    }



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isRemember() {
        return remember;
    }

    public void setRemember(boolean remember) {
        this.remember = remember;
    }


    public void save()
    {
        logueado = true;
        Integer vRemember;
        if (id == null) id = 0;
        if (token == null) token = "vacio";
        if (remember)
            vRemember = 1;
        else
            vRemember = 0;
        String insert = "INSERT INTO 'usuario' " +
                "(id,nombre,password,token,remember,logeado) " +
                "VALUES(" + id.toString() + "," +
                "'" + nombre + "'," +
                "'" + password + "'," +
                "'" + token + "'," +
                "" + vRemember.toString() + "," +
                "" + (logueado ? 1 : 0) + ");";
        Log.d("usuario", insert);
        MotoTrackingSQLiteHelper ush = new MotoTrackingSQLiteHelper(BaseDeDatos.getInstance().getContext(),BaseDeDatos.BD_NAME,null,BaseDeDatos.BD_VERSION);
        SQLiteDatabase db = ush.getWritableDatabase();
        db.execSQL(insert);
        db.close();
    }
    public static final Usuario getActive()
    {
        String consulta = "select * from usuario where logeado = 1";
        MotoTrackingSQLiteHelper ush = new MotoTrackingSQLiteHelper(BaseDeDatos.getInstance().getContext(),BaseDeDatos.BD_NAME,null,BaseDeDatos.BD_VERSION);
        SQLiteDatabase db = ush.getWritableDatabase();
        Cursor c = db.rawQuery(consulta,null);
        Usuario usuario = null;
        if(c.moveToFirst())
        {
            usuario = new Usuario();
            usuario.setId(c.getInt(c.getColumnIndex("id")));
            usuario.setNombre(c.getString(c.getColumnIndex("nombre")));
            usuario.setPassword(c.getString(c.getColumnIndex("password")));
            usuario.setToken(c.getString(c.getColumnIndex("token")));
            usuario.setRemember(c.getInt(c.getColumnIndex("remember")) == 1 ? true : false);
            usuario.setLogueado(c.getInt(c.getColumnIndex("logeado")) == 1 ? true : false);

        }
        db.close();
        return usuario;
    }
    public static final Usuario getRememberUser(){
        String consulta = "select * from usuario where remember = 1";
        MotoTrackingSQLiteHelper ush = new MotoTrackingSQLiteHelper(BaseDeDatos.getInstance().getContext(),BaseDeDatos.BD_NAME,null,BaseDeDatos.BD_VERSION);
        SQLiteDatabase db = ush.getWritableDatabase();
        Cursor c = db.rawQuery(consulta,null);
        Usuario usuario = null;
        if(c.moveToFirst())
        {
            usuario = new Usuario();
            usuario.setId(c.getInt(c.getColumnIndex("id")));
            usuario.setNombre(c.getString(c.getColumnIndex("nombre")));
            usuario.setPassword(c.getString(c.getColumnIndex("password")));
            usuario.setToken(c.getString(c.getColumnIndex("token")));
            usuario.setRemember(c.getInt(c.getColumnIndex("remember")) == 1 ? true : false);
            usuario.setLogueado(c.getInt(c.getColumnIndex("logeado")) == 1 ? true : false);

        }
        db.close();
        return usuario;
    }
    public void eliminar()
    {
        String elimina = "delete from usuario where id = " + this.id;
        MotoTrackingSQLiteHelper ush = new MotoTrackingSQLiteHelper(BaseDeDatos.getInstance().getContext(),BaseDeDatos.BD_NAME,null,BaseDeDatos.BD_VERSION);

        SQLiteDatabase db = ush.getWritableDatabase();

        db.execSQL(elimina);
        db.close();
    }

    public void signOut()
    {
        String sql = "UPDATE 'usuario'  SET  'logeado' = " + 0 + " where id =  " + this.id;
        MotoTrackingSQLiteHelper ush = new MotoTrackingSQLiteHelper(BaseDeDatos.getInstance().getContext(),BaseDeDatos.BD_NAME,null,BaseDeDatos.BD_VERSION);
        SQLiteDatabase db = ush.getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }
    public void signIn()
    {
        String sql = "UPDATE 'usuario'  SET  'logeado' = " + 1 + " where id =  " + this.id;
        MotoTrackingSQLiteHelper ush = new MotoTrackingSQLiteHelper(BaseDeDatos.getInstance().getContext(),BaseDeDatos.BD_NAME,null,BaseDeDatos.BD_VERSION);
        SQLiteDatabase db = ush.getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }
}
