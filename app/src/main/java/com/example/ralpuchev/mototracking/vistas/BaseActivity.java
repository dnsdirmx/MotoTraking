package com.example.ralpuchev.mototracking.vistas;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.ralpuchev.mototracking.EntregasActivity;
import com.example.ralpuchev.mototracking.R;
import com.example.ralpuchev.mototracking.modelos.BaseDeDatos;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseDeDatos.setContext(this);
        setContentView(R.layout.activity_base);
    }

    @Override
    public void setContentView(int layoutResID)
    {
        final DrawerLayout fullView = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        FrameLayout activityContainer = (FrameLayout) fullView.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        super.setContentView(fullView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (useToolbar())
        {
            setSupportActionBar(toolbar);
            setTitle(getString(R.string.app_name));
        }
        else
        {
            toolbar.setVisibility(View.GONE);
        }
        //verificar si esta logueado
        if(useDrawerLayout()) {
            NavigationView nv = (NavigationView) findViewById(R.id.navigationView);
            nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {
                    fullView.closeDrawers();
                    switch (item.getItemId()) {
                        case R.id.optInicio:
                            Toast.makeText(getApplicationContext(), "Opcion inicio", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.optEntRealizada:
                            Toast.makeText(getApplicationContext(), "Opcion entregados", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.optEntPendiente:
                            Toast.makeText(getApplicationContext(), "Opcion recibidos", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.optSalir:
                            Toast.makeText(getApplicationContext(), "Opcion recibidos", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    return false;
                }
            });
        }
        else
        {
            //remover vista del drawerLayout

        }
    }

    private boolean useDrawerLayout() {
        return true;
    }

    protected boolean useToolbar()
    {
        return true;
    }
}
