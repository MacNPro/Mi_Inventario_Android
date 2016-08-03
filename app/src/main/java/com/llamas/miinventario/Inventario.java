package com.llamas.miinventario;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.llamas.miinventario.CustomClasses.MediumTextView;
import com.llamas.miinventario.Model.Categoria;
import com.llamas.miinventario.Model.ExpandableListAdapter;
import com.llamas.miinventario.Model.Producto;
import com.llamas.miinventario.Model.ProductoEnInventario;

import java.util.ArrayList;

public class Inventario extends Fragment {

    private DatabaseReference mDatabase;

    ExpandableListAdapter expandableListAdapter;
    ExpandableListView expandableListView;

    public static ArrayList<Categoria> categorias = new ArrayList<>();
    public static ArrayList<ProductoEnInventario> enInventario = new ArrayList<>();
    public static ArrayList<String> idsEnVenta = new ArrayList<>();

    LinearLayout inventarioVacio, inventarioVacioVentas;
    MediumTextView guardar, cantidad, fraseDisponible;
    ImageView cerrar;
    RelativeLayout ventana, mas, menos;
    String type;

    int cantidadDeProductoAVender, cantidadDeProductoDisponible, cantidadDeProductoDisponibleLive, pid;

    boolean enVentana = false;

    public static Inventario newInstance(String type) {
        Bundle args = new Bundle();
        args.putString("Type", type);
        Inventario fragment = new Inventario();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventario, container, false);
        type = getArguments().getString("Type");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        getInventario();
        expandableListView = (ExpandableListView) view.findViewById(R.id.expandableListView);
        inventarioVacio = (LinearLayout) view.findViewById(R.id.inventarioVacio);
        inventarioVacioVentas = (LinearLayout) view.findViewById(R.id.inventarioVacioVentas);
        ImageView btnMas = (ImageView) view.findViewById(R.id.btnMas);

        btnMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Inicio.inventarioPager.setCurrentItem(1);
                Inicio.enCatalogo = true;
            }
        });

        if (type.equals("Venta")) {
            getidsEnVenta();
            btnMas.setVisibility(View.GONE);
            ventana = (RelativeLayout) view.findViewById(R.id.fondoVentana);
            mas = (RelativeLayout) view.findViewById(R.id.mas);
            menos = (RelativeLayout) view.findViewById(R.id.menos);
            cerrar = (ImageView) view.findViewById(R.id.cerrar);
            guardar = (MediumTextView) view.findViewById(R.id.guardar);
            cantidad = (MediumTextView) view.findViewById(R.id.cantidad);
            fraseDisponible = (MediumTextView) view.findViewById(R.id.fraseDisponible);

            cerrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggeVentana();
                }
            });
            mas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cantidadDeProductoAVender < cantidadDeProductoDisponible) {
                        cantidadDeProductoAVender++;
                        cantidadDeProductoDisponibleLive--;
                        fraseDisponible.setText("Actualmente tienes " + cantidadDeProductoDisponibleLive + " productos disponibles para vender");
                        cantidad.setText("" + cantidadDeProductoAVender);
                    }
                }
            });

            menos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cantidadDeProductoAVender > 0) {
                        cantidadDeProductoAVender--;
                        cantidadDeProductoDisponibleLive++;
                        fraseDisponible.setText("Actualmente tienes " + cantidadDeProductoDisponibleLive + " productos disponibles para vender");
                        cantidad.setText("" + cantidadDeProductoAVender);
                    }
                }
            });

            guardar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (cantidadDeProductoAVender == 0) {
                        mDatabase.child("usuarios").child(user.getUid()).child("venta").child("" + pid).removeValue();
                    } else {
                        mDatabase.child("usuarios").child(user.getUid()).child("venta").child("" + pid).setValue(cantidadDeProductoAVender);
                    }
                    toggeVentana();
                }
            });
        }

        return view;
    }

    public void getInventario() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.child("usuarios").child(user.getUid()).child("inventario").addValueEventListener(

                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        enInventario.clear();
                        for (DataSnapshot productos : dataSnapshot.getChildren()) {
                            ProductoEnInventario producto = new ProductoEnInventario();
                            int cantidad = productos.getValue(Integer.class);
                            if (type.equals("Inventario")) {
                                producto.setCantidad(cantidad);
                                producto.setId(productos.getKey());
                                enInventario.add(producto);
                            } else {
                                if (cantidad > 0) {
                                    producto.setCantidad(cantidad);
                                    producto.setId(productos.getKey());
                                    enInventario.add(producto);
                                }
                            }
                        }
                        if (enInventario.size() <= 0) {
                            if (!type.equals("Venta")) {
                                inventarioVacio.setVisibility(View.VISIBLE);
                                inventarioVacioVentas.setVisibility(View.GONE);
                            } else {
                                inventarioVacio.setVisibility(View.GONE);
                                inventarioVacioVentas.setVisibility(View.VISIBLE);
                            }
                        } else {
                            inventarioVacio.setVisibility(View.GONE);
                            inventarioVacioVentas.setVisibility(View.GONE);
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
                        for (int i = 0; i < enInventario.size(); i++) {
                            ids.add(enInventario.get(i).getId());
                            cantidades.add(enInventario.get(i).getCantidad());
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

    public void getidsEnVenta() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.child("usuarios").child(user.getUid()).child("venta").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                idsEnVenta.clear();
                for (DataSnapshot producto : dataSnapshot.getChildren()) {
                    idsEnVenta.add(producto.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void crearListView() {
        if (getActivity() != null) {
            expandableListAdapter = new ExpandableListAdapter(getActivity(), categorias);
            expandableListView.deferNotifyDataSetChanged();
            expandableListView.setAdapter(expandableListAdapter);
            expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                    final Producto producto = categorias.get(groupPosition).getProductos().get(childPosition);
                    if (type.equals("Inventario")) {

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
                        i.putExtra("Type", "Inventario");
                        startActivity(i);

                    } else {
                        pid = producto.getId();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (idsEnVenta.contains(String.valueOf(pid))) {
                            mDatabase.child("usuarios").child(user.getUid()).child("venta").child("" + pid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    cantidadDeProductoAVender = dataSnapshot.getValue(Integer.class);
                                    cantidadDeProductoDisponible = producto.getCantidad();
                                    cantidadDeProductoDisponibleLive = cantidadDeProductoDisponible - cantidadDeProductoAVender;
                                    fraseDisponible.setText("Actualmente tienes " + cantidadDeProductoDisponibleLive + " productos disponibles para vender");
                                    cantidad.setText("" + cantidadDeProductoAVender);
                                    toggeVentana();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        } else {
                            cantidadDeProductoAVender = 0;
                            cantidadDeProductoDisponible = producto.getCantidad();
                            cantidadDeProductoDisponibleLive = cantidadDeProductoDisponible - cantidadDeProductoAVender;
                            fraseDisponible.setText("Actualmente tienes " + cantidadDeProductoDisponibleLive + " productos disponibles para vender");
                            cantidad.setText("" + cantidadDeProductoAVender);
                            toggeVentana();
                        }
                    }

                    return true;
                }

            });
        }

    }

    public void toggeVentana() {

        if (enVentana) {
            ventana.setVisibility(View.GONE);
        } else {
            ventana.setVisibility(View.VISIBLE);
        }

        enVentana = !enVentana;
    }

}
