package com.example.ralpuchev.mototracking;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.ralpuchev.mototracking.R;
import com.example.ralpuchev.mototracking.modelos.Usuario;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

/**
 * Created by ralpuchev on 14/07/16.
 */
public class RegistrationService extends IntentService {
    public static final String TOPIC = "/topics/my_little_topic";
    String registrationToken;
    public RegistrationService() {
        super("RegistrationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        InstanceID myID = InstanceID.getInstance(this);
        try {
            registrationToken = myID.getToken(
                    getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE,
                    null
            );

            Log.d("Registration Token", registrationToken);
            new SendToken(registrationToken);
            if(registrationToken == null)
                Toast.makeText(getApplicationContext(),"Se obtuvo un token vacio",Toast.LENGTH_SHORT).show();
            GcmPubSub subscription = GcmPubSub.getInstance(this);
            subscription.subscribe(registrationToken, TOPIC, null);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private class SendToken implements Summoner
    {
        private static final int N_OPCION = 6;
        public SendToken(String token)
        {
            Usuario usuario = Usuario.getActive();
            ModelConnector.getInstance().getRespuestaDeServidor("opcion="+ N_OPCION + "&idusuario=" + usuario.getId() + "&tokenGCM=" + token, this);
        }
        @Override
        public void procesaRespuesta(String respuesta_servidor) {
            Log.d("RegistrationService",respuesta_servidor);
        }
    }
}
