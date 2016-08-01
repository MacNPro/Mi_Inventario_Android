package com.llamas.miinventario;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.support.v4.app.FragmentActivity;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.llamas.miinventario.CustomClasses.BoldTextView;
import com.llamas.miinventario.CustomClasses.ClientaVentasAdapter;
import com.llamas.miinventario.CustomClasses.MediumTextView;
import com.llamas.miinventario.Model.Clienta;
import com.llamas.miinventario.Model.VentaListaObj;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DetailClienta extends FragmentActivity {

    private DatabaseReference mDatabase;

    ListView listView;
    RelativeLayout ventanaResumen;

    ArrayList<VentaListaObj> ventaLista = new ArrayList<>();
    ArrayList<String> ventaIDs = new ArrayList<>();
    ArrayList<Integer> ventaTotales = new ArrayList<>();
    Clienta clienta;
    public static String ventaID;
    boolean enResumen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_clienta);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("usuarios").child(user.getUid());

        Intent i = getIntent();
        clienta = (Clienta) i.getSerializableExtra("clienta");


        BoldTextView ttNombre = (BoldTextView) findViewById(R.id.ttNombre);
        MediumTextView ttCumple = (MediumTextView) findViewById(R.id.ttCumple);
        listView = (ListView) findViewById(R.id.listView);
        ventanaResumen = (RelativeLayout) findViewById(R.id.ventanaResumen);

        ttNombre.setText(clienta.getNombre());
        ttCumple.setText(clienta.getCumpleaños());

        obtenerVentasDeClienta();

    }

    private void obtenerVentasDeClienta(){
        mDatabase.child("clientas").child("" + clienta.getId()).child("ventas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ventaIDs.clear();
                ventaTotales.clear();
                for (DataSnapshot venta: dataSnapshot.getChildren()){
                    ventaIDs.add(venta.getKey());
                    ventaTotales.add(venta.getValue(Integer.class));
                }
                obtenerVentaInfo();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void obtenerVentaInfo(){
        int i = 0;
        for (String id : ventaIDs) {
            i++;
            final int y = i;
            mDatabase.child("ventas").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String fecha = dataSnapshot.child("tiempo").getValue(String.class);
                    int total = dataSnapshot.child("total").getValue(Integer.class);
                    VentaListaObj ventaObj = new VentaListaObj(ventaIDs.get(y - 1), total, fecha);
                    ventaLista.add(ventaObj);
                    if (y == ventaIDs.size()){
                        llenarListView();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void llenarListView(){
        ClientaVentasAdapter customAdapter = new ClientaVentasAdapter(this, R.layout.list_item_pedido, ventaLista);
        listView.setAdapter(customAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ventaID = ventaLista.get(i).getId();
                iniciarFragmento(new ClientaResumenVenta());
            }
        });
    }

    public void cerrarResumen(View v){
        toggleResumen();
    }

    public void toggleResumen(){
        if (enResumen){
            ventanaResumen.setVisibility(View.GONE);
        } else {
            ventanaResumen.setVisibility(View.VISIBLE);
        }
        enResumen = !enResumen;
    }

    private void iniciarFragmento(Fragment newFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentResumen, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void onRegresar(View v){
        finish();
    }

}
