package com.llamas.miinventario;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.llamas.miinventario.CustomClasses.MediumTextView;

import java.util.HashMap;
import java.util.Map;

public class AgregarClienta extends Fragment implements AdapterView.OnItemSelectedListener {

    private DatabaseReference mDatabase;
    String dia, mes;
    String type;
    int numeroDeClientas;

    public static AgregarClienta newInstance(String type) {
        Bundle args = new Bundle();
        args.putString("Type", type);
        AgregarClienta fragment = new AgregarClienta();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_agregar_clienta, container, false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("usuarios").child(user.getUid()).child("clientas");
        type = getArguments().getString("Type");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                numeroDeClientas = (int) dataSnapshot.getChildrenCount();
                Log.d("TAG", "" + numeroDeClientas);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        final EditText nombre = (EditText) v.findViewById(R.id.nombre);
        Spinner dias = (Spinner) v.findViewById(R.id.dias);
        Spinner meses = (Spinner) v.findViewById(R.id.meses);
        ImageView cerrar = (ImageView) v.findViewById(R.id.cerrar);
        MediumTextView agregarClienta = (MediumTextView) v.findViewById(R.id.agregarClienta);
        LinearLayout fondo = (LinearLayout) v.findViewById(R.id.fondo);

        setupSpinners(dias, meses);

        fondo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(getActivity());
            }
        });

        agregarClienta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String n = nombre.getText().toString();
                String cumple = dia + " de " + mes;
                if (!n.isEmpty()) {
                    Map<String, String> valoresClienta = new HashMap<>();
                    valoresClienta.put("nombre", n);
                    valoresClienta.put("cumpleaños", cumple);
                    mDatabase.child("" + numeroDeClientas).setValue(valoresClienta);
                    if (type.equals("Clientas")) {
                        ((Clientas) getActivity()).toggleVentana();
                    } else {
                        ((FinalizarVenta) getActivity()).toggleAgregarC();
                        ((FinalizarVenta) getActivity()).setNombre(n);
                        FinalizarVenta.clientaID = numeroDeClientas;
                    }
                    nombre.setText("");
                } else {
                    Toast.makeText(getActivity(), "No olvides llenar todos los datos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equals("Clientas")) {
                    ((Clientas) getActivity()).toggleVentana();
                } else {
                    ((FinalizarVenta) getActivity()).toggleAgregarC();
                }
            }
        });

        return v;
    }

    private void setupSpinners(Spinner dias, Spinner meses) {
        ArrayAdapter<CharSequence> diasAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.dias, R.layout.spinner_item);
        ArrayAdapter<CharSequence> mesesAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.meses, R.layout.spinner_item);
        diasAdapter.setDropDownViewResource(R.layout.spinner_list_item);
        mesesAdapter.setDropDownViewResource(R.layout.spinner_list_item);
        dias.setAdapter(diasAdapter);
        meses.setAdapter(mesesAdapter);
        dias.setOnItemSelectedListener(this);
        meses.setOnItemSelectedListener(this);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if (parent.getCount() == 12) {
            mes = parent.getItemAtPosition(pos).toString();
        } else {
            dia = parent.getItemAtPosition(pos).toString();
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

}
