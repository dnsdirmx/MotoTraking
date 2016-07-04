package com.example.ralpuchev.mototracking;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ralpuchev on 27/03/16.
 */
public class AsyncTask extends android.os.AsyncTask<String, String, String> {

    private int numero_respuesta = -1;
    private String respuesta = "";

    public void setNumeroDeRespuesta(int numero_respuesta){
        this.numero_respuesta = numero_respuesta;
    }

    @Override
    protected String doInBackground(String... ops) {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(ops[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            respuesta = streamToString(in);
            return respuesta;
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            urlConnection.disconnect();
        }
        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        ModelConnector.getInstance().setRespuesta(respuesta, numero_respuesta);
    }

    private String streamToString(InputStream in) throws IOException {
        StringBuilder out = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        for(String line = br.readLine(); line != null; line = br.readLine())
            out.append(line);
        br.close();
        return out.toString();
    }
}
