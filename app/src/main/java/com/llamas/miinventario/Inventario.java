package com.llamas.miinventario;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.llamas.miinventario.Model.Categoria;
import com.llamas.miinventario.Model.ExpandableListAdapter;
import com.llamas.miinventario.Model.Producto;
import com.llamas.miinventario.Model.ProductoEnInventario;

import java.util.ArrayList;

public class Inventario extends Fragment {

    private static String TAG = "Frag-Inventario";
    private DatabaseReference mDatabase;

    ExpandableListAdapter expandableListAdapter;
    ExpandableListView expandableListView;

    public static ArrayList<Categoria> categorias = new ArrayList<>();
    public static ArrayList<ProductoEnInventario> enInventario = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventario, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        getInventario();
        expandableListView = (ExpandableListView) view.findViewById(R.id.expandableListView);

        return view;
    }

    public void getInventario() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.child("usuarios").child(user.getUid()).child("inventario").addListenerForSingleValueEvent(

                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        enInventario.clear();
                        for (DataSnapshot productos : dataSnapshot.getChildren()) {
                            ProductoEnInventario producto = new ProductoEnInventario();
                            producto.setCantidad(productos.getValue(Integer.class));
                            producto.setId(productos.getKey());
                            enInventario.add(producto);
                        }

                        crearArrayListView();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("TAG", "getUser:onCancelled", databaseError.toException());
                    }

                });
    }

    public void crearListView() {

        expandableListAdapter = new ExpandableListAdapter(getActivity(), categorias);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                Producto producto = categorias.get(groupPosition).getProductos().get(childPosition);

                String pID = String.valueOf(producto.getId());
                String nombre = String.valueOf(producto.getNombre());
                String precio = String.valueOf(producto.getPrecio());
                String puntos = String.valueOf(producto.getPuntos());
                String enInventario = String.valueOf(producto.getCantidad());

                Intent i = new Intent(getActivity(), DetailProducto.class);
                i.putExtra("ID", pID);
                i.putExtra("Nombre", nombre);
                i.putExtra("Precio", precio);
                i.putExtra("Puntos", puntos);
                i.putExtra("enInventario", enInventario);
                startActivity(i);

                return true;
            }

        });

    }

    public void crearArrayListView() {
        mDatabase.child("catalogo").addListenerForSingleValueEvent(

                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        ArrayList<String> ids = new ArrayList<>();
                        ArrayList<Integer> cantidades = new ArrayList<>();
                        for (int i = 0; i < enInventario.size(); i++) {
                            ids.add(enInventario.get(i).getId());
                            cantidades.add(enInventario.get(i).getCantidad());
                            Log.d("TAG", "YEAH:" + enInventario.get(i).getCantidad());
                        }

                        categorias.clear();
                        Categoria c;
                        for (DataSnapshot categoria : dataSnapshot.getChildren()) {

                            ArrayList<Producto> productos = new ArrayList<>();
                            for (DataSnapshot producto : categoria.getChildren()) {
                                Producto p = producto.getValue(Producto.class);
                                p.setId(Integer.valueOf(producto.getKey()));
                                if (ids.contains(producto.getKey())) {
                                    p.setCantidad(cantidades.get(ids.indexOf(producto.getKey())));
                                    productos.add(p);
                                }
                            }

                            if (productos.size() > 0) {
                                c = new Categoria(categoria.getKey(), productos);
                                categorias.add(c);
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

}
