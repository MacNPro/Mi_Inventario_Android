package com.llamas.miinventario;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.llamas.miinventario.CustomClasses.MediumTextView;

import java.util.Arrays;
import java.util.List;

public class DetailAjustes extends Activity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    CallbackManager mCallbackManager;
    LoginManager loginManager;

    List<String> providers;
    RelativeLayout cambiarNombre, cambiarCorreo, cambiarNivel, borrarCuenta;
    MediumTextView titulo, nombreActual, correoActual, nivelActual, guardarCambios, btnBorrarCuenta;
    EditText nombreNuevo, correoNuevo, nivelNuevo;

    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_ajustes);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        cambiarNombre = (RelativeLayout) findViewById(R.id.cambiarNombre);
        cambiarCorreo = (RelativeLayout) findViewById(R.id.cambiarCorreo);
        cambiarNivel = (RelativeLayout) findViewById(R.id.cambiarNivel);
        borrarCuenta = (RelativeLayout) findViewById(R.id.borrarCuenta);
        titulo = (MediumTextView) findViewById(R.id.titulo);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        providers = user.getProviders();

        obtenerType();

    }

    public void obtenerType(){
        Intent i = getIntent();
        type = i.getStringExtra("Type");

        switch (type) {
            case "Nombre":
                cambiarNombre.setVisibility(View.VISIBLE);
                cambiarNivel.setVisibility(View.GONE);
                cambiarCorreo.setVisibility(View.GONE);
                borrarCuenta.setVisibility(View.GONE);
                nombreNuevo = (EditText) findViewById(R.id.nombreNuevo);
                nombreActual = (MediumTextView) findViewById(R.id.nombreActual);
                nombreActual.setText(i.getStringExtra("Nombre"));
                titulo.setText("Cambiar Nombre");
                break;
            case "Correo":
                cambiarNombre.setVisibility(View.GONE);
                cambiarNivel.setVisibility(View.GONE);
                borrarCuenta.setVisibility(View.GONE);
                cambiarCorreo.setVisibility(View.VISIBLE);
                correoNuevo = (EditText) findViewById(R.id.correoNuevo);
                correoActual = (MediumTextView) findViewById(R.id.correoActual);
                correoActual.setText(i.getStringExtra("Correo"));
                titulo.setText("Cambiar Correo");
                break;
            case "Nivel":
                cambiarNombre.setVisibility(View.GONE);
                cambiarCorreo.setVisibility(View.GONE);
                borrarCuenta.setVisibility(View.GONE);
                cambiarNivel.setVisibility(View.VISIBLE);
                nivelNuevo = (EditText) findViewById(R.id.nivelNuevo);
                nivelActual = (MediumTextView) findViewById(R.id.nivelActual);
                nivelActual.setText(i.getStringExtra("Nivel"));
                titulo.setText("Cambiar Nivel");
                break;
            case "Borrar":
                guardarCambios = (MediumTextView) findViewById(R.id.guardarCambios);
                if (providers.get(0).equals("facebook.com")) {
                    btnBorrarCuenta = (MediumTextView) findViewById(R.id.btnBorrarCuenta);
                    btnBorrarCuenta.setAlpha(.6f);
                    btnBorrarCuenta.setClickable(false);
                    guardarCambios.setVisibility(View.GONE);
                    cambiarNombre.setVisibility(View.GONE);
                    cambiarCorreo.setVisibility(View.GONE);
                    cambiarNivel.setVisibility(View.GONE);
                    borrarCuenta.setVisibility(View.VISIBLE);
                } else {
                    guardarCambios.setVisibility(View.GONE);
                    borrarCuenta.setVisibility(View.GONE);
                }
                titulo.setText("Borrar Cuenta");
                break;
            default:
                break;
        }

    }

    public void onGuardarCambios(View v){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        switch (type) {
            case "Nombre":
                if (!nombreNuevo.getText().toString().isEmpty()){
                    UserProfileChangeRequest nombreUpdate = new UserProfileChangeRequest.Builder()
                            .setDisplayName(nombreNuevo.getText().toString())
                            .build();
                    user.updateProfile(nombreUpdate)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        cerrarActivity();
                                    } else {
                                        Log.d("NOMBRE Update", task.getException().toString());
                                        Toast.makeText(getApplicationContext(), "Hubo un error, intentelo más tarde", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(getApplicationContext(), "Ingresa los datos requeridos", Toast.LENGTH_SHORT).show();
                }
                break;
            case "Correo":
                if (!correoNuevo.getText().toString().isEmpty()){
                    user.updateEmail(correoNuevo.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        cerrarActivity();
                                    } else {
                                        Log.d("CORREO Update", task.getException().toString());
                                        Toast.makeText(getApplicationContext(), "Hubo un error, intentelo más tarde", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(getApplicationContext(), "Ingresa los datos requeridos", Toast.LENGTH_SHORT).show();
                }
                break;
            case "Nivel":
                mDatabase.child("usuarios").child(user.getUid()).child("nivel").setValue(nivelNuevo.getText().toString());
                cerrarActivity();
                break;
            default:
                break;
        }
    }

    public void onRegresar(View v){
        cerrarActivity();
    }

    private void cerrarActivity() {
        Intent i = new Intent(getApplicationContext(), Ajustes.class);
        startActivity(i);
        finish();
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
        cerrarActivity();
    }

    public void onVerificarCuenta(View v){
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

    public void onBorrarCuenta(View v){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userID = user.getUid();
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "User account deleted.");
                            mDatabase.child("usuarios").child(userID).setValue(null);
                            Intent i = new Intent(getApplicationContext(), Main.class);
                            startActivity(i);
                            finish();
                        }
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("TAG", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("TAG", "signInWithCredential:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w("TAG", "signInWithCredential", task.getException());
                            Toast.makeText(DetailAjustes.this, "Error al verificar la cuenta, intentelo más tarde.",Toast.LENGTH_SHORT).show();
                        } else {
                            btnBorrarCuenta.setAlpha(1f);
                            btnBorrarCuenta.setClickable(true);
                        }

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
