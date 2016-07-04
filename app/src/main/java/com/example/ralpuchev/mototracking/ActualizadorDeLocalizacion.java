package com.example.ralpuchev.mototracking;

import android.app.IntentService;
import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by ralpuchev on 04/04/16.
 */
public class ActualizadorDeLocalizacion extends IntentService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, Summoner{

    private static ActualizadorDeLocalizacion unica_instancia = null;

    public static ActualizadorDeLocalizacion getInstancia(){
        if(unica_instancia == null) unica_instancia = new ActualizadorDeLocalizacion();
        return unica_instancia;
    }

    // Defines a custom Intent action
    public static final String BROADCAST_ACTION =
            "com.example.ralpuchev.mototracking.BROADCAST";

    // Defines the key for the status "extra" in an Intent
    public static final String LOCALIZACION = "0<>0";

    public ActualizadorDeLocalizacion(){
        super("");
    }

    private static GoogleApiClient mGoogleApiClient;
    private static LocationRequest mLocationRequest;
    private static long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private static long FASTEST_INTERVAL = 15000; /* 15 secs */
    public static String id_usuario = "0";
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    public void procesaRespuesta(String respuesta_servidor) {
        System.out.println("Respuesta localizacion actualizada en background: " + respuesta_servidor);
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        this.id_usuario = workIntent.getDataString();
        getMyLocation();
    }

    /*
    IMPLEMENTACION PARA RECUPERAR LA LOCALIZACION ACTUAL Y ACTUALIZACIONES POSTERIORES
     */

    void getMyLocation() {
        //primero verificamos que haya permiso para solicitar la ubicación actual del usuario
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //como si tenemos permiso, entonces nos conectamos con playservices para tomar la ubicación del usuario.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        connectClient();
    }

    protected void connectClient() {
        //Hacemos la conexión con playservices para pedirle actualizaciones de la ubicación del usuario
        if (isGooglePlayServicesAvailable() && mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates", "Google Play services is available.");
            return true;
        } else {

            // Get the error dialog from Google Play services
            /*Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);
            */
            // If Google Play services can provide an error dialog
            //if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                /*ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(errorDialog);
                errorFragment.show(getSupportFragmentManager(), "Location Updates");*/
            //}

            return false;
        }
    }

    /*
     *Invocada cuando se realiza la conexión con location services, en este punto ya se puede solicitar la localización del usuario actual
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        //Primero verificamos los permisos para lectura de la localización actual
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //recuperamos la localización del cliente
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null) {
            System.out.println("Localizacion determinada!");
            publicaLocalizacion(location);
        } else {
            System.out.println("Localizacion NO determinada!");
        }
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
    }

    public void publicaLocalizacion(Location localizacion){
        ModelConnector.getInstance().getRespuestaDeServidor("opcion=18&idusuario=" + this.id_usuario + "&lat=" + localizacion.getLatitude() + "&long=" + localizacion.getLongitude() + "&device=1", this);

        //publicamos al mapa la localizacion actual
        Intent localIntent = new Intent(BROADCAST_ACTION).putExtra(LOCALIZACION, localizacion.getLatitude()+"<>"+localizacion.getLongitude());
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    public void onLocationChanged(Location location) {
        //método invocado cuando se actualiza la ubicación del usuario
        publicaLocalizacion(location);
    }

    /*
     * Called by Location Services if the connection to the location client
     * drops because of an error.
     */
    @Override
    public void onConnectionSuspended(int i) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(this, "Desconectado del servicio, por favor reconectate.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(this, "Se perdió la red, por favor reconectate", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * Called by Location Services if the attempt to Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
        if (connectionResult.hasResolution()) {
            //try {
                // Start an Activity that tries to resolve the error
                //connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
            //} catch (IntentSender.SendIntentException e) {
                // Log the error
            //    e.printStackTrace();
            //}
        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry. Location services not available to you", Toast.LENGTH_LONG).show();
        }
    }

    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
}
