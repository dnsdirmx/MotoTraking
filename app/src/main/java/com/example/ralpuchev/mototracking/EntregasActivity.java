package com.example.ralpuchev.mototracking;

import android.app.LocalActivityManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TabHost;

public class EntregasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entregas);

        TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);

        LocalActivityManager mLocalActivityManager = new LocalActivityManager(this, false);
        mLocalActivityManager.dispatchCreate(savedInstanceState);
        tabHost.setup(mLocalActivityManager);

        TabHost.TabSpec tab1 = tabHost.newTabSpec("Pendientes");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("Realizadas");

        tab1.setIndicator("Pendientes");
        Intent intent = new Intent(this,EntregasPendientesActivity.class);
        intent.putExtra(Inicio.IS_REALIZADAS, false);
        tab1.setContent(intent);

        tab2.setIndicator("Realizadas");
        Intent intent2 = new Intent(this,EntregasPendientesActivity.class);
        intent2.putExtra(Inicio.IS_REALIZADAS,true);
        tab2.setContent(intent2);

        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
    }
}
