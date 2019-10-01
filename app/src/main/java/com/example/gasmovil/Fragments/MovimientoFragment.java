package com.example.gasmovil.Fragments;


import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.example.gasmovil.Adapter.ElementoAdapter;
import com.example.gasmovil.Adapter.MovimientoAdapter;
import com.example.gasmovil.Entidades.AllCodigo;
import com.example.gasmovil.Entidades.Codigo;
import com.example.gasmovil.Entidades.Movimiento;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class MovimientoFragment extends Fragment {

    private View vista;
    private TextView mensajeCon;
    private RecyclerView recycler;

    private RequestQueue request;
    private JsonObjectRequest jsonObjectRequest;
    StringRequest stringRequest;

    ArrayList<Movimiento> movimientos;
    ArrayList<AllCodigo> allCodigos;

    private Codigo codig;

    private SharedPreferences preferencias;
    String nom_base_datos = "usuarios_gas";
    String id;



    public MovimientoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista = inflater.inflate(R.layout.fragment_movimiento, container, false);

        movimientos = new ArrayList<>();
        allCodigos = new ArrayList<>();

        codig = Codigo.getIntanse();

        request = Volley.newRequestQueue(getContext());
        preferencias = this.getActivity().getSharedPreferences(nom_base_datos,getContext().MODE_PRIVATE);

        id = preferencias.getString("Sid","");

        mensajeCon = (TextView) vista.findViewById(R.id.TextViewMensajeConfirmacionFM);
        recycler = vista.findViewById(R.id.idRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recycler.setHasFixedSize(true);


        cargarcodiValidation(id);
        //cargarWebServiceMovimiento(id);
        time(id);
        com.getbase.floatingactionbutton.FloatingActionButton fab = (com.getbase.floatingactionbutton.FloatingActionButton) vista.findViewById(R.id.fabM);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertMovimiento("Ingresa Codigo");
            }
        });

        return vista;
    }

    private void cargarcodiValidation(final String id) {
        String url = Utilidades_Request.HTTP + Utilidades_Request.IP + Utilidades_Request.CARPETA + "wsJsonConsultaAllActivity.php";

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();

                JSONArray json = response.optJSONArray("actividades");
                AllCodigo allCodigo = null;
                if(allCodigos.size()>0){
                    allCodigos.clear();
                }

                try {
                    for (int i = 0; i < json.length(); i++) {

                        JSONObject jsonObject = null;
                        jsonObject = json.getJSONObject(i);

                        allCodigo = new AllCodigo();
                        allCodigo.setCod(jsonObject.optString("codigo"));
                        allCodigos.add(allCodigo);
                    }

                   cargarWebServiceMovimiento(id);

                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                cargarWebServiceMovimiento(id);
                mensajeAlertaTextViewError("Error no hay conexion con la base de datos..", 3000);
            }
        });
        request.add(jsonObjectRequest);
    }

    private void cargarWebServiceMovimiento(String id) {
        String url = Utilidades_Request.HTTP + Utilidades_Request.IP + Utilidades_Request.CARPETA + "wsJsonConsultaMovimiento.php?id_user="+id;

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();

                JSONArray json = response.optJSONArray("movimiento");
                Movimiento movimiento = null;
                if(movimientos.size()>0){
                    movimientos.clear();
                }

                try {
                    for (int i = 0; i < json.length(); i++) {

                        JSONObject jsonObject = null;
                        jsonObject = json.getJSONObject(i);

                        movimiento = new Movimiento();
                        movimiento.setId_m(jsonObject.optInt("pk_id_m"));
                        movimiento.setFecha_m(jsonObject.optString("fecha_movimiento"));
                        movimiento.setEstado_m(jsonObject.optString("estado"));
                        movimiento.setNombre_t(jsonObject.optString("NOM"));
                        movimiento.setCodigo(jsonObject.optString("CODE"));
                        movimientos.add(movimiento);
                    }

                    if (movimientos.get(0).getId_m() == 0 || movimientos.get(0).getId_m() == 00){
                        mensajeAlertaTextViewError("No hay Informacion para Visualizar!.", 3000);
                    }else {
                        MovimientoAdapter adapter = new MovimientoAdapter(movimientos);
                        recycler.setAdapter(adapter);
                    }


                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mensajeAlertaTextViewError("Error no hay conexion con la base de datos..", 3000);
            }
        });
        request.add(jsonObjectRequest);
    }

    private void showAlertMovimiento(String title) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        if (title != null) builder.setTitle(title);

        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_movimiento, null);
        builder.setView(viewInflated);

        final EditText input = (EditText) viewInflated.findViewById(R.id.editTextstok);

        if(codig.getCode() != null){
            input.setText(codig.getCode());
        }

        builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String boardName = input.getText().toString().trim();
                if(boardName.length() > 0){
                    int c=0;
                    for (int i =0; i<allCodigos.size(); i++){
                        if (allCodigos.get(i).getCod()==boardName){
                            c=1;
                        }
                    }
                    if (c == 0){
                        cargarWebServiceRegistroMovimiento(boardName);
                    }else {
                        mensajeAlertaTextViewError("Este codigo ya esta Registrado!.", 3000);
                    }
                }
                else
                    mensajeAlertaTextViewError("No ingresastes ningun valor", 3000);
            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void cargarWebServiceRegistroMovimiento(final String codigo) {

        String url = Utilidades_Request.HTTP+Utilidades_Request.IP+Utilidades_Request.CARPETA+"wsJsonRegistroMovimiento.php?";

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.trim().equalsIgnoreCase("Noregistra")){
                    mensajeAlertaTextViewError("No registro ocurrio un error.. ", 3000);

                }else if(response.trim().equalsIgnoreCase("no")){
                    mensajeAlertaTextViewError("Codigo no existe en la base de datos", 3000);
                }else if(response.trim().equalsIgnoreCase("nono")){
                    mensajeAlertaTextViewError("Codigo ya Registrado!!", 3000);
                }
                else{
                    mensajeAlertaTextViewVerdadero("Movimiento registrado con Exito!.", 2000);
                    cargarWebServiceMovimiento(id);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mensajeAlertaTextViewError("Ocurrio un error en el servidor ", 3000);
                Log.i("Error", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> paramentros = new HashMap<>();
                paramentros.put("id_persona", id);
                paramentros.put("codigo", codigo);
                return paramentros;
            }
        };
        request.add(stringRequest);
    }

    public void time(final String id){
        Timer timer = new Timer();
        TimerTask t = new TimerTask() {
            @Override
            public void run() {
                cargarcodiValidation(id);
                System.out.println("1");
            }
        };
        timer.scheduleAtFixedRate(t,30000,30000);
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
