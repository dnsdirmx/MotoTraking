package com.example.ralpuchev.mototracking;
import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.widget.Toast;

import com.example.ralpuchev.mototracking.modelos.Entrega;
import com.example.ralpuchev.mototracking.modelos.EstatusProceso;
import com.example.ralpuchev.mototracking.vistas.BaseActivity;

import java.util.ArrayList;
import java.util.Iterator;

public class EntregasPendientesActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private EntregasAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_entregas_pendientes);


        Bundle bundle = getIntent().getExtras();
        boolean esRealizadas = bundle.getBoolean(Inicio.IS_REALIZADAS, false);

        ArrayList<Entrega> entregas = new ArrayList<Entrega>();
        Iterator<Entrega> iter = Entrega.findAll(Entrega.class);
        while(iter.hasNext())
        {
            entregas.add(iter.next());
        }
        ArrayList<Entrega> tmEntrega = new ArrayList<Entrega>();
        if(esRealizadas)
        {
            for(Entrega e : entregas)
            {
                if(e.getStatus() == EstatusProceso.PROCESO_FINALIZADO)
                    tmEntrega.add(e);
            }
        }
        else
        {
            for(Entrega e : entregas)
            {
                if(e.getStatus() != EstatusProceso.PROCESO_FINALIZADO)
                    tmEntrega.add(e);
            }
        }
        entregas = tmEntrega;

        //int idEntrega = bundle.getInt(Inicio.IS_NEW_ENTREGA);
        /*entrega = Entrega.finById(idEntrega);

        if(entrega == null)
        {
            Toast.makeText(this, "No se ha encontrado la entrega especificada", Toast.LENGTH_SHORT);
            this.finish();
        }*/
        //crea ReciclerView

        this.getSupportActionBar().hide();
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new EntregasAdapter(entregas,this);
        mRecyclerView.setAdapter(mAdapter);

    }
}
