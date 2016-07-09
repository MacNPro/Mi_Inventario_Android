package com.llamas.miinventario;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class DetailProducto extends Activity {

    private DatabaseReference mDatabase;

    TextView nombreTV, puntosTV, precioTV, idTV, enInventarioTV;
    String id, nombre, precio, puntos, enInventario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_producto);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        nombreTV = (TextView) findViewById(R.id.nombre);
        idTV = (TextView) findViewById(R.id.id);
        precioTV = (TextView) findViewById(R.id.precio);
        puntosTV = (TextView) findViewById(R.id.puntos);
        enInventarioTV = (TextView) findViewById(R.id.enInventario);

        Intent i = getIntent();
        id = i.getStringExtra("ID");
        nombre = i.getStringExtra("Nombre");
        precio = i.getStringExtra("Precio");
        puntos = i.getStringExtra("Puntos");
        enInventario = i.getStringExtra("enInventario");

        nombreTV.setText(nombre);
        idTV.setText(id);
        precioTV.setText("$" + precio + ".00");
        puntosTV.setText("" + puntos + " Pts.");
        enInventarioTV.setText(enInventario);

    }

    public void onAgregarAPedido(View v){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            mDatabase.child("usuarios").child(uid).child("inventario").child(id).setValue(1);
        }
    }

    public void onRegresar(View v){
        finish();
    }

}
