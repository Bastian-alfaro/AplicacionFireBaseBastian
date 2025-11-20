package com.example.myapplication;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MensajeAdapter extends ArrayAdapter<Mensaje> {

    private Activity context;
    private ArrayList<Mensaje> lista;

    public MensajeAdapter(Activity context, ArrayList<Mensaje> lista) {
        super(context, R.layout.item_mensaje, lista);
        this.context = context;
        this.lista = lista;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.item_mensaje, null, true);

        TextView txtTexto = rowView.findViewById(R.id.txtMensajeItem);
        TextView txtFecha = rowView.findViewById(R.id.txtFechaItem);

        Mensaje m = lista.get(position);

        txtTexto.setText(m.texto);

        String fecha = new SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                .format(new Date(m.timestamp));

        txtFecha.setText(fecha);

        return rowView;
    }
}
