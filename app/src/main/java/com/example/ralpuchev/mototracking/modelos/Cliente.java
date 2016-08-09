package com.example.ralpuchev.mototracking.modelos;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.example.ralpuchev.mototracking.modelos.SQLiteHelpers.MotoTrackingSQLiteHelper;

/**
 * Created by ralpuchev on 15/07/16.
 */
public class Cliente {
    Integer idCliente = -1; // este no viene desde el servidor
    Integer idUbicacion;
    String nombre;
    String telefono;
    String direccion;
    String referencia;
    String latitud;
    String longitud;

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Integer getIdUbicacion() {
        return idUbicacion;
    }

    public void setIdUbicacion(Integer idUbicacion) {
        this.idUbicacion = idUbicacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public void save()
    {
        String insert = "INSERT INTO Cliente (idUbicacion,nombre,telefono,direccion,referencia,latitud,longitud) " +
                "VALUES(" + this.idUbicacion +"," +
                "'" + this.nombre + "'," +
                "'" + this.telefono + "'," +
                "'" + this.direccion+ "'," +
                "'" + this.referencia + "'," +
                "'" + this.latitud+ "'," +
                "'" + this.longitud +"');";
        Log.d("cliente", insert);
        MotoTrackingSQLiteHelper csh = new MotoTrackingSQLiteHelper(BaseDeDatos.getInstance().getContext(),BaseDeDatos.BD_NAME,null,BaseDeDatos.BD_VERSION);
        SQLiteDatabase db = csh.getWritableDatabase();

        db.execSQL(insert);
        //obtener el id de el cliente que se guardo
        String select = "select idCliente from  Cliente where idUbicacion = " + this.idUbicacion +" AND " +
                "nombre = '" + this.nombre + "'   AND  " +
                "telefono = '" + this.telefono + "'  AND  " +
                "direccion='" + this.direccion+ "'  AND  " +
                "referencia='" + this.referencia + "'  AND  " +
                "latitud= '" + this.latitud+ "'  AND  " +
                "longitud= '" + this.longitud +"'" ;
        Cursor c = db.rawQuery(select,null);
        if(c.moveToFirst())
        {
            this.setIdCliente(c.getInt(c.getColumnIndex("idCliente")));
        }
        db.close();
    }

    public static Cliente findById(Integer id)
    {
        Cliente cliente  = null;
        String sql = "select * from Cliente where idCliente = " + id;

        MotoTrackingSQLiteHelper csh = new MotoTrackingSQLiteHelper(BaseDeDatos.getInstance().getContext(),BaseDeDatos.BD_NAME,null,BaseDeDatos.BD_VERSION);
        SQLiteDatabase db = csh.getWritableDatabase();

        Cursor c = db.rawQuery(sql,null);
        if(c.moveToFirst())
        {
            cliente = new Cliente();
            cliente.setIdCliente(c.getInt(c.getColumnIndex("idCliente")));
            cliente.setIdUbicacion(c.getInt(c.getColumnIndex("idUbicacion")));
            cliente.setNombre(c.getString(c.getColumnIndex("nombre")));
            cliente.setTelefono(c.getString(c.getColumnIndex("telefono")));
            cliente.setDireccion(c.getString(c.getColumnIndex("direccion")));
            cliente.setReferencia(c.getString(c.getColumnIndex("referencia")));
            cliente.setLatitud(c.getString(c.getColumnIndex("latitud")));
            cliente.setLongitud(c.getString(c.getColumnIndex("longitud")));
        }
        db.close();
        return cliente;
    }
    // 1
    // Abertano Nuñez Guevara
    // 2282184738
    // Calle 5 de Febrero 12, Zona Centro, Centro, 91000 Xalapa Enríquez, Ver., México
    // Preguntar en recepción por él
    // 19.53069178687886
    // -96.91647291183472

}
