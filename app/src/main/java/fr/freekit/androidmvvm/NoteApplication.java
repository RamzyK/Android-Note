package fr.freekit.androidmvvm;

import android.app.Application;
import android.content.SharedPreferences;

import fr.freekit.androidmvvm.bdd.NoteRoomDataBase;

public class NoteApplication extends Application {

    private NoteRoomDataBase db;
    private static NoteApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        db = NoteRoomDataBase.getDataBase(this);
    }

    public NoteRoomDataBase getDb() {
        if( db == null){
            db = NoteRoomDataBase.getDataBase(this);
        }
        return db;
    }

    public static NoteApplication getINSTANCE() {
        if(instance == null){
            instance = new NoteApplication();
        }
        return instance;
    }
}
