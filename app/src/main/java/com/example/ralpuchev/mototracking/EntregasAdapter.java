package com.example.ralpuchev.mototracking;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ralpuchev.mototracking.modelos.Entrega;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by ralpuchev on 21/07/16.
 */
public class EntregasAdapter extends RecyclerView.Adapter<EntregasAdapter.ViewHolder> {
    private ArrayList<Entrega> mDataset;
    public Context context;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View card;

        public ViewHolder(View v) {
        super(v);
            card = v;
        }
}

    // Provide a suitable constructor (depends on the kind of dataset)
    public EntregasAdapter(ArrayList<Entrega> dataset, Context context) {
        mDataset = dataset;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public EntregasAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.entrega_view, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        /*v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanzaIntentEntrega(v);
            }
        });*/
        return vh;
    }

    private void lanzaIntentEntrega(int position) {

        Intent resultIntent = new Intent(context, EntregaPendienteActivity.class);
        resultIntent.putExtra(Inicio.IS_NEW_ENTREGA,mDataset.get(position).getIdEntrega());
        context.startActivity(resultIntent);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        TextView texto = (TextView) holder.card.findViewById(R.id.txtDescripcion);
        texto.setText(mDataset.get(position).getDescripcion());
        TextView fecha = (TextView) holder.card.findViewById(R.id.txtFechaRecibi);


        String strFecha = new SimpleDateFormat("dd/MM/yyyy h:mm a").format(mDataset.get(position).getRecibido());
        fecha.setText(strFecha);

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanzaIntentEntrega(position);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}


