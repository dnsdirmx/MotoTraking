package com.example.ralpuchev.mototracking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ralpuchev.mototracking.modelos.BaseDeDatos;
import com.example.ralpuchev.mototracking.modelos.Usuario;
import com.example.ralpuchev.mototracking.vistas.BaseActivity;

public class Login extends BaseActivity implements Summoner{

    private String[] info_login = null;
    //token|idusuario|nombrecompleto|nombre_usuario|password|correo
    public final static String IDUSUARIO = "0";
    public final static String TOKEN = "1";
    public final static String USUARIO_NOMBRE = "2";
    ProgressDialog pdLogin = null;

    public boolean deslogea = false;

    protected boolean useToolbar()
    {
        return false;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        BaseDeDatos.setContext(this);

        if(Usuario.getActive() != null)
        {
            Usuario usuario = Usuario.getActive();
            if(usuario.isLogueado()) {
                usuario.signOut();
                deslogea = true;
                ModelConnector.getInstance().getRespuestaDeServidor("opcion=4&token=" + usuario.getToken(), this);
            }
            else {
                deslogea = false;
                ModelConnector.getInstance().getRespuestaDeServidor("opcion=3&usuario=" + usuario.getNombre() + "&password=" + usuario.getPassword(), this);
            }
        }
        else
        {
            if(Usuario.getRememberUser() != null)
            {
                EditText nombre_usuario = (EditText) findViewById(R.id.campo_usuario);
                EditText password_usuario = (EditText) findViewById(R.id.campo_password);
                CheckBox remember = (CheckBox) findViewById(R.id.remember);
                nombre_usuario.setText(Usuario.getRememberUser().getNombre());
                password_usuario.setText(Usuario.getRememberUser().getPassword());
            }
        }
    }

    public void entrar(View view) {
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
         pdLogin = ProgressDialog.show(this,"Iniciando sesión","Espere...");
        ModelConnector.getInstance().getRespuestaDeServidor("opcion=3&usuario=" + nombre_usuario.getText() + "&password=" + password_usuario.getText(), this);
    }

    public void procesaRespuesta(String respuesta_servidor){
        if(pdLogin != null)
            pdLogin.dismiss();
        if(deslogea)
        {
            ModelConnector.getInstance().getRespuestaDeServidor("opcion=3&usuario=" + Usuario.getActive().getNombre() + "&password=" + Usuario.getActive().getPassword(), this);
            deslogea = false;
        }
        else {
            System.out.println("Respuesta login: " + respuesta_servidor);

            if (respuesta_servidor.equals("0")) {
                Toast.makeText(this, "Usuario ó password incorrectos!", Toast.LENGTH_LONG).show();
                return;
            }

            System.out.println("Respuesta a dividir: " + respuesta_servidor.length());

            this.info_login = respuesta_servidor.split("<>");
            System.out.println("Info login: " + this.info_login + " tam: " + this.info_login.length);
            if (info_login.length < 5) {
                Toast.makeText(this, "Error del servidor", Toast.LENGTH_LONG).show();
                return;
            }

            //creamos la actividad de inicio de la aplicacion

            EditText nombre_usuario = (EditText) findViewById(R.id.campo_usuario);
            EditText password_usuario = (EditText) findViewById(R.id.campo_password);
            CheckBox remember = (CheckBox) findViewById(R.id.remember);
            Usuario usuario = new Usuario();
            Log.d("Login", this.info_login[1]);
            usuario.setId(Integer.parseInt(this.info_login[1]));
            usuario.setNombre(nombre_usuario.getText().toString());
            usuario.setPassword(password_usuario.getText().toString());
            usuario.setRemember(remember.isChecked());
            usuario.setToken(this.info_login[0]);
            usuario.setLogueado(true);
            usuario.save();

            Intent intent = new Intent(this, Inicio.class);
            intent.putExtra(IDUSUARIO, this.info_login[1]);
            intent.putExtra(TOKEN, this.info_login[0]);
            intent.putExtra(USUARIO_NOMBRE, this.info_login[2]);
            startActivity(intent);
        }
    }
}
