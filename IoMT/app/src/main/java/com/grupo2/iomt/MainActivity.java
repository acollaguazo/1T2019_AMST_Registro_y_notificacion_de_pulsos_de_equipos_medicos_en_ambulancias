package com.grupo2.iomt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.grupo2.iomt.dao.TokenDao;
import com.grupo2.iomt.db.DB;
import com.grupo2.iomt.entity.Token;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import java.io.IOException;
import java.net.InetAddress;

public class MainActivity extends AppCompatActivity {
    private Button btnLogin, btnRegistrarse;
    private EditText user,pass;
    private RequestQueue mQueue=null;
    private String token=null;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
        * Variables creadas referenciadas a los controles
        * Richard*/

        mQueue= Volley.newRequestQueue(this);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        user = (EditText) findViewById(R.id.txtUser);
        pass = (EditText) findViewById(R.id.txtPasswd);
        cargarCredenciales();

        sharedPreferences = getSharedPreferences("Credenciales", Context.MODE_PRIVATE);

        /* Ejemplo para usar base de datos

        DB db = instanceDB("mainDB");
        TokenDao a = db.getTokenDAO();
        Token aaa =  new Token("ahj");
        a.insert(aaa);
        a.getItems();

        */
    }

    public void IniciarSesion(View view){
        final EditText dt1=(EditText) findViewById(R.id.txtUser);
        final EditText dt2=(EditText) findViewById(R.id.txtPasswd);
        String usuario=dt1.getText().toString();
        String contrasena=dt2.getText().toString();



        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("User", usuario);
        editor.putString("Passwd", contrasena);
        editor.commit();

        if(CheckInternet.errorConexion()){
            Toast.makeText(this, "No hay conexion a Internet", Toast.LENGTH_LONG).show();
        }
        else{
            iniciarSesion(usuario,contrasena);
        }

    }

    private void cargarCredenciales() {

        SharedPreferences preferences = getSharedPreferences("Credenciales", Context.MODE_PRIVATE);

        String use = preferences.getString("User","");
        String passwd = preferences.getString("Passwd","");

        user.setText(use);
        pass.setText(passwd);

    }

    private void iniciarSesion(String usuario, String contrasena) {
        Map<String, String> params=new HashMap<>();
        params.put("username", usuario);
        params.put("password", contrasena);

        JSONObject parametros=new JSONObject(params);
        String URL = "https://amstdb.herokuapp.com/db/nuevo-jwt";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, parametros, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);
                try {
                    token = response.getString("token");
                    Intent i = new Intent(getBaseContext(), Menu.class);
                    i.putExtra("token", token);
                    startActivity(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Alerta");
                alertDialog.setMessage("Credenciales Incorrectas");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

                //Toast.makeText(getApplicationContext(),"Credenciales no correctas",Toast.LENGTH_LONG).show();
            }
        });
        mQueue.add(request);
    }

    private DB instanceDB(String name){
        DB db = Room.databaseBuilder(this, DB.class, name)
                .allowMainThreadQueries()
                .build();
        return db;
    }




}
