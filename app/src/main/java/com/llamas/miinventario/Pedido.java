package com.llamas.miinventario;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.llamas.miinventario.CustomClasses.MediumTextView;
import com.llamas.miinventario.CustomClasses.PedidosAdapter;
import com.llamas.miinventario.CustomClasses.RegularTextView;
import com.llamas.miinventario.Model.Categoria;
import com.llamas.miinventario.Model.Producto;
import com.llamas.miinventario.Model.ProductoEnInventario;

import java.util.ArrayList;

public class Pedido extends Fragment {

    private DatabaseReference mDatabase;

    public static ArrayList<Producto> productos = new ArrayList<>();
    public static ArrayList<ProductoEnInventario> enPedido = new ArrayList<>();

    MediumTextView totalView;
    ListView listView;
    int total;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pedidos, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        totalView = (MediumTextView) view.findViewById(R.id.total);
        listView = (ListView) view.findViewById(R.id.listView);

        getInventario();

        return view;
    }

    public void getInventario() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.child("usuarios").child(user.getUid()).child("pedido").addValueEventListener(

                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        enPedido.clear();
                        for (DataSnapshot productos : dataSnapshot.getChildren()) {
                            ProductoEnInventario producto = new ProductoEnInventario();
                            producto.setCantidad(productos.getValue(Integer.class));
                            producto.setId(productos.getKey());
                            enPedido.add(producto);
                        }

                        crearArrayListView();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("TAG", "getUser:onCancelled", databaseError.toException());
                    }

                });
    }

    public void crearArrayListView() {
        mDatabase.child("catalogo").addValueEventListener(

                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        ArrayList<String> ids = new ArrayList<>();
                        ArrayList<Integer> cantidades = new ArrayList<>();
                        for (int i = 0; i < enPedido.size(); i++) {
                            ids.add(enPedido.get(i).getId());
                            cantidades.add(enPedido.get(i).getCantidad());
                            Log.d("TAG", "YEAH:" + enPedido.get(i).getCantidad());
                        }

                        total = 0;
                        productos.clear();
                        for (DataSnapshot categoria : dataSnapshot.getChildren()) {
                            for (DataSnapshot producto : categoria.getChildren()) {
                                Producto p = producto.getValue(Producto.class);
                                p.setId(Integer.valueOf(producto.getKey()));
                                if (ids.contains(producto.getKey())) {
                                    p.setCantidad(cantidades.get(ids.indexOf(producto.getKey())));
                                    total = (total + (cantidades.get(ids.indexOf(producto.getKey())) * p.getPrecio()));
                                    productos.add(p);
                                }
                            }
                        }
                        crearListView();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("TAG", "getUser:onCancelled", databaseError.toException());
                    }

                });

    }

    public void crearListView() {
        totalView.setText("Total: $" + total + ".00");
        PedidosAdapter customAdapter = new PedidosAdapter(getActivity(), R.layout.list_item, productos);
        listView.setAdapter(customAdapter);
    }

}
