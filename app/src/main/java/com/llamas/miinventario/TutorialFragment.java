package com.llamas.miinventario;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.llamas.miinventario.CustomClasses.BoldTextView;
import com.llamas.miinventario.CustomClasses.MediumTextView;
import com.llamas.miinventario.CustomClasses.RegularTextView;

public class TutorialFragment extends Fragment {

    MediumTextView texto;
    MediumTextView titulo;
    ImageView imagen;
    LinearLayout imagenLayout;

    public static TutorialFragment newInstance(String type) {
        TutorialFragment fragment = new TutorialFragment();
        Bundle args = new Bundle();
        args.putString("Type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tutorial, container, false);

        titulo = (MediumTextView) v.findViewById(R.id.titulo);
        texto = (MediumTextView) v.findViewById(R.id.texto);
        imagen = (ImageView) v.findViewById(R.id.imagen);
        imagenLayout = (LinearLayout) v.findViewById(R.id.imagenLayout);

        String type = getArguments().getString("Type");
        loadType(type);

        return v;
    }

    public void loadType(String type){
        switch (type){
            case "Inicio":
                titulo.setText("¡Bienvenida!");
                Glide.with(this).load(R.drawable.bnr).into(imagen);
                texto.setText("Disfruta de 30 días gratis y\nve como crece tu negocio");
                imagenLayout.setVisibility(View.VISIBLE);
                return;
            case "Inventario":
                titulo.setText("Inventario");
                Glide.with(this).load(R.drawable.screenshoot_inventario).into(imagen);
                texto.setText("Lleva tu inventario a cualquier lado,\nsolo agrega tus productos");
                imagenLayout.setVisibility(View.VISIBLE);
                return;
            case "Pedido":
                titulo.setText("Pedido");
                Glide.with(this).load(R.drawable.screenshoot_pedido).into(imagen);
                texto.setText("Agrega productos a tu lista de pedido para que no se te olvide nada");
                imagenLayout.setVisibility(View.VISIBLE);
                return;
            case "Venta":
                titulo.setText("Venta");
                Glide.with(this).load(R.drawable.screenshoot_venta).into(imagen);
                texto.setText("Lleva un registro de todas tus ventas y analiza tus ganancias");
                imagenLayout.setVisibility(View.VISIBLE);
                return;
            case "Clientas":
                titulo.setText("Clientas");
                Glide.with(this).load(R.drawable.screenshoot_clientas).into(imagen);
                texto.setText("Un directorio de clientas, donde ves todo lo que le has vendido");
                imagenLayout.setVisibility(View.VISIBLE);
                return;
            default:
                return;
        }
    }

}
