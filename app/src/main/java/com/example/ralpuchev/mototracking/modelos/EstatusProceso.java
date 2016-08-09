package com.example.ralpuchev.mototracking.modelos;

import android.util.Log;

import com.example.ralpuchev.mototracking.ModelConnector;
import com.example.ralpuchev.mototracking.Summoner;

/**
 * Created by ralpuchev on 20/07/16.
 */
public class EstatusProceso  implements Summoner {

    private int VISITA_OPCION = 7;

    public static final Integer INICIA_PROCESO = 1;
    public static final Integer RECOGI_PAQUETE = 2;
    public static final Integer LLEGUE_AL_DESTINO = 3;
    public static final Integer PROCESO_FINALIZADO = 4;
    private String respuesta = null;

    public EstatusProceso(Usuario usuario, Entrega entrega,Integer estatus) {
        ModelConnector.getInstance().getRespuestaDeServidor("opcion=" + VISITA_OPCION + "&idusuario=" + usuario.getId() + "&identrega="+ entrega.getIdEntrega() + "&estatus=" + estatus,this);
    }

    @Override
    public void procesaRespuesta(String respuesta_servidor) {
        this.respuesta = respuesta_servidor;
        Log.d("EstatusProceso", respuesta_servidor);
    }

    public String getRespuesta() {
        return respuesta;
    }
}