package com.llamas.miinventario;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.llamas.miinventario.CustomClasses.MediumTextView;

public class Ajustes extends Activity {

    private DatabaseReference mDatabase;

    MediumTextView nombrett, correott, niveltt;
    String nombre, correo, nivel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        nombrett = (MediumTextView) findViewById(R.id.nombre);
        correott = (MediumTextView) findViewById(R.id.correo);
        niveltt = (MediumTextView) findViewById(R.id.nivel);

        obtenerUsuario();
        obtenerInformacion();
    }

    private void obtenerInformacion() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.child("usuarios").child(user.getUid()).child("nivel").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nivel = dataSnapshot.getValue(String.class);
                niveltt.setText(nivel);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onNombre(View v){
        Intent i = new Intent(this, DetailAjustes.class);
        i.putExtra("Nombre", nombre);
        i.putExtra("Type", "Nombre");
        startActivity(i);
        finish();
    }

    public void onCorreo(View v){
        Intent i = new Intent(this, DetailAjustes.class);
        i.putExtra("Correo", correo);
        i.putExtra("Type", "Correo");
        startActivity(i);
        finish();
    }

    public void onNivelDeCarrera(View v){
        Intent i = new Intent(this, DetailAjustes.class);
        i.putExtra("Nivel", nivel);
        i.putExtra("Type", "Nivel");
        startActivity(i);
        finish();
    }

    public void onBorrarCuenta(View v){
        Intent i = new Intent(this, DetailAjustes.class);
        i.putExtra("Nombre", nombre);
        i.putExtra("Type", "Borrar");
        startActivity(i);
        finish();
    }

    public void obtenerUsuario(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        nombre = user.getDisplayName();
        correo = user.getEmail();
        nombrett.setText(nombre);
        correott.setText(correo);
    }

    public void onRegresar(View v){
        Intent i = new Intent(this, Inicio.class);
        startActivity(i);
        finish();
    }

    public void onCerrarSesion(View v){
        new AlertDialog.Builder(this)
                .setTitle("Cerrar Sesion")
                .setMessage("¿Estás segura de cerrar sesión?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        Intent i = new Intent(getApplicationContext(), Main.class);
                        startActivity(i);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
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
        Intent i = new Intent(this, Inicio.class);
        startActivity(i);
        finish();
    }

}
