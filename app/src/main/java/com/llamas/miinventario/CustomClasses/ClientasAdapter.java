package com.llamas.miinventario.CustomClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.llamas.miinventario.Model.Clienta;
import com.llamas.miinventario.Model.Producto;
import com.llamas.miinventario.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by MacNPro on 7/9/16.
 */
public class ClientasAdapter extends ArrayAdapter<Clienta> {

    String type;

    public ClientasAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ClientasAdapter(Context context, int resource, List<Clienta> items, String type) {
        super(context, resource, items);
        this.type = type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_item_clienta, null);
        }

        Clienta c = getItem(position);

        if (c != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.nombre);
            TextView tt2 = (TextView) v.findViewById(R.id.cumpleaños);
            TextView tt3 = (TextView) v.findViewById(R.id.numDeVentas);

            if (tt1 != null) {
                tt1.setText(c.getNombre());
            }

            if (tt2 != null) {
                tt2.setText(c.getCumpleaños());
            }

            if (tt3 != null) {
                String numero = NumberFormat.getNumberInstance(Locale.US).format(c.getNumDeVentas());
                if (type.equals("venta")){
                    tt3.setText("" + numero);
                } else {
                    tt3.setText("$" + numero);
                }
            }
        }

        return v;
    }

}