package com.cie10;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cie10.Adapters.listarCapitulos;
import com.cie10.Datos.Preferencias;
import com.cie10.Otros.Globals;
import com.cie10.content.MenuCategoria;
import com.cie10.Datos.Capitulo;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class listadoCIECategorias extends Fragment {

    RecyclerView recyclerView;
   // private MenuCategoria.contentItem mItem;
    ArrayList<Capitulo> capitulos = new ArrayList<>();
    listarCapitulos mAdapter;
    View rootView;
    boolean mIsPremium = false;
    Integer mAnuncio = 0;

    public listadoCIECategorias() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_listadocie_categorias, container, false);

        Preferencias pPreferencia = new Preferencias(getContext()) ;
        mIsPremium = pPreferencia.obtenerPremium();


        mAnuncio = pPreferencia.obtenerAnuncio();


        if(!mIsPremium)
        {
            pPreferencia.guardarAnuncio(mAnuncio + 1);

            final Globals globalVariable = (Globals) getContext().getApplicationContext();
            final InterstitialAd mItestitialAd  = globalVariable.getInterstitial();

            if (mItestitialAd.isLoaded() && mAnuncio > 3) {
                pPreferencia.guardarAnuncio(0);
                mItestitialAd.show();
            } else {
                Log.d("TAG", mAnuncio + "");
            }
        }


        recyclerView = rootView.findViewById(R.id.recyclyCapitulos);
        recyclerView.setHasFixedSize(true);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new listarCapitulos();
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //progressBar.getDialog().dismiss();
                Intent ventana = new Intent(getContext(),listadoEnfermedades.class);
                ventana.putExtra("capitulo",capitulos.get(recyclerView.getChildAdapterPosition(view)).getCapitulo());
                ventana.putExtra("codigo",capitulos.get(recyclerView.getChildAdapterPosition(view)).getCodigos());
                ventana.putExtra("titulo",capitulos.get(recyclerView.getChildAdapterPosition(view)).getTitulo());

                startActivity(ventana);
            }
        });

        for(int i = 0; i< MenuCategoria.ITEMS.size(); i++)
        {
            Capitulo capitulo = new Capitulo();
            capitulo.setCapitulo(MenuCategoria.ITEMS.get(i).capitulo);
            capitulo.setCodigos(MenuCategoria.ITEMS.get(i).codigos);
            capitulo.setTitulo(MenuCategoria.ITEMS.get(i).titulo);
            capitulos.add(capitulo);
        }

        mAdapter.setDataset(capitulos);

        return rootView;
    }

}
