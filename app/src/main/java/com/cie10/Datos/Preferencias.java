package com.cie10.Datos;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferencias {
    private Context mContext;


    public Preferencias(Context context){
        this.mContext = context;
    }

    public Integer obtenerAnuncio(){
        SharedPreferences spreferencias = mContext.getSharedPreferences("billing", Context.MODE_PRIVATE);
        return spreferencias.getInt("mAnuncio",0);
    }

    public void guardarAnuncio(Integer vAnuncio){
        SharedPreferences spreferencias = mContext.getSharedPreferences("billing", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = spreferencias.edit();
        editor.putInt("mAnuncio",vAnuncio);
        editor.apply();
    }


    public Boolean obtenerPremium(){
        SharedPreferences spreferencias = mContext.getSharedPreferences("billing", Context.MODE_PRIVATE);
        return spreferencias.getBoolean("mIsPremium",false);
    }


    public void guardarPreferencias(Boolean vPremiun){
       SharedPreferences spreferencias = mContext.getSharedPreferences("billing", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = spreferencias.edit();
        editor.putBoolean("mIsPremium",vPremiun);
        editor.apply();
    }


}
