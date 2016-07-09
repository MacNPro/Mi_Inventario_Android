package com.llamas.miinventario.Model;

/**
 * Created by MacNPro on 7/9/16.
 */
public class ProductoEnInventario {

    String id;
    int cantidad;

    public ProductoEnInventario() {}

    public ProductoEnInventario(String id, int cantidad) {
        this.id = id;
        this.cantidad = cantidad;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
