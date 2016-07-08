package com.llamas.miinventario;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Pedidos extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        TextView texto = (TextView) view.findViewById(R.id.texto);
        texto.setText("Estas en el Pedidos");

        return view;
    }

}
