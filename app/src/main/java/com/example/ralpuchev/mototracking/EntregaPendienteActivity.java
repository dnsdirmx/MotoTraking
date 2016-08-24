package com.example.ralpuchev.mototracking;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.AvoidXfermode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ralpuchev.mototracking.modelos.Entrega;
import com.example.ralpuchev.mototracking.modelos.EstatusProceso;
import com.example.ralpuchev.mototracking.modelos.Usuario;
import com.example.ralpuchev.mototracking.vistas.BaseActivity;

import java.text.SimpleDateFormat;

import static com.example.ralpuchev.mototracking.modelos.EstatusProceso.*;

public class EntregaPendienteActivity extends BaseActivity {
    Entrega entrega = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrega_pendiente);
        Bundle bundle = getIntent().getExtras();
        Integer idEntrega = bundle.getInt(Inicio.IS_NEW_ENTREGA);
        entrega = Entrega.findById(Entrega.class,idEntrega.longValue());

        if(entrega == null)
        {
            Toast.makeText(this,"No se ha encontrado la entrega especificada",Toast.LENGTH_SHORT);
            this.finish();
        }
        llenaDatos(entrega);


        Button btnRecibo = (Button) findViewById(R.id.btnRecibo);

        btnRecibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanzaAlertaRecibo(Usuario.getActive(), entrega);
            }
        });

        Button btnLlegue = (Button) findViewById(R.id.btnLlegue);
        btnLlegue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanzaAlertaLlegue(Usuario.getActive(), entrega);
            }
        });

        Button btnEntregue = (Button) findViewById(R.id.btnEntregue);
        btnEntregue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanzaActividadEntrega(Usuario.getActive(), entrega);
            }
        });

        Log.d("vista","Estatus entrega " + entrega.getStatus());
        int estatus = entrega.getStatus();
        switch(estatus)
        {
            case 1:
                activaBtnsComienza();
                break;
            case 2:
                activaBntsRecibo();
                break;
            case 3:
                activaBntsLlegue();
                break;
            case 4:
                activaBntsEntrege();
                break;
        }


        enviaVisto(Usuario.getActive(), entrega);

    }

    private void lanzaActividadEntrega(Usuario active, Entrega entrega) {
        activaBntsEntrege();
        Intent intent = new Intent(this,DatosEntregaActivity.class);
        intent.putExtra(Inicio.IS_NEW_ENTREGA,entrega.getIdEntrega());
        startActivity(intent);
    }

    private void lanzaAlertaLlegue(final Usuario usuario, final Entrega entrega) {

        new AlertDialog.Builder(this)
                .setTitle("Entregas")
                .setMessage("Se notificara que ya llegaste al lugar de entrega")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        EstatusProceso ep = new EstatusProceso(usuario,entrega,EstatusProceso.LLEGUE_AL_DESTINO);
                        activaBntsLlegue();
                        entrega.setStatus(EstatusProceso.LLEGUE_AL_DESTINO);
                        entrega.save();
                        //entrega.updateStatus();
                        refresca();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void lanzaAlertaRecibo(final Usuario usuario, final Entrega entrega) {
        new AlertDialog.Builder(this)
                .setTitle("Entregas")
                .setMessage("Se notificara que ya tienes el paquete")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        EstatusProceso ep = new EstatusProceso(usuario,entrega,EstatusProceso.RECOGI_PAQUETE);
                        ep.getRespuesta();
                        activaBntsRecibo();
                        entrega.setStatus(EstatusProceso.RECOGI_PAQUETE);
                        entrega.save();
                        //entrega.updateStatus();

                        refresca();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void refresca() {
        //finish();
        //startActivity(getIntent());
    }

    private void enviaVisto(Usuario usuario,Entrega entrega) {
        new EstatusProceso(usuario,entrega, INICIA_PROCESO);
    }

    private void llenaDatos(Entrega entrega) {
        TextView txtDescripcion = ( TextView) findViewById(R.id.txtDescripcion);
        txtDescripcion.setText(entrega.getDescripcion());
        TextView txtClienteEntrega = (TextView) findViewById(R.id.txtClienteEntrega);
        txtClienteEntrega.setText(entrega.getClienteEntrega().getNombre() + ", " + entrega.getClienteEntrega().getDireccion());

        TextView txtClienteRecibe = (TextView) findViewById(R.id.txtClienteRecibe);
        txtClienteRecibe.setText(entrega.getClienteRecibe().getNombre()+ ", " + entrega.getClienteRecibe().getDireccion());

        TextView txtEntregapaga = (TextView) findViewById(R.id.txtCostoEntrega);
        TextView txtRecibepaga = (TextView) findViewById(R.id.txtCostoRecibe);
        if(entrega.getIdClientePaga() == 1) {
            txtEntregapaga.setText("$ " + String.valueOf(entrega.getCosto()));
            txtRecibepaga.setText("");
        }
        else {
            txtEntregapaga.setText("");
            txtRecibepaga.setText("$ " + String.valueOf(entrega.getCosto()));
        }


        String strFecha = new SimpleDateFormat("dd/MM/yyyy h:mm a").format(entrega.getRecibido());
        TextView horaRecibe = (TextView) findViewById(R.id.txtHoraRecibe);
        horaRecibe.setText(strFecha);


        TextView txtIdEntrega = (TextView) findViewById(R.id.txtIdEntrega);
        txtIdEntrega.setText(String.valueOf(entrega.getIdEntrega()));
    }

    private void activaBtnsComienza()
    {
        Button btnRecibo = (Button) findViewById(R.id.btnRecibo);
        btnRecibo.setEnabled(true);
        Button btnLlegue = (Button) findViewById(R.id.btnLlegue);
        btnLlegue.setEnabled(false);
        Button btnEntregue = (Button) findViewById(R.id.btnEntregue);
        btnEntregue.setEnabled(false);
    }

    private void activaBntsRecibo()
    {
        Button btnRecibo = (Button) findViewById(R.id.btnRecibo);
        btnRecibo.setEnabled(false);
        Button btnLlegue = (Button) findViewById(R.id.btnLlegue);
        btnLlegue.setEnabled(true);
        Button btnEntregue = (Button) findViewById(R.id.btnEntregue);
        btnEntregue.setEnabled(false);
    }

    private void activaBntsLlegue()
    {
        Button btnRecibo = (Button) findViewById(R.id.btnRecibo);
        btnRecibo.setEnabled(false);
        Button btnLlegue = (Button) findViewById(R.id.btnLlegue);
        btnLlegue.setEnabled(false);
        Button btnEntregue = (Button) findViewById(R.id.btnEntregue);
        btnEntregue.setEnabled(true);
    }

    private void activaBntsEntrege()
    {
        Button btnRecibo = (Button) findViewById(R.id.btnRecibo);
        btnRecibo.setEnabled(false);
        Button btnLlegue = (Button) findViewById(R.id.btnLlegue);
        btnLlegue.setEnabled(false);
        Button btnEntregue = (Button) findViewById(R.id.btnEntregue);
        btnEntregue.setEnabled(true);
    }
}
