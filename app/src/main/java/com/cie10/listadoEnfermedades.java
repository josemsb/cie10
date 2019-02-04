package com.cie10;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.cie10.Adapters.listarEnfermedades;
import com.cie10.Datos.Enfermedad;
import com.cie10.db.DBAdapter;

import java.util.ArrayList;

public class listadoEnfermedades extends AppCompatActivity {
    String strCapitulo, strCodigo,strTitulo;
    RecyclerView recyclerView;
    listarEnfermedades mAdapter;
    DBAdapter dbAdapter;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_enfermedades);


        Bundle extras = getIntent().getExtras();
        if(extras != null) {

            strCapitulo = extras.getString("capitulo");
            strCodigo = extras.getString("codigo");
            strTitulo = extras.getString("titulo");

            getSupportActionBar().setSubtitle("Capítulo " + strCapitulo + ": " + strTitulo);

            recyclerView =  findViewById(R.id.recyclyEnfermedades);
            recyclerView.setHasFixedSize(true);

            final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);

            mAdapter = new listarEnfermedades(this);

            recyclerView.setAdapter(mAdapter);

            Intent intent = new Intent(getApplication(), DBAdapter.class);
            getApplication().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

         }
        }

    private void loadEnfermendades() {

        ArrayList<Enfermedad> enfermedades = dbAdapter.getEnfermedadesCapitulo(strCapitulo);
        mAdapter.setDataset(enfermedades);
        getApplication().unbindService(mConnection);

    }



    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            DBAdapter.LocalBinder binder = (DBAdapter.LocalBinder) service;
            dbAdapter = binder.getService();
            loadEnfermendades();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

        }
    };
}
