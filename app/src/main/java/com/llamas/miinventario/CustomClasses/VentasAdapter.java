package com.llamas.miinventario.CustomClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.llamas.miinventario.Model.Producto;
import com.llamas.miinventario.R;

import java.util.List;

/**
 * Created by MacNPro on 7/9/16.
 */
public class VentasAdapter extends ArrayAdapter<Producto> {

    int pagerIndex;

    public VentasAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public VentasAdapter(Context context, int resource, List<Producto> items, int pagerIndex) {
        super(context, resource, items);
        this.pagerIndex = pagerIndex;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_item_pedido, null);
        }

        Producto p = getItem(position);

        if (p != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.nombre);
            TextView tt2 = (TextView) v.findViewById(R.id.precio);
            TextView tt3 = (TextView) v.findViewById(R.id.enInventario);

            if (tt1 != null) {
                tt1.setText(p.getNombre());
            }

            if (tt2 != null) {
                tt2.setText("$" + p.getPrecio() + ".00");
            }

            if (tt3 != null) {
                tt3.setText(String.valueOf(p.getCantidad()));
            }
        }

        return v;
    }

}