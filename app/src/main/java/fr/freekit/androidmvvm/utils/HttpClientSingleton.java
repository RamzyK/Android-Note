package fr.freekit.androidmvvm.utils;

import android.app.Activity;

import java.util.concurrent.TimeUnit;

import fr.freekit.androidmvvm.rest.AuthInterceptor;
import okhttp3.OkHttpClient;

public class HttpClientSingleton {

    private static OkHttpClient instance = null;

    private HttpClientSingleton(){

    }

    public static OkHttpClient getInstance(Activity c) {
        if (instance == null) {
           instance = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(c))
                    .readTimeout(50000, TimeUnit.MILLISECONDS)
                    .writeTimeout(50000, TimeUnit.MILLISECONDS).build();
        }
        return instance;
    }
}
