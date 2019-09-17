package com.example.gasmovil.Adapter;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gasmovil.Entidades.Actividad;
import com.example.gasmovil.R;

import java.util.List;

public class ProgramacionAdapter extends RecyclerView.Adapter<ProgramacionAdapter.ProgramacionHolder> {

    List<Actividad> actividads;

    public ProgramacionAdapter(List<Actividad> actividads) {
        this.actividads = actividads;

    }


    @Override
    public ProgramacionAdapter.ProgramacionHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.actividad_list_layout, parent,false );

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT);

        vista.setLayoutParams(layoutParams);

        return new ProgramacionHolder(vista);
    }

    @Override
    public void onBindViewHolder(ProgramacionAdapter.ProgramacionHolder holder, final int position) {
        final String nombre = actividads.get(position).getNombre() + " " + actividads.get(position).getApellido();
        holder.txtNom.setText(nombre);

        holder.txtObra.setText(actividads.get(position).getObra());

        String dir = actividads.get(position).getDireccion() + " Barrio " + actividads.get(position).getBarrio();
        holder.txtDireccion.setText(dir);
        holder.txtNumElement.setText(actividads.get(position).getNum_element());


    }

    @Override
    public int getItemCount() {
        return actividads.size();
    }


    public class ProgramacionHolder extends RecyclerView.ViewHolder{

        TextView txtNom,txtObra,txtDireccion, txtNumElement;
        ImageButton imageBtnInfo;

        public ProgramacionHolder(View itemView) {
            super(itemView);
            txtNom=itemView.findViewById(R.id.textNombreA);
            txtObra=itemView.findViewById(R.id.textObraA);
            txtNumElement=itemView.findViewById(R.id.image_letter);
            txtDireccion=itemView.findViewById(R.id.textDireccionA);
            imageBtnInfo =itemView.findViewById(R.id.btnInfoA);
        }
    }


}
