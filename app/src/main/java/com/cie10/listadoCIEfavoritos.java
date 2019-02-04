package com.cie10;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cie10.Adapters.listarEnfermedades;
import com.cie10.Datos.Enfermedad;
import com.cie10.Datos.Preferencias;
import com.cie10.Otros.Globals;
import com.cie10.db.DBAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import com.google.android.gms.ads.InterstitialAd;


public class listadoCIEfavoritos extends Fragment {
    View rootView;
    RecyclerView recyclerView;
    listarEnfermedades mAdapter;
    DBAdapter dbAdapter;
    ArrayList<String> list;
    boolean mIsPremium = false;
    Integer mAnuncio = 0;
    Boolean mBound=false;


    public listadoCIEfavoritos() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_listadocie_favoritos, container, false);
        Log.d("listadoCIEfavoritos","onCreateView");


        list = getStringArrayPref(getActivity(), "Codigo_Guardados");

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


        recyclerView =  rootView.findViewById(R.id.recycleCIEFavoritos);
        recyclerView.setHasFixedSize(true);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new listarEnfermedades(getContext());
        recyclerView.setAdapter(mAdapter);



        return rootView;

    }

    private void loadEnfermedades() {

        try {
            mAdapter.limpiar();
            ArrayList<Enfermedad> enfermedades = dbAdapter.getEnfermedadesPreferentes(list);
            mAdapter.setDataset(enfermedades);
            if (mBound) {
                getActivity().unbindService(mConnection);
                mBound = false; }
        }
        catch(Exception e) {
            Log.d("TAG", e.getMessage());

        }

    }

    public static ArrayList<String> getStringArrayPref(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key, null);
        ArrayList<String> urls = new ArrayList<>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    String url = a.optString(i);
                    urls.add(url);
                }
                Collections.sort(urls);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }

    @Override
    public void onDestroy() {

        if (mBound) {
            getActivity().unbindService(mConnection);
            mBound = false; }
        super.onDestroy();
    }

    @Override
    public void onStart() {
        Log.d("mConnection","onStart");
        if(list.size()>0)
        {
            if (mConnection!=null) {
                Intent intent = new Intent(getActivity(), DBAdapter.class);
                getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            }

        }else{Toast.makeText(getActivity(), " Aun no adiciona enfermedades",Toast.LENGTH_LONG).show();}

        super.onStart();
    }


    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            DBAdapter.LocalBinder binder = (DBAdapter.LocalBinder) service;
            dbAdapter = binder.getService();
            mBound = true;
            loadEnfermedades();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

}
