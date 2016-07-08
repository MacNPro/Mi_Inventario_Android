package com.llamas.miinventario.Model;

/**
 * Created by MacNPro on 7/8/16.
 */
public class Producto {

    String nombre;
    int precio;
    int puntos;
    int id;

    public Producto() {}

    public Producto(String nombre, int precio, int id, int puntos) {
        this.nombre = nombre;
        this.precio = precio;
        this.id = id;
        this.puntos = puntos;
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
