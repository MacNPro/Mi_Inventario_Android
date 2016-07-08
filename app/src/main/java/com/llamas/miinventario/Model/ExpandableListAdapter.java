package com.llamas.miinventario.Model;

import android.content.Context;
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
    public ExpandableListAdapter(Context context,ArrayList<Categoria> categorias) {
        super();
        this.categorias = categorias;
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    /*
     * @param child
     * @param group
     *  use for adding item to list view
     *
    public void addChild(Item child, Group group) {
        if(!groups.contains(group)) {
            groups.add(group);
        }
        int index = groups.indexOf(group);
        ArrayList<Item> ch = groups.get(index).getChildrens();
        ch.add(child);
        groups.get(index).setChildrens(ch);
    }*/

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
        Producto producto = getChild(groupPosition,childPosition);
        TextView productoNombre, productoPrecio;
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.list_item, null);
        }
        productoNombre = (TextView) convertView.findViewById(R.id.nombre);
        productoPrecio = (TextView) convertView.findViewById(R.id.precio);
        productoNombre.setText(producto.getNombre());
        productoPrecio.setText("$" + producto.getPrecio() + ".00");
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

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {
        TextView groupName;
        ImageView groupImage;
        Categoria categoria = getGroup(groupPosition);
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.list_grupo, null);
        }
        groupName = (TextView) convertView.findViewById(R.id.textViewGroupName);
        groupImage = (ImageView) convertView.findViewById(R.id.icono);
        groupImage.setImageResource(R.drawable.labial);
        groupName.setText(categoria.getNombre());
        return convertView;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public boolean hasStableIds() {
        return true;
    }
}
