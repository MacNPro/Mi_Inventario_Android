package com.llamas.miinventario;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.llamas.miinventario.CustomClasses.ClientasAdapter;
import com.llamas.miinventario.Model.Clienta;

import java.util.ArrayList;

public class Clientas extends FragmentActivity {

    private DatabaseReference mDatabase;

    ListView listView;
    RelativeLayout viewVentana;
    FrameLayout fragment;

    ArrayList<Clienta> clientas = new ArrayList<>();
    public static int numeroDeClientas = 0;
    boolean enVentana = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientas);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("usuarios").child(user.getUid());

        listView = (ListView) findViewById(R.id.listView);
        viewVentana = (RelativeLayout) findViewById(R.id.viewVentana);
        fragment = (FrameLayout) findViewById(R.id.fragment);

        obtenerClientas();

        iniciarFragmento(new AgregarClienta().newInstance("Clientas"));

        viewVentana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(Clientas.this);
                toggleVentana();
            }
        });

    }

    private void obtenerClientas(){
        mDatabase.child("clientas").orderByChild("nombre").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                clientas.clear();
                numeroDeClientas = (int) dataSnapshot.getChildrenCount();
                for(DataSnapshot clienta: dataSnapshot.getChildren()){
                    String nombre = clienta.child("nombre").getValue(String.class);
                    String cumpleaños = clienta.child("cumpleaños").getValue(String.class);
                    int id = Integer.valueOf(clienta.getKey());
                    int ventasTotal = 0;
                    for (DataSnapshot venta: clienta.child("ventas").getChildren()){
                        int tempCantidad = venta.getValue(Integer.class);
                        ventasTotal = ventasTotal + tempCantidad;
                    }
                    Clienta c = new Clienta(nombre, cumpleaños, ventasTotal, id);
                    clientas.add(c);
                }
                llenarListView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void llenarListView(){
        ClientasAdapter customAdapter = new ClientasAdapter(this, R.layout.list_item_pedido, clientas, "clientas");
        listView.setAdapter(customAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), DetailClienta.class);
                intent.putExtra("clienta", clientas.get(i));
                startActivity(intent);
            }
        });
    }

    public void agregarClienta(View v){
        toggleVentana();
    }

    public void toggleVentana() {
        if (enVentana){
            viewVentana.setVisibility(View.GONE);
        } else {
            viewVentana.setVisibility(View.VISIBLE);
        }
        enVentana = !enVentana;
    }

    private void iniciarFragmento(Fragment newFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void onRegresar(View v){
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

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

}
