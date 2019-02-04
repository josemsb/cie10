package com.cie10;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.cie10.Datos.Preferencias;
import com.cie10.Otros.CustomProgressBar;
import com.cie10.Otros.Globals;
import com.cie10.db.AccesoWiki;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class detalle_wikipedia extends AppCompatActivity {
    String  strWiki;
    WebView webView;
    private static CustomProgressBar progressBar;
    boolean mIsPremium = false;
    Integer mAnuncio = 0;

    Preferencias pPreferencia = new Preferencias(this) ;

    @Override
    protected void onPause() {
        super.onPause();
        progressBar.getDialog().dismiss();
    }

    @Override
    protected void onStart() {
        super.onStart();
       }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_wikipedia);

        mIsPremium = pPreferencia.obtenerPremium();


        mAnuncio = pPreferencia.obtenerAnuncio();


        if(mIsPremium)
        {

        }else {

            pPreferencia.guardarAnuncio(mAnuncio + 1);

            final Globals globalVariable = (Globals) getApplicationContext();
            final InterstitialAd mItestitialAd  = globalVariable.getInterstitial();

            if (mItestitialAd.isLoaded() && mAnuncio > 3) {
                pPreferencia.guardarAnuncio(0);
                mItestitialAd.show();
            } else {
                Log.d("TAG", mAnuncio + "");
            }
        }

        Bundle extras = getIntent().getExtras();
        if(extras != null) {

            strWiki = extras.getString("wiki");

            getSupportActionBar().setSubtitle(strWiki);

            progressBar = new CustomProgressBar();
            progressBar.show(this,"Cargando...");


            webView = findViewById(R.id.webView1);
            WebSettings websettings = webView.getSettings();
            websettings.setJavaScriptEnabled(false);

            progressBar.mostrar();

            new cargarWiki().execute("https://es.wikipedia.org/w/api.php?action=query&format=json&formatversion=2&utf8=1&prop=extracts&titles=" + strWiki.replace(" ","%20"));


        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_wiki,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {


        switch (item.getItemId()) {


            case R.id.aviso:
                alert("Esta app contiene información recogida de Wikipedia sobre cuestiones médicas. Sin embargo, no es posible garantizar la veracidad del contenido en los artículos, ya que estos pueden presentar errores, falsedades o desactualizaciones. Y, aunque la información pueda ser correcta o fiable y su contenido estar bien documentado, es posible que lo que se describa no corresponda con una situación de salud específica.");
                break;

        }

        return super.onOptionsItemSelected(item);
    }




    public class cargarWiki extends AsyncTask<String, Void, String> {
        AccesoWiki a = new AccesoWiki();

        @Override
        protected String doInBackground(String... urls){
            try{
                return a.downloadUrl(urls[0]);

            }catch (IOException e){
                return "Unable to retrieve web page. URL may be invalid";
            }
        }

        @Override
        protected  void onPostExecute(String result){
            try{

                JSONObject obj = new JSONObject(result);
                JSONObject objMobileView =  obj.getJSONObject("query");
                JSONArray ja = objMobileView.getJSONArray("pages");
                JSONObject jo = ja.getJSONObject(0);
                String unencodeHTML = "<html><body>" + jo.getString("extract") + "</body></html>";
                String encodeHTML = Base64.encodeToString(unencodeHTML.getBytes(),Base64.NO_PADDING);

                webView.loadData(encodeHTML,"text/html; charset=utf-8","base64");

            }catch (JSONException e){
                e.printStackTrace();
            }
            progressBar.getDialog().hide();


        }


    }


    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        bld.create().show();
    }

    @Override
    public void onDestroy() {

        //if(mAdView !=null)
        //{mAdView.destroy();}
        super.onDestroy();
    }


}
