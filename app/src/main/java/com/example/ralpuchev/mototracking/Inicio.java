package com.example.ralpuchev.mototracking;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

public class Inicio extends AppCompatActivity{
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private MapFragment mapFragment;
    private GoogleMap map;
    public String id_usuario = "0";
    private ActualizadorDeLocalizacionDeMapa actualizador = null;
    private String[] mOpciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        Intent intent = getIntent();
        id_usuario = intent.getStringExtra(Login.IDUSUARIO);
        System.out.println("Idusuario: " + id_usuario);

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



        //AGREGANDO EL DRAWER LAYOUT
        mOpciones = getResources().getStringArray(R.array.opciones_menu);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,R.layout.drawer_list_item, mOpciones));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener(this));

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

        Intent actualizador_intent = new Intent(this, ActualizadorDeLocalizacion.class);
        actualizador_intent.setData(Uri.parse(this.id_usuario));
        this.startService(actualizador_intent);
    }
}