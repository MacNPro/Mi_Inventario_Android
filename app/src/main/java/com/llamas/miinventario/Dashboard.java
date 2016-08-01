package com.llamas.miinventario;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.llamas.miinventario.CustomClasses.BoldTextView;
import com.llamas.miinventario.CustomClasses.MediumTextView;
import com.llamas.miinventario.CustomClasses.RegularTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Dashboard extends Fragment {

    private DatabaseReference mDatabase;

    LineChart chart;
    MediumTextView dia1, dia2, dia3, dia4, dia5, dia6, dia7;

    ArrayList<String> dias = new ArrayList<>();
    MediumTextView bienvenida;
    BoldTextView porAgotarse, enInventario, agotados;
    int porAgotarseInt, enInventarioInt, agotadosInt;
    long ventasInt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        bienvenida = (MediumTextView) view.findViewById(R.id.nombre);
        porAgotarse = (BoldTextView) view.findViewById(R.id.porAgotarse);
        enInventario = (BoldTextView) view.findViewById(R.id.enInventario);
        agotados = (BoldTextView) view.findViewById(R.id.agotados);
        chart = (LineChart) view.findViewById(R.id.chart);
        RelativeLayout botonAgotados = (RelativeLayout) view.findViewById(R.id.botonAgotados);
        RelativeLayout clientas = (RelativeLayout) view.findViewById(R.id.clientas);

        dia1 = (MediumTextView) view.findViewById(R.id.dia1);
        dia2 = (MediumTextView) view.findViewById(R.id.dia2);
        dia3 = (MediumTextView) view.findViewById(R.id.dia3);
        dia4 = (MediumTextView) view.findViewById(R.id.dia4);
        dia5 = (MediumTextView) view.findViewById(R.id.dia5);
        dia6 = (MediumTextView) view.findViewById(R.id.dia6);
        dia7 = (MediumTextView) view.findViewById(R.id.dia7);

        chart.setDescription("");

        clientas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), Clientas.class);
                startActivity(i);
            }
        });

        botonAgotados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), PedidoPorLlegar.class);
                startActivity(i);
            }
        });

        cargarUsuario();
        cargarInformacion();
        obtenerVentas();

        return view;
    }

    public ArrayList<String> obtenerFechas(){

        ArrayList<String> fechas = new ArrayList<>();

        for (int i = 6; i >= 0; i--){
            Calendar cal = GregorianCalendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_YEAR, - i);
            Date daysBeforeDate = cal.getTime();
            String date = new SimpleDateFormat("dd-MM-yyyy").format(daysBeforeDate);
            String dia = new SimpleDateFormat("dd").format(daysBeforeDate);
            fechas.add(date);
            switch (dia){
                case "01":
                    dias.add("1");
                    break;
                case "02":
                    dias.add("2");
                    break;
                case "03":
                    dias.add("3");
                    break;
                case "04":
                    dias.add("4");
                    break;
                case "05":
                    dias.add("5");
                    break;
                case "06":
                    dias.add("6");
                    break;
                case "07":
                    dias.add("7");
                    break;
                case "08":
                    dias.add("8");
                    break;
                case "09":
                    dias.add("9");
                    break;
                default:
                    dias.add(dia);
                    break;
            }
        }
        return fechas;
    }

    public void obtenerVentas(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final ArrayList<String> fechas = obtenerFechas();
        final ArrayList<Integer> ventasPorDia = new ArrayList<>();
        mDatabase.child("usuarios").child(user.getUid()).child("ventas").limitToLast(50).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ventasPorDia.clear();
                for (String fecha : fechas) {
                    int ventas = 0;
                    for (DataSnapshot venta: dataSnapshot.getChildren()){
                        if (fecha.equals(venta.child("tiempo").getValue(String.class))) {
                            ventas++;
                        }
                    }
                    ventasPorDia.add(ventas);
                }
                llenarChart(ventasPorDia);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void llenarChart(ArrayList<Integer> ventasPorDias){

        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Avenir-Medium.ttf");
        chart.setTouchEnabled(false);

        ArrayList<Entry> semana = new ArrayList<>();
        MediumTextView[] ttDias = {dia1, dia2, dia3, dia4, dia5, dia6, dia7};

        int i = 0;
        for (Integer ventas : ventasPorDias) {
            ttDias[i].setText(dias.get(i));
            Entry d1 = new Entry(i, ventas);
            semana.add(d1);
            i++;
        }

        LineDataSet setComp1 = new LineDataSet(semana, "Ventas");
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        setComp1.setValueTypeface(font);
        setComp1.setColor(Color.rgb(250,106,130));
        setComp1.setCircleColor(Color.rgb(250,106,130));
        setComp1.setCircleRadius(4);
        setComp1.setCircleHoleRadius(3);
        setComp1.setValueTextSize(10f);
        setComp1.setValueTextColor(R.color.colorPrimary);
        setComp1.setValueFormatter(new LargeValueFormatter());

        // use the interface ILineDataSet
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(setComp1);

        LineData data = new LineData(dataSets);
        chart.setData(data);
        chart.invalidate(); // refresh

        XAxis xAxis = chart.getXAxis();
        xAxis.setTypeface(font);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(14f);
        xAxis.setTextColor(R.color.colorPrimary);
        xAxis.setDrawLabels(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setAvoidFirstLastClipping(true);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setTypeface(font);
        yAxis.setTextSize(12f);
        yAxis.setAxisMinValue(0f);
        yAxis.setDrawAxisLine(false);
        yAxis.setDrawLabels(false);
        yAxis.setGridColor(Color.rgb(225,225,225));
        yAxis.setLabelCount(4, true);

        chart.getAxisRight().setEnabled(false);

    }

    public void cargarUsuario() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.getDisplayName() != null) {
            String nombre = user.getDisplayName();
            String[] nombreSplit = nombre.split("\\s+");
            bienvenida.setText("Bienvenida " + nombreSplit[0]);
        } else {
            mDatabase.child("usuarios").child(user.getUid()).child("nombre").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String nombre = dataSnapshot.getValue(String.class);
                    String[] nombreSplit = nombre.split("\\s+");
                    bienvenida.setText("Bienvenida " + nombreSplit[0]);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void cargarInformacion() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.child("usuarios").child(user.getUid()).child("inventario").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                porAgotarseInt = 0;
                enInventarioInt = 0;
                for (DataSnapshot producto : dataSnapshot.getChildren()) {
                    int cantidad = producto.getValue(Integer.class);
                    if (cantidad <= 0) {
                        agotadosInt++;
                    } else if (cantidad > 0 && cantidad <= 2) {
                        porAgotarseInt++;
                    }
                    enInventarioInt = enInventarioInt + cantidad;
                }
                porAgotarse.setText("" + porAgotarseInt);
                enInventario.setText("" + enInventarioInt);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child("usuarios").child(user.getUid()).child("pedidoPorLlegar").child("tiempo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(String.class) != null) {
                    agotados.setText("1");
                } else {
                    agotados.setText("0");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child("usuarios").child(user.getUid()).child("ventas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ventasInt = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
