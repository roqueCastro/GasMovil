package com.example.gasmovil.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
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
import com.example.gasmovil.Adapter.ElementoAdapter;
import com.example.gasmovil.Entidades.Element;
import com.example.gasmovil.R;
import com.example.gasmovil.Utilidades.Utilidades_Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Elemento extends AppCompatActivity  {


    RequestQueue request = null;
    JsonObjectRequest jsonObjectRequest = null;
    StringRequest stringRequest;

    ElementoAdapter adapter;


    TextView mensajeCon;
    ListView listView;
    ArrayList<Element> elementos;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elemento);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarE);
        toolbar.setTitle("Agrega Elementos");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);



        int id = getIntent().getIntExtra("id", 0);
        final String idM = String.valueOf(id);

        elementos = new ArrayList<>();

        mensajeCon = (TextView) findViewById(R.id.TextViewMensajeConfirmacionE);
        listView= (ListView) findViewById(R.id.idlistViewEle);

        request = Volley.newRequestQueue(this);
        cargarWebServiceElementos();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showAlertElement("Cantidad" , idM, position);
            }
        });




    }

    private void cargarWebServiceElementos() {
        String url = Utilidades_Request.HTTP + Utilidades_Request.IP + Utilidades_Request.CARPETA + "wsJsonConsultaElemento.php";

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();

                JSONArray json = response.optJSONArray("elementos");
                Element elemento = null;
                elementos.clear();

                try {
                    for (int i = 0; i < json.length(); i++) {

                        JSONObject jsonObject = null;
                        jsonObject = json.getJSONObject(i);

                        elemento = new Element();
                        elemento.setId_e(jsonObject.optInt("pk_id_e"));
                        elemento.setNombre_e(jsonObject.optString("nom_e"));
                        elemento.setCodigo_e(jsonObject.optString("codigo_elem"));
                        elementos.add(elemento);
                    }

                    adapter  = new ElementoAdapter(getApplicationContext(), elementos, R.layout.elemento_list_layout);
                    //Toast.makeText(context, adapter.toString(), Toast.LENGTH_SHORT).show();

                    listView.setAdapter(adapter);


                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mensajeAlertaTextViewError("Error no hay conexion con la base de datos", 3000);
            }
        });

        request.add(jsonObjectRequest);
    }

    private void cargarWebServiceRegistrodetalleMovimiento(final String cantidad, final int position, final String idM) {

        String url = Utilidades_Request.HTTP+Utilidades_Request.IP+Utilidades_Request.CARPETA+"wsJsonRegistroDetalleMovimiento.php?";

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.trim().equalsIgnoreCase("Noregistra")){
                    mensajeAlertaTextViewError("No registro el elemento vuelva a intentarlo.", 3000);

                }else{
                    mensajeAlertaTextViewVerdadero("Elemento registrado con Exito. ", 2000);

                    elementos.remove(position);

                    adapter  = new ElementoAdapter(getApplicationContext(), elementos, R.layout.elemento_list_layout);
                    listView.setAdapter(adapter);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mensajeAlertaTextViewError("Ocurrio un error en el servidor al insertar elemento.", 3000);
                Log.i("Error", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> paramentros = new HashMap<>();
                paramentros.put("cantidad", cantidad);
                paramentros.put("id_elemento",elementos.get(position).getId_e().toString());
                paramentros.put("id_movimiento",idM);
                return paramentros;
            }
        };
        request.add(stringRequest);
    }

    private void showAlertElement(String title, final String idM, final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (title != null) builder.setTitle(title);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_element, null);
        builder.setView(viewInflated);

        final EditText input = (EditText) viewInflated.findViewById(R.id.editTextstok);

        builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String boardName = input.getText().toString().trim();
                if(boardName.length() > 0){
                    cargarWebServiceRegistrodetalleMovimiento(boardName, position, idM);
                }
                else
                    mensajeAlertaTextViewError("No ingresastes ningun valor.", 3000);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

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
