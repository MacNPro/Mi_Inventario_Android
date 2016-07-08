package com.llamas.miinventario.Model;

import java.util.ArrayList;

/**
 * Created by MacNPro on 7/8/16.
 */
public class Categoria {

    public String nombre;
    public ArrayList<Producto> productos;

    public Categoria() {}

    public Categoria(String nombre, ArrayList<Producto> productos) {
        this.nombre = nombre;
        this.productos = productos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public ArrayList<Producto> getProductos() {
        return productos;
    }

    public void setProductos(ArrayList<Producto> productos) {
        this.productos = productos;
    }
}
