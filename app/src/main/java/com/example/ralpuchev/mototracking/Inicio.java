package com.example.ralpuchev.mototracking;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ralpuchev.mototracking.modelos.BaseDeDatos;
import com.example.ralpuchev.mototracking.modelos.Usuario;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

public class Inicio extends AppCompatActivity implements Summoner {

    public static final String IS_NEW_ENTREGA = "3";
    public static final String IS_REALIZADAS = "4";
    private MapFragment mapFragment;
    private GoogleMap map;
    public String id_usuario = "0";
    public String token = "1";
    public String usuario_nombre = "2";
    private ActualizadorDeLocalizacionDeMapa actualizador = null;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private ProgressDialog pgLogout =null;
    private Intent actualizador_intent;
    private Intent notificacionesServicio;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar motoToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        Usuario usuario = Usuario.getActive();

        Toast.makeText(this,"Pase 1",Toast.LENGTH_SHORT).show();

        if(usuario == null) finish();

        Toast.makeText(this,"Pase 2",Toast.LENGTH_SHORT).show();

        id_usuario = String.valueOf(usuario.getId());
        token = usuario.getToken();
        usuario_nombre = usuario.getNombre();

        Toast.makeText(this,"Pase 2",Toast.LENGTH_SHORT).show();

        System.out.println("Idusuario: " + id_usuario);
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            int identrega = extras.getInt(Inicio.IS_NEW_ENTREGA, 0);
            if(identrega != 0)
            {
                this.lanzaAlertaNuevaEntrega(identrega);
            }
        }
        Toast.makeText(this,"Pase 3",Toast.LENGTH_SHORT).show();

        cargaMapa();

        Toast.makeText(this,"Pase 4",Toast.LENGTH_SHORT).show();

        cargaMenuLateral();

        Toast.makeText(this,"Pase 5",Toast.LENGTH_SHORT).show();

        startServiceGCM();

        Toast.makeText(this,"Pase 6",Toast.LENGTH_SHORT).show();

    }

    private void lanzaAlertaNuevaEntrega(final int identrega) {
        new AlertDialog.Builder(this)
                .setTitle("Entregas")
                .setMessage("Te ha llegado una nueva entrega")
                .setPositiveButton("Ver entrega", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        lanzaActividadEntrega(identrega);
                    }
                })
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void lanzaActividadEntrega(int identrega) {
        Intent intent = new Intent(this,EntregaPendienteActivity.class);
        intent.putExtra(Inicio.IS_NEW_ENTREGA,identrega);
        startActivity(intent);
    }


    public void cargaMapa()
    {
        //Recuperamos el mapa de manera asincrona a través de un mapfragment
        mapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.map));
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    loadMap(map);//método a invocar cuando se cargue el mapa
                }
            });
        } else {
            System.out.println("No se pudo cargar el fragmento del mapa");
        }
    }
    public void cargaMenuLateral()
    {
        //motoToolbar = (Toolbar) findViewById(R.id.mototoolbar);
        //this.setSupportActionBar(motoToolbar);
        //se obtiene el drawer layout
        NavigationView navview = (NavigationView)findViewById(R.id.navigationView);
        //carga la cabecera
        View headerLayout = navview.getHeaderView(0);
        //carga el textview que tiene el nombre
        TextView iUsuario = (TextView) headerLayout.findViewById(R.id.titleUsuario);
        //modifica el contenido
        iUsuario.setText(usuario_nombre);
    }
    public void startServiceGCM()
    {
        notificacionesServicio = new Intent(this, RegistrationService.class);

        if(!isMyServiceRunning(RegistrationService.class)) {
            Log.d("serviceGCM", "Estado 1: " + this.stopService(notificacionesServicio));
            Log.d("serviceGCM", "Estado 2: " + startService(notificacionesServicio));

        }
        else
            Log.d("serviceGCM", "Estado 1: " + this.stopService(notificacionesServicio));
    }
   //método invocado cuando se carga el mapa, el cual toma como parámetro un mapa
    protected void loadMap(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map = googleMap;
        if (map != null) {
            map.setBuildingsEnabled(false);
            map.setMyLocationEnabled(true);
            System.out.println("Mapa cargado!");
            actualizaLocalizacionEnBackground();
        } else {
            System.out.println("Error al cargar el mapa!");
        }
    }

    /*
     * Se invoca cuando la aplicación de hace visible (Por ejemplo cuando se pasa de una aplicación a otra y luego se vuelve a esta aplicación)
    */
    @Override
    protected void onStart() {
        super.onStart();
        if(Usuario.getActive() == null)
            finish();
    }

    /*
	 * Se invoca cuando la aplicación ya no es visible
	 */
    @Override
    protected void onStop() {
        super.onStop();
    }

    public void centraMapa(String latitud, String longitud){
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(latitud), Double.parseDouble(longitud)), 16);
        map.animateCamera(cameraUpdate);
    }

    public void actualizaLocalizacionEnBackground() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        this.actualizador = new ActualizadorDeLocalizacionDeMapa();
        this.actualizador.setActividadPadre(this);
        this.actualizador.iniciaEscucha();


        actualizador_intent = new Intent(this, ActualizadorDeLocalizacion.class);
        if(!isMyServiceRunning(ActualizadorDeLocalizacion.class)) {
            actualizador_intent.setData(Uri.parse(this.id_usuario));
            Log.d("serviceGCM", "Estado 1: " + this.stopService(actualizador_intent));
            Log.d("serviceGCM", "Estado 2: " + this.startService(actualizador_intent));
        }
        else
            Log.d("serviceGCM", "Estado 1: " + this.stopService(actualizador_intent));

    }

    @Override
    public void procesaRespuesta(String respuesta_servidor) {
        Log.d("hector", "respuesta : " + respuesta_servidor);
        if(respuesta_servidor.equals("1"))
        {
            //Toast.makeText(this,"Cerrando sesión",Toast.LENGTH_SHORT);

            Usuario usuario = Usuario.getActive();
            usuario.setLogueado(false);
            usuario.save();

            if(pgLogout != null) pgLogout.dismiss();
            finish();
        }
        else
        {
            Toast.makeText(this,"No se ha logrado cerrar la sesión, Intenta nuevamente",Toast.LENGTH_SHORT);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}