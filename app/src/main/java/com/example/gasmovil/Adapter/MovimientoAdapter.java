package com.example.gasmovil.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gasmovil.Activity.Elemento;
import com.example.gasmovil.Entidades.Movimiento;
import com.example.gasmovil.R;

import java.util.List;

public class MovimientoAdapter extends RecyclerView.Adapter<MovimientoAdapter.MovimientoHolder> {



    List<Movimiento> movimientos;

    public MovimientoAdapter(List<Movimiento> movimientos) {
        this.movimientos = movimientos;
    }


    @Override
    public MovimientoAdapter.MovimientoHolder onCreateViewHolder( ViewGroup parent, int viewType) {

        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.movimiento_list_layout, parent,false );

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        vista.setLayoutParams(layoutParams);

        return new MovimientoHolder(vista);
    }

    @Override
    public void onBindViewHolder(MovimientoAdapter.MovimientoHolder holder, final int position) {

        if(movimientos.get(position).getEstado_m().equals("S.ASIGNAR")){
            holder.imageViewCheck.setImageResource(R.drawable.ic_build);
            holder.txtEstado.setText("SIN ELEMTOS");
        }

        if(movimientos.get(position).getEstado_m().equals("ASIGNADO")){
            holder.imageBtnAddElement.setEnabled(false);
            holder.imageBtnAddElement.setVisibility(View.INVISIBLE);
        }

        holder.txtFecha.setText(movimientos.get(position).getFecha_m());

        String nombre = movimientos.get(position).getNombre_t() + " " + movimientos.get(position).getApellido_t();
        holder.txtNom.setText(nombre);


        holder.imageBtnAddElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), Elemento.class);
                intent.putExtra("id", movimientos.get(position).getId_m());
                v.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return movimientos.size();
    }


    public class MovimientoHolder extends RecyclerView.ViewHolder{

        TextView txtFecha,txtEstado, txtNom;
        ImageButton imageBtnAddElement;
        ImageView imageViewCheck;

        public MovimientoHolder(View itemView) {
            super(itemView);
            txtFecha=itemView.findViewById(R.id.textFechaM);
            txtEstado=itemView.findViewById(R.id.textEstadoM);
            txtNom=itemView.findViewById(R.id.textNomM);
            imageBtnAddElement=itemView.findViewById(R.id.btnAddElemt);
            imageViewCheck=itemView.findViewById(R.id.imageCheck);
        }

    }
}
