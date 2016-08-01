package com.llamas.miinventario;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.llamas.miinventario.CustomClasses.MediumTextView;
import com.llamas.miinventario.CustomClasses.ResumenAdapter;
import com.llamas.miinventario.Model.Producto;
import com.llamas.miinventario.Model.ProductoEnInventario;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ClientaResumenVenta extends Fragment {

    private DatabaseReference mDatabase;

    ImageView cerrar;
    ListView listView;
    MediumTextView ttTotal;

    ArrayList<Producto> productos = new ArrayList<>();
    ArrayList<String> ids = new ArrayList<>();
    ArrayList<Integer> cantidades = new ArrayList<>();
    int total;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_clienta_resumen_venta, container, false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("usuarios").child(user.getUid());

        cerrar = (ImageView) v.findViewById(R.id.cerrar);
        listView = (ListView) v.findViewById(R.id.listView);
        ttTotal = (MediumTextView) v.findViewById(R.id.total);

        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DetailClienta)getActivity()).toggleResumen();
            }
        });

        obtenerProductosDeVenta();

        return v;
    }

    public void obtenerProductosDeVenta(){
        mDatabase.child("ventas").child(DetailClienta.ventaID).child("productos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ids.clear();
                cantidades.clear();
                for (DataSnapshot producto: dataSnapshot.getChildren()){
                    ids.add(producto.getKey());
                    cantidades.add(Integer.valueOf(producto.getValue(String.class)));
                }
                obtenerInfo();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void obtenerInfo(){
        FirebaseDatabase.getInstance().getReference().child("catalogo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                total = 0;
                for (DataSnapshot categoria: dataSnapshot.getChildren()){
                    for (DataSnapshot producto: categoria.getChildren()){
                        int index = ids.indexOf(producto.getKey());
                        if (ids.contains(producto.getKey())){
                            Log.d("TAG", "Ma NIGGA");
                            Producto p = producto.getValue(Producto.class);
                            p.setCantidad(cantidades.get(index));
                            total = total + (cantidades.get(index)*p.getPrecio());
                            productos.add(p);
                        }
                    }
                }
                llenarListView();
                String dineroTotal = NumberFormat.getNumberInstance(Locale.US).format(total);
                ttTotal.setText("Total: $" + dineroTotal);
                ((DetailClienta)getActivity()).toggleResumen();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void llenarListView() {
        ResumenAdapter customAdapter = new ResumenAdapter(getActivity(), R.layout.list_item_resumen, productos, productos.size());
        listView.setAdapter(customAdapter);
    }

}
