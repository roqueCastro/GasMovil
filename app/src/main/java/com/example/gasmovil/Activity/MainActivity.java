package com.example.gasmovil.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gasmovil.R;
import com.example.gasmovil.Utilidades.Utilidades_Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    TextView mensajeCon;
    private EditText _emailText,_passwordText;
    private Button _loginButton;
    String passCorrect;

    SharedPreferences preferencias;
    String nom_base_datos = "usuarios_gas";

    ProgressDialog progreso;


    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        request = Volley.newRequestQueue(getApplicationContext());
        preferencias=getSharedPreferences(nom_base_datos,Context.MODE_PRIVATE);

        mensajeCon = (TextView) findViewById(R.id.TextViewMensajeConfirmacionSS);
        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);

        _loginButton =(Button) findViewById(R.id.btn_login);


        String id = preferencias.getString("Sid","");
        String nom = preferencias.getString("Snom","");
        String ape = preferencias.getString("Sape","");
        String usua = preferencias.getString("Susua","");
        String pass = preferencias.getString("Spass","");
        String documento = preferencias.getString("Sdoc","");
        String telefono = preferencias.getString("Stel","");


        if((usua != "") && (pass != "")){
            _emailText.setText(usua);
            _passwordText.setText(pass);
        }

        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
       deshabilitarBottonLogin();
        cargarWebServiceUser(_emailText.getText().toString());
    }

    private void cargarWebServiceUser(final String email) {
        progreso= new ProgressDialog(MainActivity.this, R.style.AppCompatAlertDialogStyle);
        progreso.setMessage("Validando Informacion..");
        progreso.show();

        String url = Utilidades_Request.HTTP + Utilidades_Request.IP + Utilidades_Request.CARPETA + Utilidades_Request.ARCHIVO + email;

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
                progreso.hide();

                JSONArray json = response.optJSONArray("usuarios");

                try {
                    for (int i = 0; i < json.length(); i++) {
                        JSONObject jsonObject = null;
                        jsonObject = json.getJSONObject(i);

                        passCorrect= (String) jsonObject.get("PASS");

                        if(!validate()){
                           habilitarBottonLogin();
                            return;
                        }else {
                            SharedPreferences.Editor editor=preferencias.edit();
                            String ID = (String) jsonObject.get("ID");
                            editor.putString("Sid", ID);
                            editor.putString("Snom", (String) jsonObject.get("NOMBRE"));
                            editor.putString("Sape", (String) jsonObject.get("APELLIDO"));
                            editor.putString("Susua", (String) jsonObject.get("USUA"));
                            editor.putString("Spass", (String) jsonObject.get("PASS"));
                            editor.putString("Sdoc", (String) jsonObject.get("DOCUMENTO"));
                            editor.putString("Stel", (String) jsonObject.get("TELEFONO"));
                            editor.commit();

                            startActivity(new Intent(getApplicationContext(), Inicio.class));
                            finish();

                        }
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progreso.hide();
                habilitarBottonLogin();
                mensajeAlertaTextViewError("Error no hay conexion con la base de datos", 2000);
            }
        });
        request.add(jsonObjectRequest);


    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches() ||passCorrect.equals("0")) {
            _emailText.setError("Introduzca una dirección de correo electrónico válida");
            valid=false;
        }else {
            _emailText.setError(null);
        }

        if(password.isEmpty() || password.length() == 0) {
            _passwordText.setError("Ingresa caracteres.");
            valid=false;
        }else  {
            _passwordText.setError(null);

        }

        if(passCorrect.equals(password)) {
            _passwordText.setError(null);
        }else  {
            _passwordText.setError("Contraseña incorrecta.");
            valid=false;
        }

        return valid;
    }

    @SuppressLint("ResourceAsColor")
    private void deshabilitarBottonLogin(){
        _loginButton.setEnabled(false);
        _loginButton.setBackgroundColor(R.color.design_default_color_primary_dark);
    }

    @SuppressLint("ResourceAsColor")
    private void habilitarBottonLogin(){
        _loginButton.setBackgroundColor(Color.parseColor("#00574B"));
        _loginButton.setEnabled(true);

    }

    /*-----------MENSAJES DE ERROR Y REGISTRO--------*/

    private void mensajeAlertaTextViewError(String msj, int timeMensAler) {
        mensajeCon.setBackgroundColor(mensajeCon.getContext().getResources().getColor(R.color.colorRed));
        mensajeCon.setTextColor(mensajeCon.getContext().getResources().getColor(R.color.colorLight));
        mensajeCon.setText(msj);
        mensajeCon.setVisibility(View.VISIBLE);
        esconderMensaje(timeMensAler);
    }
    private void mensajeAlertaTextViewVerdadero(String msj, int timeMensAler) {
        mensajeCon.setBackgroundColor(mensajeCon.getContext().getResources().getColor(R.color.colorLight));
        mensajeCon.setTextColor(mensajeCon.getContext().getResources().getColor(R.color.colorVerdeClaro));
        mensajeCon.setText(msj);
        mensajeCon.setVisibility(View.VISIBLE);
        esconderMensaje(timeMensAler);
    }
    private void esconderMensaje(int timeMensAler) {
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        mensajeCon.setVisibility(View.INVISIBLE);
                    }
                }, timeMensAler);
    }
}
