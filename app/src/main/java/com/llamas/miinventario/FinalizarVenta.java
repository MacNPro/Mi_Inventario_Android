package com.llamas.miinventario;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
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
import com.llamas.miinventario.CustomClasses.ResumenAdapter;
import com.llamas.miinventario.Model.Producto;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class FinalizarVenta extends FragmentActivity {

    private DatabaseReference mDatabase;
    public static ArrayList<Producto> productos = new ArrayList<>();

    MediumTextView totalTV, finalizar;
    ListView listView;
    RelativeLayout ventana;

    public static int total;
    boolean enVentana;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalizar_venta);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Intent i = getIntent();
        productos = (ArrayList<Producto>) i.getSerializableExtra("Productos");
        total = i.getIntExtra("Total", 0);

        totalTV = (MediumTextView) findViewById(R.id.total);
        finalizar = (MediumTextView) findViewById(R.id.finalizar);
        listView = (ListView) findViewById(R.id.listView);
        ventana = (RelativeLayout) findViewById(R.id.fondoVentana);

        String totalFinal = NumberFormat.getNumberInstance(Locale.US).format(total);
        totalTV.setText("Subtotal: $" + totalFinal + ".00");

        crearListView();

        Calculadora newFragment = new Calculadora();
        iniciarFragmento(newFragment);

        finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalizarVenta();
            }
        });

    }

    public void sumarTotal() {
        total = 0;
        for (Producto producto : productos) {
            if (producto.getId() != 69) {
                total = total + (producto.getCantidad() * producto.getPrecio());
            } else {
                total = total - producto.getPrecio();
            }
        }
        String totalFinal = NumberFormat.getNumberInstance(Locale.US).format(total);
        totalTV.setText("Total: $" + totalFinal + ".00");
    }

    public void crearListView() {
        ResumenAdapter customAdapter = new ResumenAdapter(this, R.layout.list_item_resumen, productos, productos.size());
        listView.setAdapter(customAdapter);
    }

    public void onRegresar(View v) {
        finish();
    }

    public void abrirVentana(View v) {
        toggeVentana();
    }

    public void iniciarFragmento(Fragment newFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void cerrarVentana(View v) {
        toggeVentana();
    }

    public void toggeVentana() {

        if (enVentana) {
            ventana.setVisibility(View.GONE);
        } else {
            ventana.setVisibility(View.VISIBLE);
        }

        enVentana = !enVentana;
    }

    public void finalizarVenta() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.child("usuarios").child(user.getUid()).child("ventas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long ventaId = dataSnapshot.getChildrenCount();
                for (Producto producto : productos) {
                    int pID = producto.getId();
                    int pCantidad = producto.getCantidad();
                    mDatabase.child("usuarios").child(user.getUid()).child("ventas").child("" + ventaId).child("" + pID).setValue("" + pCantidad);
                }
                mDatabase.child("usuarios").child(user.getUid()).child("ventas").child("" + ventaId).child("total").setValue(total);
                String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                mDatabase.child("usuarios").child(user.getUid()).child("ventas").child("" + ventaId).child("tiempo").setValue(date);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        for (Producto producto : productos) {
            final int pID = producto.getId();
            final int pCantidad = producto.getCantidad();
            if (pID != 69) {
                mDatabase.child("usuarios").child(user.getUid()).child("inventario").child("" + pID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mDatabase.child("usuarios").child(user.getUid()).child("inventario").child("" + pID).setValue(dataSnapshot.getValue(Integer.class) - pCantidad);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
        mDatabase.child("usuarios").child(user.getUid()).child("venta").setValue(null);
        finish();
    }

}
