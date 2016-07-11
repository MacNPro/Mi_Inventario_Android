package com.llamas.miinventario.Model;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.llamas.miinventario.R;

import java.util.ArrayList;

/**
 * Created by MacNPro on 7/7/16.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {
    LayoutInflater inflater;

    private ArrayList<Categoria> categorias;

    public ExpandableListAdapter(Context context, ArrayList<Categoria> categorias) {
        super();
        this.categorias = categorias;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public Producto getChild(int groupPosition, int childPosition) {
        ArrayList<Producto> producto = categorias.get(groupPosition).getProductos();
        return producto.get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList<Producto> producto = categorias.get(groupPosition).getProductos();
        return producto.size();
    }

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Producto producto = getChild(groupPosition, childPosition);
        TextView productoNombre, productoPrecio, productoCantidad;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item, null);
        }
        productoNombre = (TextView) convertView.findViewById(R.id.nombre);
        productoPrecio = (TextView) convertView.findViewById(R.id.precio);
        productoCantidad = (TextView) convertView.findViewById(R.id.enInventario);
        int cantidad = producto.getCantidad();
        if (cantidad == 0) {
            productoCantidad.setBackgroundResource(R.drawable.indicador_agotados);
        } else if (cantidad <= 2 && cantidad > 0) {
            productoCantidad.setBackgroundResource(R.drawable.indicador_por_agotarse);
        } else {
            productoCantidad.setBackgroundResource(R.drawable.bola_rosa);
        }
        productoNombre.setText(producto.getNombre());
        productoPrecio.setText("$" + producto.getPrecio() + ".00");
        productoCantidad.setText("" + producto.getCantidad());
        return convertView;
    }

    public Categoria getGroup(int groupPosition) {
        return categorias.get(groupPosition);
    }

    public int getGroupCount() {
        return categorias.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        TextView groupName;
        ImageView groupImage;
        Categoria categoria = getGroup(groupPosition);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_grupo, null);
        }
        groupName = (TextView) convertView.findViewById(R.id.textViewGroupName);
        groupImage = (ImageView) convertView.findViewById(R.id.icono);
        groupImage.setImageResource(R.drawable.labial);
        groupName.setText(categoria.getNombre());

        if (isExpanded) {
            convertView.setBackgroundColor(Color.rgb(238, 238, 238));
        } else {
            convertView.setBackgroundColor(Color.rgb(245, 245, 245));
        }

        return convertView;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public boolean hasStableIds() {
        return true;
    }
}
