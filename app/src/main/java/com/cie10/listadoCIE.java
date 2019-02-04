package com.cie10;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.cie10.Datos.Preferencias;
import com.cie10.Otros.Globals;
import com.cie10.Util.IabBroadcastReceiver;
import com.cie10.Util.IabHelper;
import com.cie10.Util.IabResult;
import com.cie10.Util.Inventory;
import com.cie10.Util.Purchase;


public class listadoCIE extends AppCompatActivity implements IabBroadcastReceiver.IabBroadcastListener {
    private Fragment mContent;
    static final String TAG = "CIE10";

    boolean mIsPremium = false;
    boolean mBilling = false;
    static final String SKU_CIE10_full= "cie10_full";
    static final int RC_REQUEST = 1983;

    IabHelper mHelper;
    IabBroadcastReceiver mBroadcastReceiver;

    Preferencias pPreferencia = new Preferencias(this) ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_cie);

        getSupportActionBar().setSubtitle("Capítulos");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String payload = "CIE10";
                try {
                    if (mBilling) {
                        mHelper.launchPurchaseFlow(listadoCIE.this  , SKU_CIE10_full, RC_REQUEST,
                                mPurchaseFinishedListener, payload);
                    }
                    else
                        complain("Tu dispositivo no tiene configurado Google Billing");
                } catch (IabHelper.IabAsyncInProgressException e) {
                    //complain("Error launching purchase flow. Another async operation in progress.");
                    complain("Fallo la consulta de compras, inténtalo nuevamente.");
                }
            }
        });

        mIsPremium = pPreferencia.obtenerPremium();


        if(mIsPremium)
        {
            fab.setVisibility(View.INVISIBLE);
        }else {
            fab.setVisibility(View.VISIBLE);
        }

        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkm9vPFE1++S8/47YzmVoualxRetE21bftZoQSWTVIB3dc6jkyph6BRFe90xHWGokuW5ch1Gy4bMkdHP58IuD+ZI9s7WcRMsNsYnP0SrQUH0GIFWTICr97j1eHuRYu8g3LGhCDgy9yHVePOkt4aPRc7OktfRm6SiutrpWIBH5MCI/olOasCgiDX+xBOTbKm2btauU2Al+okvJq4IOyUJ8KLZ1H8nm6mAkwZtDOsLc0HRr1VGE/RwnG9PfrrxVipZB7nSwq212Lcgb8F6CeZVGo7h5HvoXXlmNndjQiLEtswQJ+mmdlDRVsKKuKgSHDWCWi5+zd5gww53pGZbnh+zTcQIDAQAB";
        //Log.d(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(false);

        //Log.d(TAG, "Starting setup.");

        final Globals globalVariable = (Globals) getApplicationContext();
        globalVariable.setInterstitial(this);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    mBilling=false;
                    // Oh noes, there was a problem.
                    //complain("Tu dispositivo no tiene configurado Google Billing: " + result);
                    complain("Tu dispositivo no tiene configurado Google Billing");
                    return;
                }else mBilling=true;

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                mBroadcastReceiver = new IabBroadcastReceiver(listadoCIE.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                registerReceiver(mBroadcastReceiver, broadcastFilter);

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    //complain("Error querying inventory. Another async operation in progress.");
                    complain("Error cargando tu inventario de compras, cierra y vuelve abrir la app.");
                }
            }
        });


        android.support.v4.app.Fragment fragment = new listadoCIECategorias();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerCIE, fragment)
                .commit();


    }

    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                //complain("Failed to query inventory: " + result);
                complain("Fallo la consulta de compras, inténtalo nuevamente.");
                return;
            }

            Log.d(TAG, "Query inventory was successful.");


            Preferencias pPreferencia = new Preferencias(getApplicationContext()) ;
            mIsPremium = pPreferencia.obtenerPremium();

            if(!mIsPremium) {
                Purchase premiumPurchase = inventory.getPurchase(SKU_CIE10_full);
                mIsPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
            }
            pPreferencia.guardarPreferencias(mIsPremium);

            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };

    @Override
    public void receivedBroadcast() {
        // Received a broadcast notification that the inventory of items has changed
        Log.d(TAG, "Received broadcast notification. Querying inventory.");
        try {
            mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            //complain("Error querying inventory. Another async operation in progress.");
            complain("Fallo la consulta de compras, inténtalo nuevamente.");
        }
    }

    /** Verifies the developer payload of a purchase. */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {


        switch (item.getItemId()) {


            case R.id.categoria:
                getSupportActionBar().setSubtitle("Capítulos");
                Fragment newContent4 = new listadoCIECategorias();
                switchContent(newContent4);

                break;

            case R.id.search:
                getSupportActionBar().setSubtitle("Búsqueda");
                Fragment newContent1 = new listadoCIEbusqueda();
                switchContent(newContent1);

                break;

            case R.id.mic:
                getSupportActionBar().setSubtitle("Voz");
                if(isOnline())
                {
                    Fragment newContent2 = new listadoCIEvoz();
                    switchContent(newContent2);

                }else{
                    Toast.makeText(this, " No se ha encontrado conexión a internet para ejecutar esta función..",Toast.LENGTH_LONG).show();}
                break;

            case R.id.com:

                String[] TO = {"informes@evaluometro.pe"}; //Direcciones email  a enviar.
                //String[] CC = {""}; //Direcciones email con copia.

                Intent emailIntent = new Intent(Intent.ACTION_SEND);

                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                //emailIntent.putExtra(Intent.EXTRA_CC, CC);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Descríbemos que mas funcionalidad deserarias");
                //emailIntent.putExtra(Intent.EXTRA_TEXT, "tuemail@email.com"); // * configurar email aquí!

                try {
                    startActivity(Intent.createChooser(emailIntent, "Selecciona programa de correo."));
                    Log.i("EMAIL", "Enviando email...");
                }
                catch (android.content.ActivityNotFoundException e) {
                    Toast.makeText(this, "NO existe ningún cliente de email instalado!.", Toast.LENGTH_SHORT).show();
                }


                break;

            case R.id.favorite:
                //hideSoftKeyBoard();

                getSupportActionBar().setSubtitle("Favoritos");
                Fragment newContent3 = new listadoCIEfavoritos();
                switchContent(newContent3);

                break;

            case R.id.ads:
                String payload = "CIE10";

                try {
                    if (mBilling) {
                        mHelper.launchPurchaseFlow(this, SKU_CIE10_full, RC_REQUEST,
                                mPurchaseFinishedListener, payload);
                    }
                    else
                        complain("Tu dispositivo no tiene configurado Google Billing");
                } catch (IabHelper.IabAsyncInProgressException e) {
                    //complain("Error launching purchase flow. Another async operation in progress.");
                    complain("Fallo la consulta de compras, inténtalo nuevamente.");
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void hideSoftKeyBoard()
    {   Log.e("hideSoftKeyBoard", "preguntar ");
        if(getCurrentFocus()!=null)
        {   Log.e("hideSoftKeyBoard", "cerrar" );
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                //complain("Uy.. algo paso con la compra.");
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                //complain("Error purchasing. Authenticity verification failed.");
                complain("Ummm, autenticación fallida en la compra.");
                return;
            }

            Log.d(TAG, "Purchase successful.");


            if (purchase.getSku().equals(SKU_CIE10_full)) {
                mIsPremium = true;
            }
            pPreferencia.guardarPreferencias(mIsPremium );

        }
    };


    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            // We know this is the "gas" sku because it's the only one we consume,
            // so we don't check which sku was consumed. If you have more than one
            // sku, you probably should check...
            if (result.isSuccess()) {
                // successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit
                Log.d(TAG, "Consumption successful. Provisioning.");
                //mTank = mTank == TANK_MAX ? TANK_MAX : mTank + 1;
                //saveData();
                alert("Gracias por apoyar, ahora vuelve a cargar la opción que gustes. ");
            }
            else {
                //complain("Error while consuming: " + result);
                complain("Error al comprar, si tienes una duda envíanos un correo.: " + result);
            }

            Log.d(TAG, "End consumption flow.");
        }
    };


    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }

        return false;
    }

    public void switchContent(final Fragment fragment) {

        mContent = fragment;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.containerCIE, fragment)
                //.addToBackStack(null)
                .commit();
    }

    void complain(String message) {
        Log.e(TAG, "**** Medicode Error: " + message);
        alert(message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

    // We're being destroyed. It's important to dispose of the helper here!
    @Override
    public void onDestroy() {
        super.onDestroy();

        // very important:
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }

        // very important:
        Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.disposeWhenFinished();
            mHelper = null;
        }
    }


}
