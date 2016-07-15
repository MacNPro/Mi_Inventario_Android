package com.llamas.miinventario;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Pedido extends Fragment {

    private DatabaseReference mDatabase;
    public static ArrayList<Producto> productos = new ArrayList<>();
    public static ArrayList<ProductoEnInventario> enPedido = new ArrayList<>();

    LinearLayout pedidoVacio;
    RelativeLayout ventana, mas, menos;
    ImageView cerrar, btnMas;
    MediumTextView totalView, cantidad, guardar;
    ListView listView;
    String pID;
    int total, cantidadDeProducto;

    boolean enVentana = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pedidos, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        totalView = (MediumTextView) view.findViewById(R.id.total);
        cantidad = (MediumTextView) view.findViewById(R.id.cantidad);
        guardar = (MediumTextView) view.findViewById(R.id.guardar);
        listView = (ListView) view.findViewById(R.id.listView);
        ventana = (RelativeLayout) view.findViewById(R.id.fondoVentana);
        pedidoVacio = (LinearLayout) view.findViewById(R.id.pedidoVacio);
        mas = (RelativeLayout) view.findViewById(R.id.mas);
        menos = (RelativeLayout) view.findViewById(R.id.menos);
        cerrar = (ImageView) view.findViewById(R.id.cerrar);
        btnMas = (ImageView) view.findViewById(R.id.btnMas);
        getPedido();

        btnMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Inicio.pedidoPager.setCurrentItem(2);
                Inicio.enCatalogo = true;
            }
        });

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
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (cantidadDeProducto == 0){
                    mDatabase.child("usuarios").child(user.getUid()).child("pedido").child(pID).removeValue();
                } else {
                    mDatabase.child("usuarios").child(user.getUid()).child("pedido").child(pID).setValue(cantidadDeProducto);
                }
                toggeVentana();
            }
        });

        totalView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (enPedido.size() > 0){
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Agregar Pedido")
                            .setMessage("¿Segura que quieres agregar los productos a tu inventario?")
                            .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    for (final ProductoEnInventario producto : enPedido) {
                                        mDatabase.child("usuarios").child(user.getUid()).child("inventario").child(producto.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                int nuevaCantidad;
                                                if (dataSnapshot.getValue(Integer.class) != null){
                                                    nuevaCantidad = dataSnapshot.getValue(Integer.class) + producto.getCantidad();
                                                } else {
                                                    nuevaCantidad =  producto.getCantidad();
                                                }
                                                mDatabase.child("usuarios").child(user.getUid()).child("inventario").child(producto.getId()).setValue(nuevaCantidad);
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                    mDatabase.child("usuarios").child(user.getUid()).child("pedido").setValue(null);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();
                }
            }
        });

        return view;
    }

    public void getPedido() {
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

                        if (enPedido.size() <= 0){
                            pedidoVacio.setVisibility(View.VISIBLE);
                            listView.setVisibility(View.GONE);
                        } else {
                            listView.setVisibility(View.VISIBLE);
                            pedidoVacio.setVisibility(View.GONE);
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
        String totalFinal = NumberFormat.getNumberInstance(Locale.US).format(total);
        totalView.setText("$" + totalFinal+".00");
        PedidosAdapter customAdapter = new PedidosAdapter(getActivity(), R.layout.list_item_pedido, productos, 1);
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
