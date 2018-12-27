package fr.freekit.androidmvvm.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPrefsUtils {

    private static  SharedPreferences sharedPreferences;
    private static SharedPrefsUtils instance;

    public SharedPrefsUtils(Context context) {
        sharedPreferences = context.getSharedPreferences("token", Context.MODE_PRIVATE);

    }

    public static SharedPrefsUtils getInstance(Context context) {
        if(instance == null){
            instance =  new SharedPrefsUtils(context);
            return instance;
        }
         return instance;
    }

    public void put(String key, String value){
        sharedPreferences.edit().putString(key, value).commit();
    }

    public void put(String key, boolean value){
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public String getString(String key){
       return sharedPreferences.getString(key, "");
    }

    public boolean getBoolean(String key){
        return sharedPreferences.getBoolean(key, false);
    }

    public void remove(String key){
        sharedPreferences.edit().remove(key).apply();
    }


}
