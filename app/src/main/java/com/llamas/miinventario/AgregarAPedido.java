package com.llamas.miinventario;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AgregarAPedido extends Fragment {

    private DatabaseReference mDatabase;

    TextView cantidad, agregarProductos;

    int cantidadIntCounter = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agregar_a_pedido, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        cantidad = (TextView) view.findViewById(R.id.cantidad);
        agregarProductos = (TextView) view.findViewById(R.id.agregarProductos);

        cantidad.setText("" + cantidadIntCounter);

        agregarProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cantidadIntCounter > 0){
                    mDatabase.child("usuarios").child(user.getUid()).child("pedido").child(DetailProducto.id).setValue(cantidadIntCounter);
                    ((DetailProducto)getActivity()).toggleVentana();
                }
            }
        });

        RelativeLayout mas = (RelativeLayout) view.findViewById(R.id.mas);
        mas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cantidadIntCounter++;
                cantidad.setText("" + cantidadIntCounter);
                if (cantidadIntCounter > 0){
                    agregarProductos.setAlpha(1f);
                    agregarProductos.setClickable(true);
                }
            }
        });

        RelativeLayout menos = (RelativeLayout) view.findViewById(R.id.menos);
        menos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cantidadIntCounter > 0){
                    cantidadIntCounter--;
                    cantidad.setText("" + cantidadIntCounter);
                }
                if (cantidadIntCounter == 0){
                    agregarProductos.setAlpha(0.6f);
                    agregarProductos.setClickable(false);
                }
            }
        });

        return view;
    }

}
