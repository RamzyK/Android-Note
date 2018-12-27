package fr.freekit.androidmvvm.bdd;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import fr.freekit.androidmvvm.bdd.dao.NoteDao;
import fr.freekit.androidmvvm.bdd.entity.NoteEntity;

@Database(entities = {NoteEntity.class}, version = 3, exportSchema = false)
public abstract class NoteRoomDataBase extends RoomDatabase {

    public abstract NoteDao wordDao();

    private static volatile NoteRoomDataBase INSTANCE;

    public static NoteRoomDataBase getDataBase(Context context) {
        if (INSTANCE == null) {
            synchronized (NoteRoomDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), NoteRoomDataBase.class, "note")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
