package com.cie10;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.cie10.Adapters.listarEnfermedades;
import com.cie10.Datos.Enfermedad;
import com.cie10.Datos.Preferencias;
import com.cie10.db.DBAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

public class listadoCIEbusqueda extends Fragment {
    View rootView;
    EditText editText;
    RecyclerView recyclerView;
    listarEnfermedades mAdapter;
    DBAdapter dbAdapter;
    String strBusqueda;
    private AdView mAdView;
    boolean mIsPremium = false;

    public listadoCIEbusqueda() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        rootView = inflater.inflate(R.layout.fragment_listadocie_busqueda, container, false);


        Preferencias pPreferencia = new Preferencias(getContext()) ;
        //pPreferencia.guardarAnuncio(0);
        mIsPremium = pPreferencia.obtenerPremium();

        mAdView = rootView.findViewById(R.id.adViewB);

        if(mIsPremium)
        {
            mAdView.setVisibility(View.INVISIBLE);
            mAdView.getLayoutParams().height=1;
        }else {
            mAdView.setVisibility(View.VISIBLE);
            MobileAds.initialize(getContext(), "ca-app-pub-3918734194731544~4006952735");
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);



        }


        recyclerView =  rootView.findViewById(R.id.recyclyBusqueda);
        recyclerView.setHasFixedSize(true);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new listarEnfermedades(getContext());
        recyclerView.setAdapter(mAdapter);

        editText = rootView.findViewById(R.id.txtCIEBusqueda);

        editText.requestFocus();

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(getView(), 0);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT);

        editText.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)
                {
                    	buscarElemtos(editText.getText().toString().trim());
                    return true;
                }
                return false;
            }

        });

        return rootView;
    }


    public void buscarElemtos(String s)
    {
        if(s.length()>2 )
        {
            strBusqueda=s;
            Intent intent = new Intent(getActivity(), DBAdapter.class);
            getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        }
        else
        {
            Toast.makeText(getActivity(),"Ingrese mínimo 3 caracteres para búsqueda",Toast.LENGTH_LONG).show();
            mAdapter.limpiar();

        }


    }

    private void loadEnfermedades() {
        mAdapter.limpiar();
        ArrayList<Enfermedad> enfermedades = dbAdapter.getEnfermedadesBusqueda(remove1(strBusqueda));
        if(enfermedades.size()>0) {
            mAdapter.bolBusqueda = true;
            mAdapter.strBusqueda=remove1(strBusqueda);
            mAdapter.setDataset(enfermedades);
        }
        else
            Toast.makeText(getActivity(),"No se han encontrado resultados",Toast.LENGTH_LONG).show();

        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null){
            imm.hideSoftInputFromWindow( getActivity().getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        }

        getActivity().unbindService(mConnection);


    }

    public static String remove1(String input) {
        String original = "��������������u�������������������";
        String ascii = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC";
        String output = input;
        for (int i=0; i<original.length(); i++) {
            output = output.replace(original.charAt(i), ascii.charAt(i));
        }
        return output;
    }




    /* Crea la variable conexción par ala base de datos */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            DBAdapter.LocalBinder binder = (DBAdapter.LocalBinder) service;
            dbAdapter = binder.getService();
            loadEnfermedades();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

        }
    };

    @Override
    public void onDestroy() {

        if(mAdView !=null)
        {mAdView.destroy();}
        super.onDestroy();
    }

}
