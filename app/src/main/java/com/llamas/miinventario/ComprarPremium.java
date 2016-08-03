package com.llamas.miinventario;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.llamas.miinventario.CustomClasses.MediumTextView;

import java.util.Date;

public class ComprarPremium extends Activity implements BillingProcessor.IBillingHandler{

    private DatabaseReference mDatabase;
    BillingProcessor bp;
    private String LICENCE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAknSrSV03llKOOB91pr4E4MM1Wha5RynIPTnl+wuef07VockDMDTOdv3Ga9+212lHEu/I4LJBiEq60u8l+XQe/baJUryKgZm3THv2kR2OnmZl62Zp91sEaR2YZTHTW56BatS9BLY17NUuOxokjS+6HEoz8p7NznOCKAP+wuzUkZujy1GILiH9Qp/Rgu1oxQI6UnhI/cvg2SpXAOVyLJyDK+Oi4dvqkEpWU2t7+X/mIRnXiwdjcTuuk6VzfvsYJv5iWYK/xj6UKqTqb+nIGqykH97fFZBE+1eOcOf3J9v5O0KPVy+WsDBnuJP6NDDb3qLJSt2+cel9+rckx1ZexeYG2wIDAQAB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprar_premium);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("usuarios").child(user.getUid());

        obtenerPremium();
        cargarImagen();

        bp = new BillingProcessor(this, LICENCE_KEY, this);

    }

    public void comprar(View v){
        bp.purchase(this, "com.llamaslabs.miinventario.premium");
    }

    public void obtenerPremium(){
        mDatabase.child("premium").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int premium = dataSnapshot.getValue(Integer.class);
                Log.d("PREMIUM", "" + premium);
                if (premium == 1) {
                    Intent i = new Intent(getApplicationContext(), Inicio.class);
                    startActivity(i);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void cargarImagen(){
        ImageView imageView = (ImageView) findViewById(R.id.imagen);
        Glide.with(this).load(R.drawable.fondo_venta).into(imageView);
    }

    @Override
    public void onBillingInitialized() {
        /*
         * Called when BillingProcessor was initialized and it's ready to purchase
         */
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        mDatabase.child("premium").setValue(1);
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        /*
         * Called when some error occurred. See Constants class for more details
         *
         * Note - this includes handling the case where the user canceled the buy dialog:
         * errorCode = Constants.BILLING_RESPONSE_RESULT_USER_CANCELED
         */
    }

    @Override
    public void onPurchaseHistoryRestored() {
        mDatabase.child("premium").setValue(1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        if (bp != null)
            bp.release();

        super.onDestroy();
    }

}
