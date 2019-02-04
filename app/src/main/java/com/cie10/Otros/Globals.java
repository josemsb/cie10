package com.cie10.Otros;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class Globals extends Application {

    private InterstitialAd mItestitialAd;
    private Context mContext;



    public InterstitialAd getInterstitial() {
        return mItestitialAd;
    }


    public void setInterstitial(Context context) {
        this.mContext = context;
        mItestitialAd = new InterstitialAd(mContext);
        //ca-app-pub-3918734194731544/6167429390
        mItestitialAd.setAdUnitId("ca-app-pub-3918734194731544/6167429390");
        mItestitialAd.loadAd(new AdRequest.Builder().build());

        mItestitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.

                mItestitialAd.loadAd(new AdRequest.Builder().build());
                //Log.d("TAG", "JEJE");
            }

        });
    }

}
