package com.example.ralpuchev.mototracking;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class Login extends AppCompatActivity implements Summoner{

    private String[] info_login = null;
    //token|idusuario|nombrecompleto|nombre_usuario|password|correo
    public final static String IDUSUARIO = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        DrawerLayout mDrawerLayout;
        ListView mDrawerList;
        String[] mOpciones = getResources().getStringArray(R.array.opciones_menu);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,R.layout.drawer_list_item, mOpciones));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener(this));
    }

    public void validaLogin(View view) {
        EditText nombre_usuario = (EditText) findViewById(R.id.campo_usuario);
        EditText password_usuario = (EditText) findViewById(R.id.campo_password);

        if(TextUtils.isEmpty(nombre_usuario.getText())) {
            nombre_usuario.requestFocus();
            nombre_usuario.setError("Debes escribir un nombre de usuario!");
            return;
        }

        if(TextUtils.isEmpty(password_usuario.getText())) {
            password_usuario.requestFocus();
            password_usuario.setError("Debes escribir tu password!");
            return;
        }

        //intentamos el login con el usuario y password proporcionado
        ModelConnector.getInstance().getRespuestaDeServidor("opcion=3&usuario=" + nombre_usuario.getText() + "&password=" + password_usuario.getText(), this);
    }

    public void procesaRespuesta(String respuesta_servidor){
        System.out.println("Respuesta login: "+respuesta_servidor);

        if(respuesta_servidor.equals("0")){
            Toast.makeText(this, "Usuario ó password incorrectos!", Toast.LENGTH_LONG).show();
            return;
        }
        System.out.println("Respuesta a dividir: "+respuesta_servidor);
        this.info_login = respuesta_servidor.split("<>");
        System.out.println("Info login: "+this.info_login+" tam: "+this.info_login.length);

        //creamos la actividad de inicio de la aplicacion
        Intent intent = new Intent(this, Inicio.class);
        intent.putExtra(IDUSUARIO, this.info_login[1]);
        startActivity(intent);
    }
}
