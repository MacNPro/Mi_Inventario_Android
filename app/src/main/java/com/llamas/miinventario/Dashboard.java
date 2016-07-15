package com.llamas.miinventario;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.llamas.miinventario.CustomClasses.BoldTextView;
import com.llamas.miinventario.CustomClasses.MediumTextView;
import com.llamas.miinventario.CustomClasses.RegularTextView;

public class Dashboard extends Fragment {

    private DatabaseReference mDatabase;

    MediumTextView bienvenida;
    BoldTextView porAgotarse, enInventario, agotados, ventas;
    int porAgotarseInt, enInventarioInt, agotadosInt;
    long ventasInt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        bienvenida = (MediumTextView) view.findViewById(R.id.nombre);
        porAgotarse = (BoldTextView) view.findViewById(R.id.porAgotarse);
        enInventario = (BoldTextView) view.findViewById(R.id.enInventario);
        agotados = (BoldTextView) view.findViewById(R.id.agotados);
        ventas = (BoldTextView) view.findViewById(R.id.ventas);

        cargarUsuario();
        cargarInformacion();

        return view;
    }

    public void cargarUsuario() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.getDisplayName() != null) {
            String nombre = user.getDisplayName();
            String[] nombreSplit = nombre.split("\\s+");
            bienvenida.setText("Bienvenida " + nombreSplit[0]);
        } else {
            mDatabase.child("usuarios").child(user.getUid()).child("nombre").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String nombre = dataSnapshot.getValue(String.class);
                    String[] nombreSplit = nombre.split("\\s+");
                    bienvenida.setText("Bienvenida " + nombreSplit[0]);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void cargarInformacion() {
        porAgotarseInt = 0;
        enInventarioInt = 0;
        agotadosInt = 0;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.child("usuarios").child(user.getUid()).child("inventario").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot producto : dataSnapshot.getChildren()) {
                    int cantidad = producto.getValue(Integer.class);
                    if (cantidad <= 0) {
                        agotadosInt++;
                    } else if (cantidad > 0 && cantidad <= 2) {
                        porAgotarseInt++;
                    }
                    enInventarioInt = enInventarioInt + cantidad;
                }
                porAgotarse.setText("" + porAgotarseInt);
                enInventario.setText("" + enInventarioInt);
                agotados.setText("" + agotadosInt);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mDatabase.child("usuarios").child(user.getUid()).child("ventas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ventasInt = dataSnapshot.getChildrenCount();
                ventas.setText("" + ventasInt);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
