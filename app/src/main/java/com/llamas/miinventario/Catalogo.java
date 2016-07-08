package com.llamas.miinventario;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Catalogo extends Fragment {

    private String title;
    private int page;

    public static Catalogo newInstance(int page, String title) {
        Catalogo catalogo = new Catalogo();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        catalogo.setArguments(args);
        return catalogo;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_catalogo, container, false);

        TextView texto = (TextView) view.findViewById(R.id.texto);
        texto.setText("Estas en el " + title);

        return view;
    }

}
