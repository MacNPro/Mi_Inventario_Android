package com.llamas.miinventario.Model;

import java.io.Serializable;

/**
 * Created by MacNPro on 7/31/16.
 */
public class Clienta implements Serializable {

    String nombre;
    String cumpleaños;
    int numDeVentas;
    int id;

    public Clienta() {}

    public Clienta(String nombre, String cumpleaños, int numDeVentas, int id) {
        this.nombre = nombre;
        this.cumpleaños = cumpleaños;
        this.numDeVentas = numDeVentas;
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCumpleaños() {
        return cumpleaños;
    }

    public void setCumpleaños(String cumpleaños) {
        this.cumpleaños = cumpleaños;
    }

    public int getNumDeVentas() {
        return numDeVentas;
    }

    public void setNumDeVentas(int numDeVentas) {
        this.numDeVentas = numDeVentas;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
