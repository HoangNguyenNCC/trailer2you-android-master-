package com.applutions.t2y.data.network;

import android.content.Context;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.applutions.t2y.utils.Constants.BASE_URL;

public class RetrofitInstance {
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance(Context context) {
        OkHttpClient client = getOkHttpClient(context);
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private static OkHttpClient getOkHttpClient(Context context) {
        return new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .readTimeout(60,TimeUnit.SECONDS)
                .callTimeout(60,TimeUnit.SECONDS)
                .addInterceptor(new HeaderInterceptor(context))
                .build();
    }

}
