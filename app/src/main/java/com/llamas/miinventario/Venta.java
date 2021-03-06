package com.llamas.miinventario;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.llamas.miinventario.CustomClasses.VentasAdapter;
import com.llamas.miinventario.Model.Producto;
import com.llamas.miinventario.Model.ProductoEnInventario;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Venta extends Fragment {

    private DatabaseReference mDatabase;
    public static ArrayList<Producto> productos = new ArrayList<>();
    public static ArrayList<ProductoEnInventario> productosAVender = new ArrayList<>();

    LinearLayout ventaVacia;
    RelativeLayout ventana, mas, menos;
    ImageView cerrar;
    MediumTextView subtotal;
    MediumTextView cantidad, guardar, fraseDisponible;
    ListView listView;
    String pID;
    int cantidadDeProductoAVender, cantidadDeProductoDisponible, cantidadDeProductoDisponibleLive, total;

    boolean enVentana = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_venta, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        subtotal = (MediumTextView) view.findViewById(R.id.total);
        cantidad = (MediumTextView) view.findViewById(R.id.cantidad);
        guardar = (MediumTextView) view.findViewById(R.id.guardar);
        fraseDisponible = (MediumTextView) view.findViewById(R.id.fraseDisponible);
        listView = (ListView) view.findViewById(R.id.listView);
        ventana = (RelativeLayout) view.findViewById(R.id.fondoVentana);
        ventaVacia = (LinearLayout) view.findViewById(R.id.ventaVacia);
        mas = (RelativeLayout) view.findViewById(R.id.mas);
        menos = (RelativeLayout) view.findViewById(R.id.menos);
        cerrar = (ImageView) view.findViewById(R.id.cerrar);
        ImageView btnMas = (ImageView) view.findViewById(R.id.btnMas);
        getVenta();

        btnMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Inicio.ventasPager.setCurrentItem(1);
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
                if (cantidadDeProductoAVender < cantidadDeProductoDisponible){
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
                    mDatabase.child("usuarios").child(user.getUid()).child("venta").child(""+pID).removeValue();
                } else {
                    mDatabase.child("usuarios").child(user.getUid()).child("venta").child(""+pID).setValue(cantidadDeProductoAVender);
                }
                toggeVentana();
            }
        });

        subtotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), FinalizarVenta.class);
                i.putExtra("Productos", productos);
                i.putExtra("Total", total);
                startActivity(i);
            }
        });

        return view;
    }

    public void getVenta() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.child("usuarios").child(user.getUid()).child("venta").addValueEventListener(

                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        productosAVender.clear();
                        for (DataSnapshot productos : dataSnapshot.getChildren()) {
                            ProductoEnInventario producto = new ProductoEnInventario();
                            producto.setCantidad(productos.getValue(Integer.class));
                            producto.setId(productos.getKey());
                            productosAVender.add(producto);
                        }
                        if (productosAVender.size() <= 0){
                            ventaVacia.setVisibility(View.VISIBLE);
                            listView.setVisibility(View.GONE);
                        } else {
                            listView.setVisibility(View.VISIBLE);
                            ventaVacia.setVisibility(View.GONE);
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
                        for (int i = 0; i < productosAVender.size(); i++) {
                            ids.add(productosAVender.get(i).getId());
                            cantidades.add(productosAVender.get(i).getCantidad());
                        }

                        productos.clear();
                        ArrayList<Integer> precios = new ArrayList();
                        for (DataSnapshot categoria : dataSnapshot.getChildren()) {
                            for (DataSnapshot producto : categoria.getChildren()) {
                                Producto p = producto.getValue(Producto.class);
                                p.setId(Integer.valueOf(producto.getKey()));
                                if (ids.contains(producto.getKey())) {
                                    p.setCantidad(cantidades.get(ids.indexOf(producto.getKey())));
                                    precios.add(p.getPrecio());
                                    productos.add(p);
                                }
                            }
                        }

                        total = 0;
                        for (Producto p : productos) {
                            total = total + (p.getPrecio()*p.getCantidad());
                        }
                        if (total <= 0){
                            subtotal.setClickable(false);
                        } else {
                            subtotal.setClickable(true);
                        }
                        String totalFinal = NumberFormat.getNumberInstance(Locale.US).format(total);
                        subtotal.setText("Subtotal: $" + totalFinal+".00");
                        crearListView();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("TAG", "getUser:onCancelled", databaseError.toException());
                    }

                });

    }

    public void crearListView() {
        if (getActivity() != null) {
            VentasAdapter customAdapter = new VentasAdapter(getActivity(), R.layout.list_item_pedido, productos, 0);
            listView.setAdapter(customAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    final int index = i;
                    pID = String.valueOf(productos.get(i).getId());
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    mDatabase.child("usuarios").child(user.getUid()).child("inventario").child(pID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            cantidadDeProductoDisponible = dataSnapshot.getValue(Integer.class);
                            cantidadDeProductoAVender = productos.get(index).getCantidad();
                            cantidad.setText("" + cantidadDeProductoAVender);
                            cantidadDeProductoDisponibleLive = cantidadDeProductoDisponible - cantidadDeProductoAVender;
                            fraseDisponible.setText("Actualmente tienes " + cantidadDeProductoDisponibleLive + " productos disponibles para vender");
                            toggeVentana();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
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
