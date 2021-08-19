package com.applutions.t2y.common;

import android.app.Application;
import android.content.Context;


import com.applutions.t2y.R;
import com.facebook.stetho.Stetho;
import com.google.android.libraries.places.api.Places;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.model.PaymentIntent;

import org.json.JSONObject;

public class TrailerApplication extends Application {

    public static TrailerApplication application;
    public static JSONObject trailerDetailsResponse;


    public static synchronized TrailerApplication getInstance() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        Stetho.initializeWithDefaults(this);
        Places.initialize(getApplicationContext(), getResources().getString(R.string.google_api_key));

        PaymentConfiguration.init(getApplicationContext(), "pk_test_CyIf8HRNFvrXTivqfBr8SIua00dBYroXEr");

    }

    public static TrailerApplication getApplicationInstance() {
        if (application != null)
            return application;
        return null;
    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        //MultiDex.install(this);
    }
}
