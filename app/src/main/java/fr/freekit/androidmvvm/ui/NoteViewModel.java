package fr.freekit.androidmvvm.ui;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.modelmapper.ModelMapper;

import java.util.List;

import fr.freekit.androidmvvm.bdd.entity.NoteEntity;
import fr.freekit.androidmvvm.bdd.repos.LocalNoteRepository;
import fr.freekit.androidmvvm.rest.ApiWebService;
import fr.freekit.androidmvvm.rest.dto.NoteDto;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class NoteViewModel extends ViewModel {

    private LocalNoteRepository mRepository;
    private CompositeDisposable disposable = new CompositeDisposable();
    private MutableLiveData<List<NoteEntity>> mAllWords = new MutableLiveData<>();
    private String token;

    public NoteViewModel() {
        mRepository = new LocalNoteRepository();
    }

    public MutableLiveData<List<NoteEntity>> getAllWords() {
        getAll();
        return mAllWords;
    }

    public void deleteRx(NoteEntity noteEntity, Action onSuccess) {
        disposable.add(mRepository.deleteRx(noteEntity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Timber.d("Success");
                    onSuccess.run();
                }, Timber::e));
    }

    public void getAll() {
        disposable.add(mRepository.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ListNoteEntities -> {
                    if (ListNoteEntities != null) {
                        mAllWords.setValue(ListNoteEntities);
                    }
                }, Timber::e));
    }

    public void deleteFromApi(NoteDto noteDto, Action action) {
        ApiWebService service = new ApiWebService();
        Call<Response<NoteDto>> toDelete = service.getService().deleteNote(noteDto);

        toDelete.enqueue(new Callback<Response<NoteDto>>() {
            @Override
            public void onResponse(Call<Response<NoteDto>> call, Response<Response<NoteDto>> response) {
                ModelMapper modelMapper = new ModelMapper();
                if (response != null) {
                    try {
                        NoteEntity noteEntity = modelMapper.map(noteDto, NoteEntity.class);
                        deleteRx(noteEntity, action);
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Response<NoteDto>> call, Throwable t) {
                Timber.e(t);
            }
        });
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}