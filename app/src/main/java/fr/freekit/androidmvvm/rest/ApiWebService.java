package fr.freekit.androidmvvm.rest;

import android.app.Activity;

import fr.freekit.androidmvvm.rest.endpoint.NoteInterface;
import fr.freekit.androidmvvm.utils.HttpClientSingleton;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiWebService {

    private NoteInterface service;
    public static final String ENDPOIINT = "https://note-pi-api.herokuapp.com";
    public Activity c;

    public ApiWebService() {
    }

    public ApiWebService(Activity ctx) {
        this.c = ctx;
    }

    public NoteInterface getService() {
        OkHttpClient okHttpClient = HttpClientSingleton.getInstance(c);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ENDPOIINT)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        service = retrofit.create(NoteInterface.class);
        return service;
    }
}
