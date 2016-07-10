package com.llamas.miinventario;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.llamas.miinventario.CustomClasses.RegularTextView;

public class DetailProducto extends FragmentActivity {

    private DatabaseReference mDatabase;

    RegularTextView botonAgregar;
    RelativeLayout secondLayout;
    FrameLayout fragmentLayout;
    TextView nombreTV, puntosTV, precioTV, idTV, enInventarioTV;
    public static String id, nombre, precio, puntos, enInventario;

    boolean enVentana = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_producto);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        botonAgregar = (RegularTextView) findViewById(R.id.botonAgregar);
        secondLayout = (RelativeLayout) findViewById(R.id.secondlayout);
        fragmentLayout = (FrameLayout) findViewById(R.id.fragment);
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

        float density = getResources().getDisplayMetrics().density;
        botonAgregar.setHeight(Math.round(density*48));

        nombreTV.setText(nombre);
        idTV.setText(id);
        precioTV.setText("$" + precio + ".00");
        puntosTV.setText("" + puntos + " Pts.");
        enInventarioTV.setText(enInventario);

    }

    public void toggleVentana(){
        if (enVentana){
            secondLayout.setVisibility(View.GONE);
        } else {
            secondLayout.setVisibility(View.VISIBLE);
        }
        enVentana = !enVentana;
    }

    public void onAgregarAPedido(View v){
        AgregarAPedido newFragment = new AgregarAPedido();
        iniciarFragmento(newFragment);
        toggleVentana();
    }

    public void iniciarFragmento(Fragment newFragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void onRegresar(View v){
        finish();
    }

    public void onAtras(View v){
        toggleVentana();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
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

    }

}
