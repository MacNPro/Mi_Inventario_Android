package com.llamas.miinventario;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
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
import com.llamas.miinventario.CustomClasses.ClientasAdapter;
import com.llamas.miinventario.CustomClasses.MediumTextView;
import com.llamas.miinventario.CustomClasses.ResumenAdapter;
import com.llamas.miinventario.Model.Clienta;
import com.llamas.miinventario.Model.Producto;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FinalizarVenta extends FragmentActivity {

    private DatabaseReference mDatabase;
    public static ArrayList<Producto> productos = new ArrayList<>();
    ArrayList<Clienta> clientas = new ArrayList<>();

    MediumTextView totalTV, finalizar, nombre, agregarClienta;
    ListView listView, clientasListView;
    RelativeLayout ventana, ventanaClientas, ventanaAgregarC;

    String uID;
    public static int clientaID = -1;
    public static int total;
    boolean enVentana, enClientas, enAgregarC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalizar_venta);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        uID = user.getUid();

        Intent i = getIntent();
        productos = (ArrayList<Producto>) i.getSerializableExtra("Productos");
        total = i.getIntExtra("Total", 0);

        totalTV = (MediumTextView) findViewById(R.id.total);
        finalizar = (MediumTextView) findViewById(R.id.finalizar);
        nombre = (MediumTextView) findViewById(R.id.nombre);
        agregarClienta = (MediumTextView) findViewById(R.id.agregarClienta);
        listView = (ListView) findViewById(R.id.listView);
        clientasListView = (ListView) findViewById(R.id.clientasListView);
        ventana = (RelativeLayout) findViewById(R.id.fondoVentana);
        ventanaClientas = (RelativeLayout) findViewById(R.id.fondoClienta);
        ventanaAgregarC = (RelativeLayout) findViewById(R.id.fondoAgregarC);

        String totalFinal = NumberFormat.getNumberInstance(Locale.US).format(total);
        totalTV.setText("Total: $" + totalFinal + ".00");

        crearListView();
        obtenerClientas();

        Calculadora newFragment = new Calculadora();
        iniciarFragmento(newFragment);

        iniciarAgregarC(new AgregarClienta().newInstance("Ventas"));

        agregarClienta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleClientas();
                toggleAgregarC();
            }
        });

        nombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleClientas();
            }
        });

        finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalizarVenta();
            }
        });

    }

    public void setNombre(String s){
        nombre.setText(s);
    }

    private void obtenerClientas() {
        mDatabase.child("usuarios").child(uID).child("clientas").orderByChild("nombre").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                clientas.clear();
                for (DataSnapshot clienta : dataSnapshot.getChildren()) {
                    String nombre = clienta.child("nombre").getValue(String.class);
                    String cumpleaños = clienta.child("cumpleaños").getValue(String.class);
                    int ventasTotal = (int) clienta.child("ventas").getChildrenCount();
                    int id = Integer.valueOf(clienta.getKey());
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

    public void iniciarFragmento(Fragment newFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void iniciarAgregarC(Fragment newFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentAgregarC, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void abrirVentana(View v) {
        toggeVentana();
    }

    public void abrirClientas(View v) {
        toggleClientas();
    }

    public void abrirAgregarC(View v) {
        toggleAgregarC();
    }

    public void cerrarAgregarC(View v) {
        toggleAgregarC();
    }

    public void cerrarVentana(View v) {
        toggeVentana();
    }

    public void cerrarClientas(View v) {
        toggleClientas();
    }

    public void toggeVentana() {

        if (enVentana) {
            ventana.setVisibility(View.GONE);
        } else {
            ventana.setVisibility(View.VISIBLE);
        }

        enVentana = !enVentana;
    }

    public void toggleAgregarC() {

        if (enAgregarC) {
            ventanaAgregarC.setVisibility(View.GONE);
        } else {
            ventanaAgregarC.setVisibility(View.VISIBLE);
        }

        enAgregarC = !enAgregarC;
    }

    public void toggleClientas() {

        if (enClientas) {
            ventanaClientas.setVisibility(View.GONE);
        } else {
            ventanaClientas.setVisibility(View.VISIBLE);
        }

        enClientas = !enClientas;
    }

    public void llenarListView() {
        ClientasAdapter customAdapter = new ClientasAdapter(this, R.layout.list_item_pedido, clientas, "venta");
        clientasListView.setAdapter(customAdapter);
        clientasListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                clientaID = clientas.get(i).getId();
                nombre.setText(clientas.get(i).getNombre());
                toggleClientas();
            }
        });
    }

    public void finalizarVenta() {
        if (clientaID != -1) {
            mDatabase.child("usuarios").child(uID).child("ventas").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long ventaId = dataSnapshot.getChildrenCount();
                    for (Producto producto : productos) {
                        int pID = producto.getId();
                        int pCantidad = producto.getCantidad();
                        mDatabase.child("usuarios").child(uID).child("ventas").child("" + ventaId).child("productos").child("" + pID).setValue("" + pCantidad);
                        if (pID == 69){
                            mDatabase.child("usuarios").child(uID).child("ventas").child("" + ventaId).child("descuento").setValue(producto.getPrecio());
                        }
                    }
                    mDatabase.child("usuarios").child(uID).child("ventas").child("" + ventaId).child("total").setValue(total);
                    String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                    mDatabase.child("usuarios").child(uID).child("ventas").child("" + ventaId).child("tiempo").setValue(date);
                    mDatabase.child("usuarios").child(uID).child("ventas").child("" + ventaId).child("clienta").setValue(clientaID);
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/usuarios/" + uID + "/clientas/" + clientaID + "/ventas/" + ventaId, total);
                    mDatabase.updateChildren(childUpdates);
                    Toast.makeText(getApplicationContext(), "La venta se realizó exitosamente", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            for (Producto producto : productos) {
                final int pID = producto.getId();
                final int pCantidad = producto.getCantidad();
                if (pID != 69) {
                    mDatabase.child("usuarios").child(uID).child("inventario").child("" + pID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mDatabase.child("usuarios").child(uID).child("inventario").child("" + pID).setValue(dataSnapshot.getValue(Integer.class) - pCantidad);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
            mDatabase.child("usuarios").child(uID).child("venta").setValue(null);
            finish();

        } else {
            Toast.makeText(this, "Presiona el icono de la izquierda para elegir una clienta", Toast.LENGTH_SHORT).show();
        }
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
        Intent i = new Intent(this, Inicio.class);
        i.putExtra("type","ventas");
        startActivity(i);
    }

}
