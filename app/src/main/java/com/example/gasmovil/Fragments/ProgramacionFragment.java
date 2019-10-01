package com.example.gasmovil.Fragments;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.example.gasmovil.Adapter.ProgramacionAdapter;
import com.example.gasmovil.Entidades.Actividad;
import com.example.gasmovil.Entidades.Codigo;
import com.example.gasmovil.Entidades.Movimiento;
import com.example.gasmovil.OnClickListener.RecyclerItemClickListener;
import com.example.gasmovil.R;
import com.example.gasmovil.Utilidades.Utilidades_Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProgramacionFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private View vista;

    private TextView mensajeCon;
    private RecyclerView recycler;
    private SwipeRefreshLayout swipeRefresh;

    private RequestQueue request;
    private JsonObjectRequest jsonObjectRequest;
    private StringRequest stringRequest;

    ArrayList<Actividad> actividads;

    private Codigo codig;
    private SharedPreferences preferencias;
    private String nom_base_datos = "usuarios_gas";
    private String id;
    public ProgramacionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista = inflater.inflate(R.layout.fragment_programacion, container, false);

        actividads = new ArrayList<>();

        codig = Codigo.getIntanse();

        request = Volley.newRequestQueue(getContext());
        preferencias = this.getActivity().getSharedPreferences(nom_base_datos,getContext().MODE_PRIVATE);

        id = preferencias.getString("Sid","");

        mensajeCon = (TextView) vista.findViewById(R.id.TextViewMensajeConfirmacionFP);
        swipeRefresh = (SwipeRefreshLayout) vista.findViewById(R.id.swipeR);
        recycler = vista.findViewById(R.id.idRecyclerPr);
        recycler.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recycler.setHasFixedSize(true);

        recycler.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), recycler ,new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        showAlertPrincipal(actividads.get(position).getUsuario(), position);
                        // do whatever
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

        swipeRefresh.setOnRefreshListener(this);
        swipeRefresh.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        cargarWebServiceActivid(id);


        return vista;
    }

    private void cargarWebServiceActivid(final String id) {
        if(actividads.size()>0){
            actividads.clear();
        }
        String url = Utilidades_Request.HTTP + Utilidades_Request.IP + Utilidades_Request.CARPETA + "wsJsonConsultaProgramacion.php?id_user="+id;

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();

                JSONArray json = response.optJSONArray("actividad");
                Actividad actividad = null;

                try {
                    for (int i = 0; i < json.length(); i++) {

                        JSONObject jsonObject = null;
                        jsonObject = json.getJSONObject(i);

                        actividad = new Actividad();
                        actividad.setId(jsonObject.optInt("ID"));
                        actividad.setDireccion(jsonObject.optString("DIRECCION"));
                        actividad.setCodigo(jsonObject.optString("CODIGO"));
                        actividad.setUsuario(jsonObject.optString("USUARIO"));
                        actividad.setTelefono(jsonObject.optString("telefono"));
                        actividad.setFecha(jsonObject.optString("FECHA"));
                        actividad.setObra(jsonObject.optString("OBRA"));
                        if (jsonObject.optString("NUM_ELEMENT").equals("0")){
                            actividad.setNum_element(null);
                        }else{
                            actividad.setNum_element(jsonObject.optString("NUM_ELEMENT"));
                        }

                        actividads.add(actividad);
                    }

                    swipeRefresh.setRefreshing(false);
                    if (actividads.size()==0){
                        mensajeAlertaTextViewError("No hay Informacion para visualizar!.", 3000);
                    }else {
                        ProgramacionAdapter adapter = new ProgramacionAdapter(actividads);
                        recycler.setAdapter(adapter);
                    }


                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefresh.setRefreshing(false);
                mensajeAlertaTextViewError("Error no hay conexion con la base de datos", 3000);
            }
        });
        request.add(jsonObjectRequest);
    }



    private void showAlertPrincipal(final String title, final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        if (title != null) builder.setTitle(title);

        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_programacion_principal, null);
        builder.setView(viewInflated);

        final AlertDialog dialog = builder.create();


        final Button info = (Button) viewInflated.findViewById(R.id.btnInfoDA);
        final Button novedad = (Button) viewInflated.findViewById(R.id.btnNovedadDA);
        final Button construida = (Button) viewInflated.findViewById(R.id.btnConstruidaDA);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertInfo(title,position);
                dialog.cancel();
            }
        });

        novedad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(actividads.get(position).getNum_element() == null){
                    mensajeAlertaTextViewError("Tienes que registrar primero los elementos!.", 3000);
                }else {
                    showAlertNovedad(title,position);
                }

                dialog.cancel();
            }
        });

        construida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(actividads.get(position).getNum_element() == null){
                    mensajeAlertaTextViewError("Tienes que registrar primero los elementos!.", 3000);
                }else {
                    showAlertConstruida(title, position);
                }

                dialog.cancel();
            }
        });


        dialog.show();
    }

    private void showAlertInfo(String title, final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        if (title != null) builder.setTitle(title);

        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_info_actividad, null);
        builder.setView(viewInflated);

        final TextView telefono = (TextView) viewInflated.findViewById(R.id.textViewTelefonoDA);
        final TextView codigo = (TextView) viewInflated.findViewById(R.id.textViewCodigoDA);
        final TextView obra = (TextView) viewInflated.findViewById(R.id.textViewFechaDA);
        final ImageButton add = (ImageButton) viewInflated.findViewById(R.id.btn_add_code);





        if(actividads.get(position).getNum_element()!=null){
            add.setVisibility(viewInflated.GONE);
        }
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codig.setCode(actividads.get(position).getCodigo());
                add.setEnabled(false);
                add.setImageResource(R.drawable.ic_done);
            }
        });

        telefono.setText(actividads.get(position).getTelefono());
        codigo.setText(actividads.get(position).getCodigo());
        obra.setText(actividads.get(position).getFecha());



        builder.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void showAlertConstruida(String title, final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        if (title != null) builder.setTitle(title);

        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_construida_programacion, null);
        builder.setView(viewInflated);

        final EditText medidor = (EditText) viewInflated.findViewById(R.id.editMedidorDA);
        final EditText acta = (EditText) viewInflated.findViewById(R.id.editTextActaDA);

        builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String medi = medidor.getText().toString().trim();
                String ac = acta.getText().toString().trim();
                if(ac.length() > 0){

                    cargarWebServiceUpdateConstruida(actividads.get(position).getId().toString(),medi,ac);
                }
                else
                    mensajeAlertaTextViewError("No ingresaste ningun numero de acta.", 3000);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void cargarWebServiceUpdateConstruida(final String acti, final String medi, final String act) {

        String url = Utilidades_Request.HTTP+Utilidades_Request.IP+Utilidades_Request.CARPETA+"wsJsonUpdateConstuida.php?";

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.trim().equalsIgnoreCase("Noregistra")){
                    mensajeAlertaTextViewError("No registro ocurrio un error vuelva a intentarlo. ", 3000);

                }else{
                    mensajeAlertaTextViewVerdadero("Obra construida registrada con Exito.", 2000);
                    cargarWebServiceActivid(id);

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
                paramentros.put("id", acti);
                paramentros.put("medidor", medi);
                paramentros.put("acta", act);
                return paramentros;
            }
        };
        request.add(stringRequest);
    }


    private void cargarWebServiceRegisterNovedad(final String acti, final String novedad) {

        String url = Utilidades_Request.HTTP+Utilidades_Request.IP+Utilidades_Request.CARPETA+"wsJsonRegistroNovedad.php?";

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.trim().equalsIgnoreCase("Noregistra")){
                    mensajeAlertaTextViewError("No registro ocurrio un error vuelva a intentarlo.", 3000);

                }else{
                    mensajeAlertaTextViewVerdadero("Novedad registrada con Exito.", 2000);

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mensajeAlertaTextViewError("Ocurrio un error con el SERVIDOR.", 3000);
                Log.i("Error", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> paramentros = new HashMap<>();
                paramentros.put("id", acti);
                paramentros.put("novedad", novedad);
                return paramentros;
            }
        };
        request.add(stringRequest);
    }

    private void showAlertNovedad(String title, final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        if (title != null) builder.setTitle(title);

        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_novedad_programacion, null);
        builder.setView(viewInflated);

        final EditText input = (EditText) viewInflated.findViewById(R.id.editNovedadDA);

        builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String boardName = input.getText().toString().trim();
                if(boardName.length() > 0){
                    cargarWebServiceRegisterNovedad(actividads.get(position).getId().toString(), boardName);
                }
                else
                    mensajeAlertaTextViewError("No ingresastes ningun valor", 3000);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

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

    @Override
    public void onRefresh() {
        cargarWebServiceActivid(id);
    }
}
