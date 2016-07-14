package com.llamas.miinventario;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.llamas.miinventario.CustomClasses.MediumTextView;
import com.llamas.miinventario.CustomClasses.PedidosAdapter;
import com.llamas.miinventario.Model.Producto;
import com.llamas.miinventario.Model.ProductoEnInventario;

import java.util.ArrayList;

public class PorAgotarse extends Fragment {

    private DatabaseReference mDatabase;
    public static ArrayList<Producto> productos = new ArrayList<>();
    public static ArrayList<ProductoEnInventario> Agotados = new ArrayList<>();

    RelativeLayout ventana, mas, menos;
    ImageView cerrar;
    MediumTextView cantidad, guardar;
    ListView listView;
    String pID;
    int cantidadDeProducto;

    boolean enVentana = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_por_agotarse, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        cantidad = (MediumTextView) view.findViewById(R.id.cantidad);
        guardar = (MediumTextView) view.findViewById(R.id.guardar);
        listView = (ListView) view.findViewById(R.id.listView);
        ventana = (RelativeLayout) view.findViewById(R.id.fondoVentana);
        mas = (RelativeLayout) view.findViewById(R.id.mas);
        menos = (RelativeLayout) view.findViewById(R.id.menos);
        cerrar = (ImageView) view.findViewById(R.id.cerrar);
        getPedido();

        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggeVentana();
            }
        });

        mas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cantidadDeProducto++;
                cantidad.setText("" + cantidadDeProducto);
            }
        });

        menos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cantidadDeProducto > 0){
                    cantidadDeProducto--;
                    cantidad.setText("" + cantidadDeProducto);
                }
            }
        });

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (cantidadDeProducto == 0){
                    mDatabase.child("usuarios").child(user.getUid()).child("pedido").child(pID).removeValue();
                } else {
                    mDatabase.child("usuarios").child(user.getUid()).child("pedido").child(pID).setValue(cantidadDeProducto);
                }
                toggeVentana();
            }
        });

        return view;
    }

    public void getPedido() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.child("usuarios").child(user.getUid()).child("inventario").addValueEventListener(

                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Agotados.clear();
                        for (DataSnapshot productos : dataSnapshot.getChildren()) {
                            ProductoEnInventario producto = new ProductoEnInventario();
                            int cantidad = productos.getValue(Integer.class);
                            if (cantidad <= 2 && cantidad >= 0){
                                producto.setCantidad(productos.getValue(Integer.class));
                                producto.setId(productos.getKey());
                                Agotados.add(producto);
                            }
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
                        for (int i = 0; i < Agotados.size(); i++) {
                            ids.add(Agotados.get(i).getId());
                            cantidades.add(Agotados.get(i).getCantidad());
                        }

                        productos.clear();
                        for (DataSnapshot categoria : dataSnapshot.getChildren()) {
                            for (DataSnapshot producto : categoria.getChildren()) {
                                Producto p = producto.getValue(Producto.class);
                                p.setId(Integer.valueOf(producto.getKey()));
                                if (ids.contains(producto.getKey())) {
                                    p.setCantidad(cantidades.get(ids.indexOf(producto.getKey())));
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
        PedidosAdapter customAdapter = new PedidosAdapter(getActivity(), R.layout.list_item_pedido, productos, 0);
        listView.setAdapter(customAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                toggeVentana();
                cantidadDeProducto = productos.get(i).getCantidad();
                pID = String.valueOf(productos.get(i).getId());
                Log.d("Cantidad", ""+cantidadDeProducto);
                cantidad.setText(""+cantidadDeProducto);
            }
        });
    }

    public void toggeVentana(){

        if (enVentana){
            ventana.setVisibility(View.GONE);
        } else {
            ventana.setVisibility(View.VISIBLE);
        }

        enVentana = !enVentana;
    }

}
