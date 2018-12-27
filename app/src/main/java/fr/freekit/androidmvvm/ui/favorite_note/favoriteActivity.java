package fr.freekit.androidmvvm.ui.favorite_note;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.freekit.androidmvvm.R;
import fr.freekit.androidmvvm.bdd.repos.LocalNoteRepository;
import fr.freekit.androidmvvm.ui.NoteDetailVM;
import fr.freekit.androidmvvm.ui.NoteListAdapter;
import fr.freekit.androidmvvm.ui.NoteViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class favoriteActivity extends AppCompatActivity {

    private LocalNoteRepository mWordRepository;
    private NoteViewModel mWordViewModel;
    private NoteDetailVM mNoteDetailVm;
    private NoteListAdapter wordAdaper;
    private CompositeDisposable disposable = new CompositeDisposable();

    @BindView(R.id.fav_recyclerView)
    RecyclerView mfavouriteRV;

    @BindView(R.id.fav_refresh)
    SwipeRefreshLayout mFavRefresh;

    @BindView(R.id.fav_tv)
    TextView mEmptyFavsTv;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_activity);

        ButterKnife.bind(this);

        mWordRepository = new LocalNoteRepository();
        mEmptyFavsTv.setVisibility(View.INVISIBLE);

        mWordViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        mNoteDetailVm = ViewModelProviders.of(this).get(NoteDetailVM.class);
        wordAdaper = new NoteListAdapter(this,mWordViewModel, mNoteDetailVm);

        mfavouriteRV.setLayoutManager(new LinearLayoutManager(this));
        mfavouriteRV.setAdapter(wordAdaper);

        applyFavFilter();
    }

    private void applyFavFilter() {
        disposable.add(mWordRepository.getFavourite(true)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(fav -> {
                    if (fav.size() > 0) {
                        wordAdaper.setmWords(fav);
                    }else{
                        mEmptyFavsTv.setVisibility(View.VISIBLE);
                    }
                }, Timber::e));
    }
}
