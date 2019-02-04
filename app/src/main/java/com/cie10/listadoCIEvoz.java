package com.cie10;


import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.cie10.Adapters.listarEnfermedades;
import com.cie10.Datos.Enfermedad;
import com.cie10.Datos.Preferencias;
import com.cie10.Otros.Globals;
import com.cie10.db.DBAdapter;
import com.google.android.gms.ads.InterstitialAd;
import java.util.ArrayList;


public class listadoCIEvoz extends Fragment {
    View rootView;
    RecyclerView recyclerView;
    listarEnfermedades mAdapter;
    DBAdapter dbAdapter;
    String strBusqueda;
    EditText editText;
    Integer mAnuncio = 0;
    boolean mIsPremium = false;


    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1;

    public listadoCIEvoz() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_listadocie_voz, container, false);

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

        recyclerView = rootView.findViewById(R.id.recycleCIEVoz);
        recyclerView.setHasFixedSize(true);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new listarEnfermedades(getContext());
        recyclerView.setAdapter(mAdapter);


        editText =  rootView.findViewById(R.id.txtCIEBVoz);
        editText.setEnabled(false);
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
            mAdapter.strBusqueda = remove1(strBusqueda);
            mAdapter.setDataset(enfermedades);
        }
            else
            Toast.makeText(getActivity(),"No se han encontrado resultados",Toast.LENGTH_LONG).show();


        getActivity().unbindService(mConnection);



    }

    private void startVoiceRecognitionActivity() {
        try
        {
        String language =  "es-ES";
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,language);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language);
        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, language);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Hable ahora ...");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
        }catch (ActivityNotFoundException e)
        {
            alert("Microfono no soportado");
        }
    }
    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(getContext());
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        bld.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Log.d("resultCode",String.valueOf(resultCode));
        if(requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode==-1){

            //El intent nos envia un ArrayList aunque en este caso solo
            //utilizaremos la pos.0
            ArrayList<String> matches = data.getStringArrayListExtra
                    (RecognizerIntent.EXTRA_RESULTS);

                editText.setText(matches.get(0).trim());
                buscarElemtos(matches.get(0).trim());

        }
    }

    @Override
    public void onStart() {
        startVoiceRecognitionActivity();
        super.onStart();
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
}
