package com.llamas.miinventario.Model;

/**
 * Created by MacNPro on 7/31/16.
 */
public class VentaListaObj {

    String id;
    int total;
    String fecha;

    public VentaListaObj() {

    }

    public VentaListaObj(String id, int total, String fecha) {
        this.id = id;
        this.total = total;
        this.fecha = fecha;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
