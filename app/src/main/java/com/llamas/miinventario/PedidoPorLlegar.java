package com.llamas.miinventario;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

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

public class PedidoPorLlegar extends Activity {

    private DatabaseReference mDatabase;

    ArrayList<Producto> productos = new ArrayList<>();
    ArrayList<String> ids = new ArrayList<>();
    ArrayList<Integer> cant = new ArrayList<>();

    ListView listView;
    MediumTextView titulo;
    RelativeLayout ventana, mas, menos;
    ImageView cerrar;
    MediumTextView cantidad, guardar;
    String pID;
    int cantidadDeProducto;
    FirebaseUser user;

    boolean enVentana = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_por_llegar);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("usuarios").child(user.getUid()).child("pedidoPorLlegar");

        cantidad = (MediumTextView) findViewById(R.id.cantidad);
        guardar = (MediumTextView) findViewById(R.id.guardar);
        listView = (ListView) findViewById(R.id.listView);
        ventana = (RelativeLayout) findViewById(R.id.fondoVentana);
        mas = (RelativeLayout) findViewById(R.id.mas);
        menos = (RelativeLayout) findViewById(R.id.menos);
        cerrar = (ImageView) findViewById(R.id.cerrar);
        titulo = (MediumTextView) findViewById(R.id.titulo);

        obtenerTitulo();

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
                if (cantidadDeProducto == 0){
                    mDatabase.child("productos").child(pID).removeValue();
                } else {
                    mDatabase.child("productos").child(pID).setValue(cantidadDeProducto);
                }
                toggeVentana();
            }
        });

    }

    private void obtenerTitulo() {
        mDatabase.child("tiempo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String tiempo = dataSnapshot.getValue(String.class);
                titulo.setText("Pedido del " + tiempo);
                obtenerPedido();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void obtenerPedido() {
        mDatabase.child("productos").addValueEventListener(

                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        ids.clear();
                        cant.clear();

                        for (DataSnapshot producto : dataSnapshot.getChildren()) {
                            ids.add(producto.getKey());
                            cant.add(producto.getValue(Integer.class));
                        }

                        obtenerInfo();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("TAG", "getUser:onCancelled", databaseError.toException());
                    }

                });
    }

    private void obtenerInfo() {
        FirebaseDatabase.getInstance().getReference().child("catalogo").addValueEventListener(

                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        productos.clear();

                        for (DataSnapshot categoria : dataSnapshot.getChildren()) {
                            for (DataSnapshot producto : categoria.getChildren()) {
                                Producto p = producto.getValue(Producto.class);
                                p.setId(Integer.valueOf(producto.getKey()));
                                if (ids.contains(producto.getKey())) {
                                    p.setCantidad(cant.get(ids.indexOf(producto.getKey())));
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

    private void crearListView() {
        PedidosAdapter customAdapter = new PedidosAdapter(this, R.layout.list_item_pedido, productos, 0);
        listView.setAdapter(customAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                toggeVentana();
                cantidadDeProducto = productos.get(i).getCantidad();
                pID = String.valueOf(productos.get(i).getId());
                cantidad.setText(""+cantidadDeProducto);
            }
        });
    }

    private void toggeVentana() {
        if (enVentana){
            ventana.setVisibility(View.GONE);
        } else {
            ventana.setVisibility(View.VISIBLE);
        }

        enVentana = !enVentana;
    }

    public void agregarAInventario(View v) {
        new AlertDialog.Builder(this)
                .setTitle("Agregar Productos")
                .setMessage("Recomendamos agregar los productos al inventario una vez que haya llegado tu pedido.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        for (final Producto producto : productos) {
                            FirebaseDatabase.getInstance().getReference().child("usuarios").child(user.getUid()).child("inventario").child(String.valueOf(producto.getId()))
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            int nuevaCantidad;
                                            if (dataSnapshot.getValue(Integer.class) != null) {
                                                nuevaCantidad = dataSnapshot.getValue(Integer.class) + producto.getCantidad();
                                            } else {
                                                nuevaCantidad = producto.getCantidad();
                                            }
                                            FirebaseDatabase.getInstance().getReference().child("usuarios").child(user.getUid()).child("inventario").child(String.valueOf(producto.getId())).setValue(nuevaCantidad);
                                            mDatabase.setValue(null);
                                            Toast.makeText(getApplicationContext(), "Los productos se agregaron correctamente", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }
                        finish();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    public void onRegresar(View v) {
        finish();
    }

}
