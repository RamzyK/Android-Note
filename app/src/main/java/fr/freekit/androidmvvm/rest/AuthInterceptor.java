package fr.freekit.androidmvvm.rest;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private String token;
    private Activity c;

    private Request originalRequest;
    private SharedPreferences sharedPrefs;

    public AuthInterceptor(Activity ctx){
        c = ctx;
    }
    @Override
    public Response intercept(Chain chain) throws IOException {
        originalRequest = chain.request();


        sharedPrefs = c.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        token = sharedPrefs.getString("token", ""); // Get Token

        if(token != ""){
            originalRequest = originalRequest.newBuilder().addHeader("x-api-key", token).build();
        }
        return chain.proceed(originalRequest);
    }
}
