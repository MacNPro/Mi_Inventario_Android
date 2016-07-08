package com.llamas.miinventario;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class DetailProducto extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_producto);

        Intent i = getIntent();
        String id = i.getStringExtra("ID");
        String nombre = i.getStringExtra("Nombre");
        String precio = i.getStringExtra("Precio");
        String puntos = i.getStringExtra("Puntos");

        TextView texto = (TextView) findViewById(R.id.texto);
        texto.setText(nombre + "\n" + id + "\n" + precio + "\n" + puntos);

    }
}
