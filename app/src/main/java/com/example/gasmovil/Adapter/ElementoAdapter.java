package com.example.gasmovil.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.Response;
import com.example.gasmovil.Entidades.Element;
import com.example.gasmovil.R;

import org.json.JSONObject;

import java.util.List;

public class ElementoAdapter extends BaseAdapter {

    private Context context;
    private List<Element> list;
    private int layout;

    public ElementoAdapter(Context context, List<Element> list, int layout) {
        this.context = context;
        this.list = list;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder vh;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(layout, null);
            vh = new ViewHolder();
            vh.txtNombre = (TextView) convertView.findViewById(R.id.textViewNombreE);
            vh.txtCodigo = (TextView) convertView.findViewById(R.id.textViewCodigoE);

            convertView.setTag(vh);

        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        Element element = list.get(position);
        vh.txtNombre.setText(element.getNombre_e());
        vh.txtNombre.setTextColor(R.color.design_default_color_primary_dark);

        vh.txtCodigo.setText(element.getCodigo_e());
        vh.txtCodigo.setTextColor(R.color.colorPrimary);

        return convertView;
    }

    public class ViewHolder {
        TextView txtNombre,txtCodigo;
    }
}

