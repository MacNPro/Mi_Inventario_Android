package com.llamas.miinventario;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class IniciaSesion extends Activity {

    private FirebaseAuth.AuthStateListener mAuthListener;
    private static String TAG = "INICIA SESION";
    private FirebaseAuth mAuth;

    EditText correo, contraseña;

    CallbackManager mCallbackManager;
    LoginManager loginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicia_sesion);
        mAuth = FirebaseAuth.getInstance();

        correo = (EditText) findViewById(R.id.correo);
        contraseña = (EditText) findViewById(R.id.contraseña);

        ImageView fondo = (ImageView) findViewById(R.id.fondo);
        Glide.with(this).load(R.drawable.fondo_inicia_sesion).into(fondo);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    Intent i = new Intent(getApplicationContext(), Inicio.class);
                    startActivity(i);

                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

    }

    public void onIniciarConFirebase(View v){
        String correoTxt = correo.getText().toString();
        String contraseñaTxt = contraseña.getText().toString();
        if (!correoTxt.equals("") && !contraseñaTxt.equals("")){
            mAuth.signInWithEmailAndPassword(correoTxt, contraseñaTxt)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInWithEmail", task.getException());
                                Toast.makeText(IniciaSesion.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(IniciaSesion.this, "Ingresa tus datos",Toast.LENGTH_SHORT).show();
        }
    }

    public void onIniciarConFacebook(View v){
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        loginManager = LoginManager.getInstance();
        loginManager.logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
        loginManager.registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException exception) {

                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(IniciaSesion.this, "Error al Iniciar Sesión",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
