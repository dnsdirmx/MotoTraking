package com.example.ralpuchev.mototracking;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ralpuchev.mototracking.modelos.BaseDeDatos;
import com.example.ralpuchev.mototracking.modelos.Cliente;
import com.example.ralpuchev.mototracking.modelos.Entrega;
import com.example.ralpuchev.mototracking.modelos.Usuario;
import com.google.android.gms.gcm.GcmListenerService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import java.util.Date;
import java.util.List;


/**
 * Created by ralpuchev on 14/07/16.
 */
public class NotificationsListenerService extends GcmListenerService {
    Integer idNotificacion = 0;


    private Entrega ParseaDatosRecibidos(String data)
    {
        BaseDeDatos.getInstance().setContext(this);
        String[]  partes = data.split("<\\|>");

        Entrega entrega = new Entrega();
        entrega.setIdEntrega(Integer.parseInt(partes[0]));
        entrega.setDescripcion(partes[1]);
        entrega.setIdClientePaga(Integer.parseInt(partes[4]));
        entrega.setTiempoAprox(partes[5]);
        entrega.setDistanciaAprox(partes[6]);
        entrega.setCosto(Float.parseFloat(partes[7]));
        entrega.setStatus(Integer.parseInt(partes[8]));
        Date date = new Date();
        entrega.setRecibido(date.getTime());

        String strClienteEntrega = partes[2];
        String strClienteRecibe = partes[3];

        String[] partesClienteEntrega = strClienteEntrega.split("\\|");
        String[] partesClienteRecibe = strClienteRecibe.split("\\|");

        Cliente clienteEntrega = new Cliente();
        Cliente clienteRecibe = new Cliente();

        clienteEntrega.setIdUbicacion(Integer.parseInt(partesClienteEntrega[0]));
        clienteEntrega.setNombre(partesClienteEntrega[1]);
        clienteEntrega.setTelefono(partesClienteEntrega[2]);
        clienteEntrega.setDireccion(partesClienteEntrega[3]);
        clienteEntrega.setReferencia(partesClienteEntrega[4]);
        clienteEntrega.setLatitud(partesClienteEntrega[5]);
        clienteEntrega.setLongitud(partesClienteEntrega[6]);

        clienteRecibe.setIdUbicacion(Integer.parseInt(partesClienteRecibe[0]));
        clienteRecibe.setNombre(partesClienteRecibe[1]);
        clienteRecibe.setTelefono(partesClienteRecibe[2]);
        clienteRecibe.setDireccion(partesClienteRecibe[3]);
        clienteRecibe.setReferencia(partesClienteRecibe[4]);
        clienteRecibe.setLatitud(partesClienteRecibe[5]);
        clienteRecibe.setLongitud(partesClienteRecibe[6]);

        entrega.setClienteEntrega(clienteEntrega);
        entrega.setClienteRecibe(clienteRecibe);
        entrega.setIdUsuario(Usuario.getActive().getId());
        return entrega;
    }

    private void muestraNotificacion(Entrega entrega)
    {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Mototracking")
                        .setContentText("has recibido una nueva notificacion");
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, EntregaPendienteActivity.class);
        resultIntent.putExtra(Inicio.IS_NEW_ENTREGA, entrega.getIdEntrega());

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(Inicio.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        //vibracion
        long[] pattern = new long[]{1000,500,1000};
        mBuilder.setVibrate(pattern);
        //audio
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(defaultSound);
        mBuilder.setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());
    }
    private void lanzanNotificacion(Entrega entrega)
    {
        Log.d("notification", "Aplicacion en : " + appIsInBackground());
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("MotoTracking")
                        .setContentText("Se te ha asignado una nueva entrega");
// Creates an explicit intent for an Activity in your app

        final Intent intent = new Intent(getApplicationContext(), EntregaPendienteActivity.class);
        intent.putExtra("identrega", entrega.getIdEntrega().toString());
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //startActivity(intent);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        //final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
/*
        Intent resultIntent = new Intent(this, EntregaPendienteActivity.class);
        resultIntent.putExtra("identrega",entrega.getIdEntrega().toString());
        Log.d("Notification", "identrega: " + entrega.getIdEntrega());
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(Inicio.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
*/
        mBuilder.setContentIntent(pendingIntent);
        //vibracion
        long[] pattern = new long[]{1000,500,1000};
        mBuilder.setVibrate(pattern);
        //audio
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(defaultSound);

        mBuilder.setAutoCancel(true);
        mBuilder.mNotification.flags = mBuilder.mNotification.flags | Notification.FLAG_INSISTENT;

        //mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(idNotificacion, mBuilder.build());
        idNotificacion++;
    }

    private boolean appIsInBackground() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService( Context.ACTIVITY_SERVICE );
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for(ActivityManager.RunningAppProcessInfo appProcess : appProcesses){
            if(appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
                Log.i("Foreground App", appProcess.processName);
                return false;
            }
        }
        return true;
    }


    private void lanzaAlerta(Entrega entrega) {
        Intent intent = new Intent(getApplicationContext(),Inicio.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Inicio.IS_NEW_ENTREGA, entrega.getIdEntrega());
        startActivity(intent);
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("datos");
        Log.d("Listener", "From: " + from);
        Log.d("Listener", "Message: " + message);
        Entrega entrega = this.ParseaDatosRecibidos(message);
        entrega.save();
        Log.d("app"," estado: " + appIsInBackground());
        if(appIsInBackground())
            muestraNotificacion(entrega);
        else
            lanzaAlerta(entrega);
    }


}
