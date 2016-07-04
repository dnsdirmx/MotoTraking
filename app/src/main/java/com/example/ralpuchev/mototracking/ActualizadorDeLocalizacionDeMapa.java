package com.example.ralpuchev.mototracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.maps.GoogleMap;

/**
 * Created by ralpuchev on 04/04/16.
 */
public class ActualizadorDeLocalizacionDeMapa extends BroadcastReceiver {

    private Inicio inicio = null;

    public void setActividadPadre(Inicio inicio){
        this.inicio = inicio;
    }

    public void iniciaEscucha(){
        //nos registramos en el broadcast manager para recibir notificaciones cuando se actualice la localizacion y entonces debemos mover el mapa (camera)
        // The filter's action is BROADCAST_ACTION
        IntentFilter mStatusIntentFilter = new IntentFilter("com.example.ralpuchev.mototracking.BROADCAST");
        // Registers the DownloadStateReceiver and its intent filters
        LocalBroadcastManager.getInstance(this.inicio).registerReceiver(this, mStatusIntentFilter);
        System.out.println("Esperando actualizaciones de localizacion para el mapa");
    }

    // Called when the BroadcastReceiver gets an Intent it's registered to receive
    @Override
    public void onReceive(Context context, Intent intent) {
        String localizacion = intent.getStringExtra(ActualizadorDeLocalizacion.LOCALIZACION);
        System.out.println("Debemos centrar el mapa en la localizacion: " + localizacion);
        String[] partes_localizacion = localizacion.split("<>");

        inicio.centraMapa(partes_localizacion[0], partes_localizacion[1]);
    }
}
