package com.cie10.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cie10.Datos.Capitulo;
import com.cie10.R;

import java.util.ArrayList;

public class listarCapitulos extends RecyclerView.Adapter<listarCapitulos.ViewHolder> implements  View.OnClickListener  {

    private ArrayList<Capitulo> mDataSet;
    private View.OnClickListener listener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v  = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_listado_capitulos,null,false);

        v.setOnClickListener(this);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Capitulo capitulo = mDataSet.get(position);
        holder.txtCapitulo_capitulo.setText(capitulo.getCapitulo().concat(" "));
        holder.txtCapitulo_codigo.setText(capitulo.getCodigos());
        holder.txtCapitulo_title.setText(capitulo.getTitulo());
    }

    public listarCapitulos(){
        mDataSet = new ArrayList<>();
    }

    public void setDataset(ArrayList<Capitulo> dataset){
        mDataSet = dataset;
        notifyDataSetChanged();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener=listener;
    }

    @Override
    public void onClick(View view) {
        if(listener!=null){
            listener.onClick(view);
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  {
        public TextView txtCapitulo_capitulo, txtCapitulo_codigo, txtCapitulo_title;

        private ViewHolder(View v){
            super(v);
            txtCapitulo_capitulo = v.findViewById(R.id.txtCapitulo_capitulo);
            txtCapitulo_codigo = v.findViewById(R.id.txtCapitulo_codigo);
            txtCapitulo_title =  v.findViewById(R.id.txtCapitulo_title);
        }

    }
}
