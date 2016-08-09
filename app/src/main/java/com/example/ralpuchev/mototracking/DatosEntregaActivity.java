package com.example.ralpuchev.mototracking;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ralpuchev.mototracking.modelos.Entrega;
import com.example.ralpuchev.mototracking.modelos.EstatusProceso;
import com.example.ralpuchev.mototracking.modelos.Usuario;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatosEntregaActivity extends AppCompatActivity {
    public static final int THUMBNAIL_WIDTH = 64;
    public static final int THUMBNAIL_HEIGHT = 64;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private Entrega entrega;
    private int nFotografia = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_entrega);



        Bundle bundle = getIntent().getExtras();
        int idEntrega = bundle.getInt(Inicio.IS_NEW_ENTREGA);
        entrega = Entrega.finById(idEntrega);

        if(entrega == null)
        {
            Toast.makeText(this,"No se ha encontrado la entrega especificada",Toast.LENGTH_SHORT);
            this.finish();
        }

        ImageButton btnFrente = (ImageButton)findViewById(R.id.btnFrente);
        btnFrente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nFotografia = 1;
                tomaFotografia();
            }
        });
        ImageButton btnTras = (ImageButton) findViewById(R.id.btnTras);
        btnTras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nFotografia = 2;
                tomaFotografia();
            }
        });


        Button btn = (Button) findViewById(R.id.btnEntregado);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validaForm())
                {
                    Toast.makeText(DatosEntregaActivity.this,"Faltan campos",Toast.LENGTH_SHORT).show();
                    return;
                }
                confirmaEnvioEntregado(entrega);

            }
        });

        if(entrega.getStatus() == EstatusProceso.PROCESO_FINALIZADO)
            btn.setEnabled(false);
    }

    private void confirmaEnvioEntregado(final Entrega entrega)
    {
        new AlertDialog.Builder(this)
                .setTitle("Entregas")
                .setMessage("Se notificara que ya has entregado el paquete")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        TextView txtNombre = (TextView) findViewById(R.id.txtNombre);
                        entrega.setNombreRecibio(txtNombre.getText().toString());
                        entrega.save();
                        entrega.updateStatus(EstatusProceso.PROCESO_FINALIZADO);
                        enviaEstatus es = new enviaEstatus(Usuario.getActive(),entrega);


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
    private boolean validaForm() {
        boolean status = true;
        TextView txtNombre = (TextView) findViewById(R.id.txtNombre);
        if(txtNombre.getText().length() < 1)
            status = false;
        return status;
    }

    private void tomaFotografia() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
        fileUri.getPath();

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }
    private  Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }
    private  File getOutputMediaFile(int type){

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "MotoTracking");

        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MotoTracking", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ entrega.getIdEntrega() +"_"+ timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Imagen guardada " +fileUri.getPath() + "\n", Toast.LENGTH_SHORT).show();
                if(nFotografia == 1) {
                    entrega.setImgFrente(fileUri.getPath());

                    ImageButton btnFrente = (ImageButton)findViewById(R.id.btnFrente);
                    Bitmap resized = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(fileUri.getPath()), THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
                    btnFrente.setImageBitmap(resized);
                }
                if(nFotografia == 2) {
                    entrega.setImgAtras(fileUri.getPath());
                    ImageButton btnTras = (ImageButton) findViewById(R.id.btnTras);
                    Bitmap resized = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(fileUri.getPath()), THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
                    btnTras.setImageBitmap(resized);
                }
                entrega.save();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this,"Se cancelo la fotografia",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,"Imagen no se pudo guardar",Toast.LENGTH_SHORT).show();
            }
        }
    }
    private class enviaEstatus implements Summoner
    {
        Usuario usuario;
        Entrega entrega;
        private String respuesta;

        public enviaEstatus(Usuario usuario,Entrega entrega){
            this.usuario = usuario;
            this.entrega = entrega;
            ModelConnector.getInstance().getRespuestaDeServidor("opcion=9&idusuario=" + usuario.getId() + "&identrega=" + entrega.getIdEntrega() + "&nombre_recibe=" + entrega.getNombreRecibio(), this);
        }

        @Override
        public void procesaRespuesta(String respuesta_servidor) {
            this.respuesta = respuesta_servidor;
            Log.d("Entrega", respuesta_servidor);


            if(respuesta.equals("0"))
                Toast.makeText(DatosEntregaActivity.this,"Ha ocurrido un error",Toast.LENGTH_SHORT).show();
            else {
                try {
                    if (Integer.parseInt(respuesta_servidor) > 0) ;
                    Toast.makeText(DatosEntregaActivity.this, "Se ha enviado la confirmacion de entrega", Toast.LENGTH_SHORT).show();
                    Button btn = (Button) findViewById(R.id.btnEntregado);
                    btn.setEnabled(false);
                }catch (Exception e)
                {
                    Toast.makeText(DatosEntregaActivity.this,"Ha ocurrido un error",Toast.LENGTH_SHORT).show();
                }
            }
        }

        public String getRespuesta() {
            return respuesta;
        }
    }
}
