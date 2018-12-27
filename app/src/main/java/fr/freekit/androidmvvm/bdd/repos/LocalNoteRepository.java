package fr.freekit.androidmvvm.bdd.repos;

import android.arch.lifecycle.LiveData;

import java.util.List;

import fr.freekit.androidmvvm.NoteApplication;
import fr.freekit.androidmvvm.bdd.dao.NoteDao;
import fr.freekit.androidmvvm.bdd.entity.NoteEntity;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import timber.log.Timber;

public class LocalNoteRepository {
    private NoteDao mWordDao;

    public LocalNoteRepository() {
        mWordDao = NoteApplication.getINSTANCE().getDb().wordDao();
    }

    public LiveData<List<NoteEntity>> getAllWords() {
        return mWordDao.getAllWords();
    }

    public Completable deleteRx(NoteEntity noteEntity) {
        return Completable.fromAction(() -> mWordDao.delete(noteEntity));
    }

    public Single<NoteEntity> insertRx(NoteEntity noteEntity) {
        return Single.fromCallable(() -> {
            long id = 0;
            try {
                id = mWordDao.insert(noteEntity);
            } catch (Exception e) {
                Timber.e(e);
            }
            noteEntity.setId(id);
            return noteEntity;
        });
    }

    public Single<NoteEntity> fid(long id) {
        return mWordDao.fid(id);
    }

    public Flowable<List<NoteEntity>> getFavourite(boolean isFav){
        return mWordDao.getFavs(isFav);
    }

    public Completable updateRx(NoteEntity noteEntity) {
        return Completable.fromAction(() -> mWordDao.update(noteEntity));
    }

    public Flowable<List<NoteEntity>> getAll() {
        return mWordDao.getAll();
    }
}
