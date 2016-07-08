package com.llamas.miinventario;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class Main extends Activity {

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Intent i = new Intent(getApplicationContext(), Inicio.class);
            startActivity(i);
        }

        ImageView fondo = (ImageView) findViewById(R.id.fondo);
        Glide.with(this).load(R.drawable.fondo_inicia_sesion).into(fondo);

    }

    public void onIniciaSesion(View v){
        Intent i = new Intent(this, IniciaSesion.class);
        startActivity(i);
    }

    public void onRegistrate(View v){
        Intent i = new Intent(this, Registrate.class);
        startActivity(i);
    }

}
