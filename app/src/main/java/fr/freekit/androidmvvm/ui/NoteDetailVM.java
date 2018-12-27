package fr.freekit.androidmvvm.ui;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;

import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

import fr.freekit.androidmvvm.bdd.entity.NoteEntity;
import fr.freekit.androidmvvm.bdd.repos.LocalNoteRepository;
import fr.freekit.androidmvvm.rest.ApiWebService;
import fr.freekit.androidmvvm.rest.dto.NoteDto;
import fr.freekit.androidmvvm.rest.repos.RemoteNoteRepository;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class NoteDetailVM extends ViewModel {
    private final static ArrayList<Integer> errors = new ArrayList<Integer>()
    {{
        add(301);
        add(302);
        add(401);
        add(403);
        add(404);
        add(500);
        add(503);
        add(504);
    }};
    private CompositeDisposable disposable = new CompositeDisposable();
    private MutableLiveData<List<NoteEntity>> mWordsMLD = new MutableLiveData<>();
    private MutableLiveData<NoteEntity> noteEntityMutableLiveData = new MutableLiveData<>();
    private LocalNoteRepository mWordRepository;
    private String token;

    public NoteDetailVM() {
        mWordRepository = new LocalNoteRepository();

    }

    public MutableLiveData<List<NoteEntity>> getAll() {
        mWordsMLD.setValue(mWordRepository.getAllWords().getValue());
        return mWordsMLD;
    }


    public MutableLiveData<NoteEntity> findById(long id) {
        fidRx(id);
        return noteEntityMutableLiveData;
    }
    /**/

    public void updateRx(NoteEntity noteEntity, Action action) {
        disposable.add(mWordRepository.updateRx(noteEntity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Timber.d("Successfully updated");
                    action.run();
                }, Timber::e));
    }

    public void insertRx(NoteEntity noteEntity, Action action) {
        disposable.add(mWordRepository.insertRx(noteEntity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(note -> {
                    if (note != null) {
                        action.run();
                    }
                }, Timber::e));
    }


    public void fidRx(long id) {
        disposable.add(mWordRepository.fid(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(noteEntity -> {
                    if (noteEntity != null) {
                        noteEntityMutableLiveData.setValue(noteEntity);
                    }
                }, Timber::e));
    }

    public void updateNoteAfterNetworkCheck(Context c, NoteDto updateDto, Action action) {
        disposable.add(ReactiveNetwork.observeNetworkConnectivity(c)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(connectivity -> {
                    if (connectivity.available()) {
                        updateNoteApi(c,updateDto, action);
                    } else {
                        try {
                            NoteEntity update_entity = new ModelMapper().map(updateDto, NoteEntity.class);
                            updateRx(update_entity, action);
                        } catch (Exception e) {
                            Timber.e(e);
                        }
                    }
                }, Timber::e)
        );

    }

    public void addNoteAfterNetworkCheck(Context c , NoteDto updateDto, Action action){
       disposable.add(ReactiveNetwork.observeNetworkConnectivity(c)
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(connectivity -> {
                   if(connectivity.available()){
                       token = c.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE).getString("token", "");
                       addNoteApi(c,updateDto, action, token);
                   }else{
                       try {
                           NoteEntity update_entity = new ModelMapper().map(updateDto,NoteEntity.class);
                           insertRx(update_entity, action);
                       } catch (Exception e) {
                           Timber.e(e);
                       }
                   }
               }, Timber::e)
       );

    }

    public void updateNoteApi(Context c, NoteDto updateDto, Action action) {
        ApiWebService service = new ApiWebService();
        Call<Response<NoteDto>> note = service.getService().updateNote(updateDto);
        note.enqueue(new Callback<Response<NoteDto>>() {
            @Override
            public void onResponse(Call<Response<NoteDto>> call, Response<Response<NoteDto>> response) {
                if (response != null && note != null) {
                    ModelMapper modelMapper = new ModelMapper();
                    try {
                        NoteEntity update_entity = modelMapper.map(updateDto, NoteEntity.class);
                        updateRx(update_entity, action);
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }else{
                    try {
                        Toast.makeText(c, "Error while getting data from API!", Toast.LENGTH_SHORT).show();
                        action.run();
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

    public void addNoteApi(Context c, NoteDto updateDto, Action action, String tok) {
        disposable.add(new RemoteNoteRepository().createNote(updateDto, tok)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((Response<NoteDto> noteDtoResponse) -> {
                    if (noteDtoResponse.body() != null && updateDto != null ) {
                        try {
                            action.run();
                        } catch (Exception e) {
                            Timber.e(e);
                        }
                    }else if(errors.contains(noteDtoResponse)){
                        Toast.makeText(c, "Error while getting data from API!", Toast.LENGTH_SHORT).show();
                        action.run();
                    }
                }, Timber::e));
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
