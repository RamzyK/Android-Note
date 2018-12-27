package fr.freekit.androidmvvm.bdd.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import java.util.List;

import fr.freekit.androidmvvm.bdd.entity.NoteEntity;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface NoteDao {
    @Transaction
    @Insert
    void insertWord(NoteEntity word);

    @Delete
    void deleteWord(NoteEntity word);

    @Transaction
    @Query("DELETE FROM note")
    void deleteAll();

    @Transaction
    @Query("SELECT * FROM note ORDER BY id ASC")
    LiveData<List<NoteEntity>> getAllWords();

    @Update
    void updateWord(NoteEntity noteEntity);

    /* */

    @Delete
    void delete(NoteEntity noteEntity);

    @Insert
    long insert(NoteEntity noteEntity);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(NoteEntity noteEntity);

    @Transaction
    @Query("SELECT * FROM note WHERE id = :note_id")
    Single<NoteEntity> fid(long note_id);

    @Transaction
    @Query("SELECT * FROM note ORDER BY id ASC")
    Flowable<List<NoteEntity>> getAll();

    @Transaction
    @Query("SELECT * FROM note WHERE favourite = :fav")
    Flowable<List<NoteEntity>> getFavs(boolean fav);

}
