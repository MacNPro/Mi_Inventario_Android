package com.llamas.miinventario;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.llamas.miinventario.CustomClasses.MediumTextView;
import com.llamas.miinventario.Model.Producto;

import java.text.NumberFormat;
import java.util.Locale;

public class Calculadora extends Fragment {

    MediumTextView descuento, pad1, pad2, pad3, pad4, pad5, pad6, pad7, pad8, pad9, pad0, padPorcentaje, padBorrar, agregarDescuento;
    ImageView cerrar;

    String numString = "";
    int num;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calculadora, container, false);

        cerrar = (ImageView) view.findViewById(R.id.cerrar);
        descuento = (MediumTextView) view.findViewById(R.id.descuento);
        agregarDescuento = (MediumTextView) view.findViewById(R.id.agregarDescuento);
        pad0 = (MediumTextView) view.findViewById(R.id.pad0);
        pad1 = (MediumTextView) view.findViewById(R.id.pad1);
        pad2 = (MediumTextView) view.findViewById(R.id.pad2);
        pad3 = (MediumTextView) view.findViewById(R.id.pad3);
        pad4 = (MediumTextView) view.findViewById(R.id.pad4);
        pad5 = (MediumTextView) view.findViewById(R.id.pad5);
        pad6 = (MediumTextView) view.findViewById(R.id.pad6);
        pad7 = (MediumTextView) view.findViewById(R.id.pad7);
        pad8 = (MediumTextView) view.findViewById(R.id.pad8);
        pad9 = (MediumTextView) view.findViewById(R.id.pad9);
        padPorcentaje = (MediumTextView) view.findViewById(R.id.padPorcentaje);
        padBorrar = (MediumTextView) view.findViewById(R.id.padBorrar);

        descuento.setText("$0");

        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((FinalizarVenta)getActivity()).toggeVentana();
                numString = "";
                displayDescuento();
            }
        });

        pad0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numString.length() < 10){
                    numString = numString + "0";
                    displayDescuento();
                }
            }
        });

        pad1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numString.length() < 10){
                    numString = numString + "1";
                    displayDescuento();
                }
            }
        });

        pad2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numString.length() < 10){
                    numString = numString + "2";
                    displayDescuento();
                }
            }
        });

        pad3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numString.length() < 10){
                    numString = numString + "3";
                    displayDescuento();
                }
            }
        });

        pad4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numString.length() < 10){
                    numString = numString + "4";
                    displayDescuento();
                }
            }
        });

        pad5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numString.length() < 10){
                    numString = numString + "5";
                    displayDescuento();
                }
            }
        });

        pad6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numString.length() < 10){
                    numString = numString + "6";
                    displayDescuento();
                }
            }
        });

        pad7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numString.length() < 10){
                    numString = numString + "7";
                    displayDescuento();
                }
            }
        });

        pad8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numString.length() < 10){
                    numString = numString + "8";
                    displayDescuento();
                }
            }
        });

        pad9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numString.length() < 10){
                    numString = numString + "9";
                    displayDescuento();
                }
            }
        });

        padBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numString.length() < 10){
                    numString = "";
                    descuento.setText("$0");
                }
            }
        });

        padPorcentaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                num = Integer.valueOf(numString);
                float porcentaje = (num/100f);
                numString = String.valueOf(Math.round(FinalizarVenta.total*porcentaje));
                displayDescuento();
            }
        });

        agregarDescuento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Producto p = new Producto("Descuento", 1, Integer.valueOf(numString), 69, 0);
                FinalizarVenta.productos.add(p);
                ((FinalizarVenta)getActivity()).crearListView();
                ((FinalizarVenta)getActivity()).sumarTotal();
                ((FinalizarVenta)getActivity()).toggeVentana();
                numString = "";
                displayDescuento();
            }
        });

        return view;
    }

    private void displayDescuento() {
        if (numString.equals("")){
            num = 0;
        } else {
            num = Integer.valueOf(numString);
        }
        String totalFinal = NumberFormat.getNumberInstance(Locale.US).format(num);
        descuento.setText("$"+totalFinal);
    }

}
