package com.llamas.miinventario;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.llamas.miinventario.Model.ExpandableListAdapter;
import com.llamas.miinventario.Model.Producto;

public class Inventario extends Fragment {

    private String title;
    private int page;
    boolean seleccionado = false;

    ExpandableListAdapter expandableListAdapter;
    ExpandableListView expandableListView;

    public static Inventario newInstance(int page, String title) {
        Inventario inventario = new Inventario();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        inventario.setArguments(args);
        return inventario;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventario, container, false);

        expandableListAdapter = new ExpandableListAdapter(getActivity(), Inicio.categorias);
        expandableListView = (ExpandableListView) view.findViewById(R.id.expandableListView);
        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,int groupPosition, int childPosition, long id) {

                Producto producto = Inicio.categorias.get(groupPosition).getProductos().get(childPosition);

                String pID = String.valueOf(producto.getId());
                Log.d("PRUEBA", pID);
                String nombre = String.valueOf(producto.getNombre());
                String precio = String.valueOf(producto.getPrecio());
                String puntos = String.valueOf(producto.getPuntos());

                Intent i = new Intent(getActivity(), DetailProducto.class);
                i.putExtra("ID", pID);
                i.putExtra("Nombre", nombre);
                i.putExtra("Precio", precio);
                i.putExtra("Puntos", puntos);
                startActivity(i);

                return true;
            }
        });

        return view;
    }

}
