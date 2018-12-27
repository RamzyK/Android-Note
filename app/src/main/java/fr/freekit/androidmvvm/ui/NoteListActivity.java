package fr.freekit.androidmvvm.ui;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;

import org.modelmapper.ModelMapper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.freekit.androidmvvm.R;
import fr.freekit.androidmvvm.bdd.entity.NoteEntity;
import fr.freekit.androidmvvm.rest.ApiWebService;
import fr.freekit.androidmvvm.rest.dto.NoteDto;
import fr.freekit.androidmvvm.ui.favorite_note.favoriteActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class NoteListActivity extends AppCompatActivity {

    public static final int NEW_WORD_ACTIVITY_REQUEST_CODE = 1;
    public int apiResponseSize = 0;
    private String token;

    private NoteViewModel mWordViewModel;
    private NoteListAdapter wordAdaper;
    private NoteDetailVM mNoteDetailVm;
    private ApiWebService service;

    private CompositeDisposable disposable = new CompositeDisposable();
    private MutableLiveData<NetworkInfo.State> state = new MutableLiveData<>();
    private SwipeRefreshLayout refresh;

    private Context c = this;
    @BindView(R.id.recyclerview)
    RecyclerView mWordRecyclerView;

    @BindView(R.id.tv)
    TextView tv;

    @OnClick(R.id.fab)
    public void addNewWord(View view) {
        Intent intent = new Intent(NoteListActivity.this, NoteDetailedActivity.class);
        startActivityForResult(intent, NEW_WORD_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        refresh = findViewById(R.id.refresh);
        refresh.setOnRefreshListener(() -> {
            if(state.getValue().equals(NetworkInfo.State.CONNECTED)){
                updateApiData();
            }else{
                Toast.makeText(NoteListActivity.this, "Unable to refresh, turn on W-fi & try again.", Toast.LENGTH_SHORT).show();
                refresh.setRefreshing(false);
            }
        });
        tv.setVisibility(View.INVISIBLE);
        mWordViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        mNoteDetailVm = ViewModelProviders.of(this).get(NoteDetailVM.class);

        wordAdaper = new NoteListAdapter(this, mWordViewModel, mNoteDetailVm);

        mWordRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mWordRecyclerView.setAdapter(wordAdaper);

        token = getTokenFromPrefs();

        mNoteDetailVm.setToken(token);
        mWordViewModel.setToken(token);

        service = new ApiWebService(this);

        state.observe(this, state1 -> {
            if (state1 == NetworkInfo.State.CONNECTED) {
                getDataFromApi();
            }
        });

        mWordViewModel.getAllWords().observe(this, words -> {
            if (words != null && words.size() > 0 && apiResponseSize >= 0) {
                wordAdaper.setmWords(words);
                tv.setVisibility(View.INVISIBLE);
            } else {
                wordAdaper.setmWords(words);
                tv.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkNetwork();
       //updateApiData();
        //getDataFromApi();

    }

    @Override
    protected void onStop() {
        disposable.clear();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_settings){
            Intent fav = new Intent(this, favoriteActivity.class);
            startActivity(fav);
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkNetwork() {
        disposable.add(ReactiveNetwork.observeNetworkConnectivity(this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(connectivity -> {
                    if(connectivity.state().equals(NetworkInfo.State.CONNECTED)){
                        state.setValue(NetworkInfo.State.CONNECTED);

                    }else{
                        Toast.makeText(c, "Turn Wi-Fi on et try again;", Toast.LENGTH_SHORT).show();
                    }
                }, Timber::e)
        );
    }

    private void updateApiData() {
        disposable.add(service.getService().getNotefromApi()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.body() != null) {
                        int dbNoteList_size = mWordViewModel.getAllWords().getValue().size();
                        int apiNoteList_size = response.body().size();

                        if (dbNoteList_size > apiNoteList_size) {
                            ModelMapper mapper = new ModelMapper();
                            for (int i = 0; i < (dbNoteList_size - apiNoteList_size); i++) {
                                NoteDto newDtoNote = mapper.map(mWordViewModel.getAllWords().getValue().get(i), NoteDto.class);
                                mNoteDetailVm.addNoteApi(c,newDtoNote,
                                        () -> Toast.makeText(NoteListActivity.this, "API updated successfully !", Toast.LENGTH_SHORT).show(), token);
                                mWordViewModel.deleteRx(mWordViewModel.getAllWords().getValue().get(i),
                                        () -> Toast.makeText(NoteListActivity.this, "API & DataBase are sync", Toast.LENGTH_SHORT).show());
                            }
                        }
                    }else{
                        Toast.makeText(NoteListActivity.this, "Error with the server", Toast.LENGTH_SHORT).show();
                    }
                    if(refresh.isRefreshing()){
                        refresh.setRefreshing(false);
                    }
                },Timber::e));

    }

    public void getDataFromApi() {
        if(state.getValue() != null ){
            if (state.getValue().equals(NetworkInfo.State.CONNECTED)) {
                disposable.add(service.getService().getNotefromApi()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            if (response != null) {
                                ModelMapper modelMapper = new ModelMapper();
                                if (response.body() != null) {
                                    apiResponseSize = response.body().size();
                                    for (NoteDto noteDto : response.body()) {
                                        try {
                                            NoteEntity newNote = modelMapper.map(noteDto, NoteEntity.class);
                                            mNoteDetailVm.insertRx(newNote, () -> Timber.d("Success"));
                                        } catch (Exception e) {
                                            Timber.e(e);
                                        }
                                    }
                                }
                            }
                        }, Timber::e));
                updateApiData();
            }else{
                Toast.makeText(this, "Turn on Wi-Fi & try again.", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
        }
    }

    public String getTokenFromPrefs() {
        SharedPreferences sharedPrefs = this.getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        token = sharedPrefs.getString("token", "");

        return token;
    }
}

