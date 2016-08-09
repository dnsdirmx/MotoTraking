package com.example.ralpuchev.mototracking.modelos;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.ralpuchev.mototracking.modelos.SQLiteHelpers.MotoTrackingSQLiteHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ralpuchev on 15/07/16.
 */
public class Entrega {

    Integer idEntrega;
    String descripcion;
    Cliente clienteEntrega;
    Cliente clienteRecibe;
    Integer idClientePaga;
    String tiempoAprox;
    String distanciaAprox;
    Float costo;
    Integer status;
    Date recibido;
    Integer idUsuario;
    String nombreRecibio;
    String imgFrente;
    String imgAtras;

    public String getNombreRecibio() {
        return nombreRecibio;
    }

    public void setNombreRecibio(String nombreRecibio) {
        this.nombreRecibio = nombreRecibio;
    }

    public String getImgFrente() {
        return imgFrente;
    }

    public void setImgFrente(String imgFrente) {
        this.imgFrente = imgFrente;
    }

    public String getImgAtras() {
        return imgAtras;
    }

    public void setImgAtras(String imgAtras) {
        this.imgAtras = imgAtras;
    }



    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }



    public Date getRecibido() {
        return recibido;
    }

    public void setRecibido(long recibido) {

        this.recibido = new Date(recibido);
    }

    public Integer getIdClientePaga() {
        return idClientePaga;
    }

    public void setIdClientePaga(Integer idClientePaga) {
        this.idClientePaga = idClientePaga;
    }
    public Integer getIdEntrega() {
        return idEntrega;
    }

    public void setIdEntrega(Integer idEntrega) {
        this.idEntrega = idEntrega;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Cliente getClienteEntrega() {
        return clienteEntrega;
    }

    public void setClienteEntrega(Cliente clienteEntrega) {
        this.clienteEntrega = clienteEntrega;
    }

    public Cliente getClienteRecibe() {
        return clienteRecibe;
    }

    public void setClienteRecibe(Cliente clienteRecibe) {
        this.clienteRecibe = clienteRecibe;
    }

    public String getTiempoAprox() {
        return tiempoAprox;
    }

    public void setTiempoAprox(String tiempoAprox) {
        this.tiempoAprox = tiempoAprox;
    }

    public String getDistanciaAprox() {
        return distanciaAprox;
    }

    public void setDistanciaAprox(String distanciaAprox) {
        this.distanciaAprox = distanciaAprox;
    }

    public Float getCosto() {
        return costo;
    }

    public void setCosto(Float costo) {
        this.costo = costo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }


    public void save()
    {
        String sql;
        String fechaHora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(this.recibido);

        if(this.clienteRecibe == null || this.clienteEntrega == null) {
            Log.d("Save", "cliente recibe o envia vacios");
            return;
        }
        if(this.clienteEntrega.getIdCliente() == -1)
            this.clienteEntrega.save();
        if(this.clienteRecibe.getIdCliente() == -1)
            this.clienteRecibe.save();

        if(Entrega.finById(this.idEntrega) != null)
        {
            sql = "UPDATE 'Entrega'  SET " +
                    " 'descripcion' = '" + this.descripcion + "' ," +
                    " 'idClienteEntrega' = " + this.clienteEntrega.getIdCliente() + " ," +
                    " 'idClienteRecibe' = " + this.clienteRecibe.getIdCliente() + "," +
                    " 'paga' = " + this.idClientePaga + "," +
                    " 'tiempoAprox' = '" + this.tiempoAprox + "' ," +
                    " 'distanciaAprox' = '" + this.distanciaAprox + "' ," +
                    " 'costo' = " + this.costo + " ," +
                    " 'status' = '" + this.status + "'," +
                    " 'recibido' = '" + fechaHora + "'," +
                    " 'idUsuario' = " + idUsuario + "," +
                    " 'nombreRecibio' = '" + this.nombreRecibio + "'," +
                    " 'imgFrente' = '" + this.imgFrente + "'," +
                    " 'imgAtras' = '" + this.imgAtras + "'" +
                    " WHERE 'idEntrega' = " + this.idEntrega;
        }
        else {
            sql = "INSERT INTO 'Entrega' VALUES(" + this.idEntrega + "," +
                    "'" + this.descripcion + "'," +
                    "" + this.clienteEntrega.getIdCliente() + "," +
                    "" + this.clienteRecibe.getIdCliente() + "," +
                    "" + this.idClientePaga + "," +
                    "'" + this.tiempoAprox + "'," +
                    "'" + this.distanciaAprox + "'," +
                    "" + this.costo + "," +
                    "" + this.status + "," +
                    "'" + fechaHora + "'," +
                    "" + this.idUsuario + "," +
                    "'" + this.nombreRecibio + "'," +
                    "'" + this.imgFrente + "'," +
                    "'" + this.imgAtras + "'" +
                    ")";
        }
        Log.d("save", sql);
        MotoTrackingSQLiteHelper esh = new MotoTrackingSQLiteHelper(BaseDeDatos.getInstance().getContext(),BaseDeDatos.BD_NAME,null,BaseDeDatos.BD_VERSION);
        SQLiteDatabase db = esh.getWritableDatabase();
        try {
            if(Entrega.finById(this.idEntrega) == null)
                db.execSQL(sql);
            else
                db.rawQuery(sql,null);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        db.close();
    }
    public static Entrega finById(Integer id)
    {
        Entrega entrega = null;
        String sql = "select * from Entrega where idEntrega = " + id;
        Log.d("Entrega", sql);
        MotoTrackingSQLiteHelper esh = new MotoTrackingSQLiteHelper(BaseDeDatos.getInstance().getContext(),BaseDeDatos.BD_NAME,null,BaseDeDatos.BD_VERSION);
        SQLiteDatabase db = esh.getWritableDatabase();

        Cursor c = db.rawQuery(sql, null);
        if(c.moveToFirst())
        {
            entrega = new Entrega();
            entrega.setIdEntrega(c.getInt(c.getColumnIndex("idEntrega")));
            entrega.setDescripcion(c.getString(c.getColumnIndex("descripcion")));
            entrega.setClienteEntrega(Cliente.findById(c.getInt(c.getColumnIndex("idClienteEntrega"))));
            entrega.setClienteRecibe(Cliente.findById(c.getInt(c.getColumnIndex("idClienteRecibe"))));
            entrega.setIdClientePaga(c.getInt(c.getColumnIndex("paga")));
            entrega.setTiempoAprox(c.getString(c.getColumnIndex("tiempoAprox")));
            entrega.setDistanciaAprox(c.getString(c.getColumnIndex("distanciaAprox")));
            entrega.setCosto(c.getFloat(c.getColumnIndex("costo")));

            entrega.setStatus(c.getInt(c.getColumnIndex("status")));
            //entrega.setRecibido(c.getLong(c.getColumnIndex("recibido")));


            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                entrega.setRecibido(sdf.parse(c.getString(c.getColumnIndex("recibido"))));
            } catch (ParseException e) {
                e.printStackTrace();
            }


            entrega.setIdUsuario(c.getInt(c.getColumnIndex("idUsuario")));

            entrega.setNombreRecibio(c.getString(c.getColumnIndex("nombreRecibio")));
            entrega.setImgFrente(c.getString(c.getColumnIndex("imgFrente")));
            entrega.setImgAtras(c.getString(c.getColumnIndex("imgAtras")));
        }
        db.close();
        return entrega;
    }

    private void setRecibido(Date recibido) {
        this.recibido = recibido;
    }

    public static ArrayList<Entrega> findAll()
    {
        ArrayList<Entrega> lista = new ArrayList<Entrega>();
        String sql = "select * from 'Entrega'";


        Log.d("Entrega", sql);
        MotoTrackingSQLiteHelper esh = new MotoTrackingSQLiteHelper(BaseDeDatos.getInstance().getContext(),BaseDeDatos.BD_NAME,null,BaseDeDatos.BD_VERSION);
        SQLiteDatabase db = esh.getWritableDatabase();

        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext())
        {
            Entrega entrega = new Entrega();
            entrega.setIdEntrega(c.getInt(c.getColumnIndex("idEntrega")));
            entrega.setDescripcion(c.getString(c.getColumnIndex("descripcion")));
            entrega.setClienteEntrega(Cliente.findById(c.getInt(c.getColumnIndex("idClienteEntrega"))));
            entrega.setClienteRecibe(Cliente.findById(c.getInt(c.getColumnIndex("idClienteRecibe"))));
            entrega.setIdClientePaga(c.getInt(c.getColumnIndex("paga")));
            entrega.setTiempoAprox(c.getString(c.getColumnIndex("tiempoAprox")));
            entrega.setDistanciaAprox(c.getString(c.getColumnIndex("distanciaAprox")));
            entrega.setCosto(c.getFloat(c.getColumnIndex("costo")));

            entrega.setStatus(c.getInt(c.getColumnIndex("status")));
            Log.d("entrega", "Entrega: " + entrega.getIdEntrega() + " status: " + entrega.getStatus());
            //entrega.setRecibido(c.getLong(c.getColumnIndex("recibido")));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                entrega.setRecibido(sdf.parse(c.getString(c.getColumnIndex("recibido"))));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            entrega.setIdUsuario(c.getInt(c.getColumnIndex("idUsuario")));

            entrega.setNombreRecibio(c.getString(c.getColumnIndex("nombreRecibio")));
            entrega.setImgFrente(c.getString(c.getColumnIndex("imgFrente")));
            entrega.setImgAtras(c.getString(c.getColumnIndex("imgAtras")));

            lista.add(entrega);
        }
        db.close();
        Log.d("Entrega","Tam lista " + lista.size());
        return lista;
    }

    public void updateStatus(Integer status)
    {
        String sql = "UPDATE 'Entrega'  SET 'status' = " + status + " WHERE idEntrega = " + this.idEntrega;
        this.status = status;
        MotoTrackingSQLiteHelper esh = new MotoTrackingSQLiteHelper(BaseDeDatos.getInstance().getContext(),BaseDeDatos.BD_NAME,null,BaseDeDatos.BD_VERSION);
        SQLiteDatabase db = esh.getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }
}
