package com.llamas.miinventario.CustomClasses;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.llamas.miinventario.Model.Producto;
import com.llamas.miinventario.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by MacNPro on 7/9/16.
 */
public class ResumenAdapter extends ArrayAdapter<Producto> {

    int arraySize;

    public ResumenAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ResumenAdapter(Context context, int resource, List<Producto> items, int arraySize) {
        super(context, resource, items);
        this.arraySize = arraySize;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_item_resumen, null);
        }

        Producto p = getItem(position);

        if (p != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.nombre);
            TextView tt2 = (TextView) v.findViewById(R.id.precio);
            TextView tt3 = (TextView) v.findViewById(R.id.cantidad);

            if (tt1 != null) {
                tt1.setText(p.getNombre());
            }

            if (tt2 != null) {
                String totalFinal = NumberFormat.getNumberInstance(Locale.US).format((p.getPrecio()*p.getCantidad()));
                tt2.setText("$" + totalFinal + ".00");
            }

            if (tt3 != null) {
                tt3.setText("x" + p.getCantidad());
            }

            if (p.getId() == 69){
                LinearLayout linearLayout = (LinearLayout) v.findViewById(R.id.item);
                linearLayout.setBackgroundColor(Color.rgb(245,245,245));
                tt2.setBackgroundColor(Color.rgb(245,245,245));
                tt3.setBackgroundColor(Color.rgb(245,245,245));
            }

            if (p.getId() != 69){
                LinearLayout linearLayout = (LinearLayout) v.findViewById(R.id.item);
                linearLayout.setBackgroundColor(Color.rgb(255,255,255));
                tt2.setBackgroundColor(Color.rgb(255,255,255));
                tt3.setBackgroundColor(Color.rgb(255,255,255));
            }

        }

        return v;
    }

}