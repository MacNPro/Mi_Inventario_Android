package com.llamas.miinventario.Model;

import java.io.Serializable;

/**
 * Created by MacNPro on 7/8/16.
 */
public class Producto implements Serializable {

    String nombre;
    int cantidad;
    int precio;
    int puntos;
    int id;

    public Producto() {}

    public Producto(String nombre, int cantidad, int precio, int id, int puntos) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio = precio;
        this.id = id;
        this.puntos = puntos;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }
}
