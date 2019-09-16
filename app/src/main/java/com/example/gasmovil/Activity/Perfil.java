package com.example.gasmovil.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gasmovil.R;
import com.example.gasmovil.Utilidades.Utilidades_Request;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

public class Perfil extends AppCompatActivity {

    TextView mensajeCon;
    EditText _nombre, _apellido, _telefono, _documentoText;
    Button _signupButton;


    SharedPreferences preferencias;
    String nom_base_datos = "usuarios_gas";

    private RequestQueue request;
    StringRequest stringRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarP);
        toolbar.setTitle("Actualizar datos del perfil");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        _nombre = (EditText) findViewById(R.id.input_nombre);
        _apellido = (EditText) findViewById(R.id.input_apellido);
        _telefono = (EditText) findViewById(R.id.input_telefono);
        _documentoText = (EditText) findViewById(R.id.input_documento);
        mensajeCon = (TextView) findViewById(R.id.TextViewMensajeConfirmacionP);

        _signupButton = (Button) findViewById(R.id.btn_signup);

        request = Volley.newRequestQueue(this);
        preferencias=getSharedPreferences(nom_base_datos, Context.MODE_PRIVATE);
        final String id = preferencias.getString("Sid","");
        final String nom = preferencias.getString("Snom","");
        final String ape = preferencias.getString("Sape","");
        final String documento = preferencias.getString("Sdoc","");
        final String telefono = preferencias.getString("Stel","");

        _nombre.setText(nom);
        _apellido.setText(ape);
        _telefono.setText(telefono);
        _documentoText.setText(documento);


        _signupButton.setEnabled(true);
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp(id, nom,ape,telefono,documento);
            }
        });
    }


    @SuppressLint("ResourceAsColor")
    private void signUp(String id, String nomb, String apell, String telefono, String docu){

        if(!validate()) {
            return;
        }

        final String nom = _nombre.getText().toString();
        final String ape = _apellido.getText().toString();
        final String tel = _telefono.getText().toString();
        final String doc = _documentoText.getText().toString();

        if(validatCamps(nom,nomb,ape,apell,tel,telefono,doc,docu)){
            mensajeAlertaTextViewError("No as cambiado ningun dato!",5000);
            return;
        }

        cargarWebServiceActualizaPersona(id,nom,ape,tel,doc);


    }

    private boolean validatCamps(String nom, String nomb, String ape, String apell, String tel, String telefono, String doc, String docu) {
        boolean vali = true;
        int es = 0;
        if(nom.equals(nomb)){
        }else {
            es = 1;
        }

        if(ape.equals(apell)){
        }else {
            es = 1;
        }

        if(tel.equals(telefono)){

        }else {
            es = 1;
        }

        if(doc.equals(docu)){
        }else {
            es = 1;
        }

        if(es == 1){
            vali=false;
        }

       return vali;
    }



    private void cargarWebServiceActualizaPersona(final String id, final String nom, final String ape, final String tel, final String doc) {

        String url = Utilidades_Request.HTTP+Utilidades_Request.IP+Utilidades_Request.CARPETA+"wsJsonUpdatePersona.php?";

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onResponse(String response) {

                if(response.trim().equalsIgnoreCase("Noregistra")){
                    mensajeAlertaTextViewError("No registro ocurrio un error..", 500);

                }else{
                    mensajeAlertaTextViewVerdadero("Registro Exitoso. ", 400);
                    deshabilitarBotones();
                    updateShare(nom,ape,tel,doc);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mensajeAlertaTextViewError("Ocurrio un error en el servidor ", 500);
                Log.i("Error", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> paramentros = new HashMap<>();
                paramentros.put("id", id);
                paramentros.put("nombre", nom);
                paramentros.put("apellido",ape);
                paramentros.put("telefono", tel);
                paramentros.put("documento", doc);
                return paramentros;
            }
        };
        request.add(stringRequest);
    }

    @SuppressLint("ResourceAsColor")
    private void deshabilitarBotones() {
        _signupButton.setEnabled(false);
        _signupButton.setBackgroundColor(R.color.design_default_color_primary_dark);

        _nombre.setEnabled(false);
        _apellido.setEnabled(false);
        _telefono.setEnabled(false);
        _documentoText.setEnabled(false);
    }

    private void updateShare(String nom, String ape, String tel, String doc) {
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("Snom", nom);
        editor.putString("Sape", ape);
        editor.putString("Sdoc", doc);
        editor.putString("Stel", tel);
        editor.commit();
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nombre.getText().toString();
        String apellido = _apellido.getText().toString();
        String telefono = _telefono.getText().toString();
        String documento = _documentoText.getText().toString();

        if (name.isEmpty() || name.length() < 3 ) {
            _nombre.setError("Minimo 3 letras");
            valid = false;
        } else {
            _nombre.setError(null);
        }

        if (apellido.isEmpty() || apellido.length() < 3) {
            _apellido.setError("Minimo 3 letras");
            valid = false;
        } else {
            _apellido.setError(null);
        }


        if (telefono.isEmpty() || telefono.length() != 10) {
            _documentoText.setError("No esta completo el numero telefonico.");
            valid = false;
        } else {
            _documentoText.setError(null);
        }

        if (documento.isEmpty()) {
            _documentoText.setError("Ingrese un numero de documento.");
            valid = false;
        } else {
            _documentoText.setError(null);
        }

        return valid;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
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
