package com.llamas.miinventario;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.llamas.miinventario.CustomClasses.MediumTextView;

public class DetailProducto extends Activity {

    private DatabaseReference mDatabase;
    MediumTextView botonAgregar;
    TextView nombreTV, puntosTV, precioTV, idTV, enInventarioTV;
    public static String id, nombre, precio, puntos, enInventario, type;

    TextView cantidad;

    int cantidadIntCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_producto);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        botonAgregar = (MediumTextView) findViewById(R.id.botonAgregar);
        nombreTV = (TextView) findViewById(R.id.nombre);
        idTV = (TextView) findViewById(R.id.id);
        precioTV = (TextView) findViewById(R.id.precio);
        puntosTV = (TextView) findViewById(R.id.puntos);
        enInventarioTV = (TextView) findViewById(R.id.enInventario);
        cantidad = (TextView) findViewById(R.id.cantidad);

        Intent i = getIntent();
        id = i.getStringExtra("ID");
        nombre = i.getStringExtra("Nombre");
        precio = i.getStringExtra("Precio");
        puntos = i.getStringExtra("Puntos");
        enInventario = i.getStringExtra("enInventario");
        type = i.getStringExtra("Type");

        if (type.equals("Pedido")) {
            botonAgregar.setText("Agregar a Pedido");
        }

        nombreTV.setText(nombre);
        idTV.setText(id);
        precioTV.setText("$" + precio + ".00");
        puntosTV.setText("" + puntos + " Pts.");
        enInventarioTV.setText(enInventario);

        obtenerCantidadEnPedido();

        RelativeLayout mas = (RelativeLayout) findViewById(R.id.mas);
        mas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cantidadIntCounter++;
                cantidad.setText("" + cantidadIntCounter);
                if (cantidadIntCounter > 0) {
                    botonAgregar.setAlpha(1f);
                    botonAgregar.setClickable(true);
                }
            }
        });

        RelativeLayout menos = (RelativeLayout) findViewById(R.id.menos);
        menos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cantidadIntCounter > 0) {
                    cantidadIntCounter--;
                    cantidad.setText("" + cantidadIntCounter);
                }
                if (cantidadIntCounter == 0) {
                    botonAgregar.setAlpha(0.6f);
                    botonAgregar.setClickable(false);
                }
            }
        });

    }

    public void obtenerCantidadEnPedido() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String key;
        if (type.equals("Inventario")) {
            key = "inventario";
        } else {
            key = "pedido";
        }
        mDatabase.child("usuarios").child(user.getUid()).child(key).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (type.equals("Inventario")) {
                        cantidadIntCounter = 0;
                        cantidad.setText("" + cantidadIntCounter);
                        botonAgregar.setAlpha(0.6f);
                        botonAgregar.setClickable(false);
                    } else {
                        cantidadIntCounter = dataSnapshot.getValue(Integer.class);
                        cantidad.setText("" + cantidadIntCounter);
                    }
                } else {
                    cantidadIntCounter = 0;
                    cantidad.setText("" + cantidadIntCounter);
                    botonAgregar.setAlpha(0.6f);
                    botonAgregar.setClickable(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onAgregar(View v) {
        if (type.equals("Inventario")) {
            if (cantidadIntCounter > 0) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                mDatabase.child("usuarios").child(user.getUid()).child("inventario").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue(Integer.class) != null) {
                            int cantidadEnInventario = cantidadIntCounter + dataSnapshot.getValue(Integer.class);
                            mDatabase.child("usuarios").child(user.getUid()).child("inventario").child(id).setValue(cantidadEnInventario);
                        } else {
                            mDatabase.child("usuarios").child(user.getUid()).child("inventario").child(id).setValue(cantidadIntCounter);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        } else {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (cantidadIntCounter == 0) {
                mDatabase.child("usuarios").child(user.getUid()).child("pedido").child(id).removeValue();
            } else {
                mDatabase.child("usuarios").child(user.getUid()).child("pedido").child(id).setValue(cantidadIntCounter);
            }
        }
        finish();
    }

    public void onRegresar(View v) {
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
