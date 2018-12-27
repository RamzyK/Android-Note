package fr.freekit.androidmvvm.ui;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.freekit.androidmvvm.R;
import fr.freekit.androidmvvm.bdd.entity.NoteEntity;
import fr.freekit.androidmvvm.rest.dto.NoteDto;
import timber.log.Timber;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.WordViewHolder> {

    private List<NoteEntity> mWords;
    private Context c;
    private NoteViewModel mNoteVm;
    private NoteDetailVM noteDetailVM;

    private int lastCardHeight;

    public NoteListAdapter(Context context, NoteViewModel noteVm, NoteDetailVM detailVM) {
        mNoteVm = noteVm;
        noteDetailVM = detailVM;
        if (mWords == null) {
            mWords = new ArrayList<>();
        }
        c = context;
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_item, viewGroup, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder wordViewHolder, int i) {
        NoteEntity current = mWords.get(i);

        if (current != null) {
            try {
                ModelMapper modelMapper = new ModelMapper();
                NoteDto noteDto = modelMapper.map(current, NoteDto.class);

                wordViewHolder.delete.setOnClickListener(v -> mNoteVm.deleteFromApi(noteDto, () -> notifyItemRemoved(i)));

                wordViewHolder.unselected_fav.setOnClickListener(v -> {
                    current.setFav(true);
                    NoteDto favNote = modelMapper.map(current, NoteDto.class);
                    noteDetailVM.updateRx(current, () -> Toast.makeText(c, "Note added to favourite list.", Toast.LENGTH_SHORT).show());
                });

                wordViewHolder.selected_fav.setOnClickListener(v -> {
                    current.setFav(false);
                    NoteDto favNote = modelMapper.map(current, NoteDto.class);
                    noteDetailVM.updateRx(current, () -> Toast.makeText(c, "Note deleted from favourite list.", Toast.LENGTH_SHORT).show());
                });

                wordViewHolder.expandMore.setOnClickListener(v -> {
                    ViewGroup.LayoutParams params =  wordViewHolder.descItemView.getLayoutParams();
                    params.height = 700;
                    wordViewHolder.descItemView.setLayoutParams(params);

                    ViewGroup.LayoutParams cardParams = wordViewHolder.card.getLayoutParams();
                    cardParams.height = lastCardHeight;
                    wordViewHolder.card.setLayoutParams(cardParams);

                    wordViewHolder.descItemView.setVisibility(View.VISIBLE);
                    wordViewHolder.expandMore.setVisibility(View.INVISIBLE);
                    wordViewHolder.expandLess.setVisibility(View.VISIBLE);
                });

                wordViewHolder.expandLess.setOnClickListener(v -> {
                    lastCardHeight = wordViewHolder.card.getHeight();

                    ViewGroup.LayoutParams params =  wordViewHolder.descItemView.getLayoutParams();
                    params.height = 0;
                    wordViewHolder.descItemView.setLayoutParams(params);

                    ViewGroup.LayoutParams cardParams = wordViewHolder.card.getLayoutParams();
                    cardParams.height = 130;
                    wordViewHolder.card.setLayoutParams(cardParams);

                    wordViewHolder.expandLess.setVisibility(View.INVISIBLE);
                    wordViewHolder.expandMore.setVisibility(View.VISIBLE);
                });

                wordViewHolder.bind(current);
            } catch (Exception e) {
                Timber.e(e);
            }


        }
    }

    public void setmWords(List<NoteEntity> word) {
        if (word != null) {
            mWords = word;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return mWords.size();
    }






    public class WordViewHolder extends RecyclerView.ViewHolder {
        private NoteEntity word;
        @BindView(R.id.cardView)
        CardView card;

        @BindView(R.id.titre)
        TextView titleItemView;

        @BindView(R.id.desc)
        TextView descItemView;

        @OnClick(R.id.ll)
        public void editNote(View view) {
            Intent intent = new Intent(c, NoteDetailedActivity.class);
            intent.putExtra("id", word.getId());
            c.startActivity(intent);
        }


        @BindView(R.id.notFav)
        ImageButton unselected_fav;

        @BindView(R.id.fav)
        ImageButton selected_fav;

        @BindView(R.id.delete)
        ImageButton delete;

        @BindView(R.id.expandMore)
        ImageButton expandMore;

        @BindView(R.id.expandLess)
        ImageButton expandLess;

        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            selected_fav.setVisibility(View.INVISIBLE);
            expandMore.setVisibility(View.INVISIBLE);
        }

        public void bind(NoteEntity current) {
            word = current;
            if (word != null) {
                titleItemView.setText(current.getTitle());
                descItemView.setText(current.getContent());
                if (current.isFav()) {
                    selected_fav.setVisibility(View.VISIBLE);
                } else {
                    selected_fav.setVisibility(View.INVISIBLE);
                }
            }
        }
    }
}
