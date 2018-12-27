package fr.freekit.androidmvvm.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import fr.freekit.androidmvvm.R;
import fr.freekit.androidmvvm.databinding.UpdateNoteActivityBinding;
import fr.freekit.androidmvvm.rest.dto.NoteDto;

public class NoteDetailedActivity extends AppCompatActivity {


    private NoteDetailVM mNoteDetailVM;
    private long noteId = -1;
    private UpdateNoteActivityBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_note_activity);
        binding = DataBindingUtil.setContentView(this, R.layout.update_note_activity);
        mNoteDetailVM = ViewModelProviders.of(this).get(NoteDetailVM.class);

        if (getIntent() != null) {
            noteId = getIntent().getLongExtra("id", -1);
            if (noteId > -1) {
                mNoteDetailVM.findById(noteId).observe(this, noteEntity -> {
                    if (noteEntity != null) {
                        binding.title.setText(noteEntity.getTitle());
                        binding.description.setText(noteEntity.getContent());
                        binding.setNote(noteEntity);
                    }
                });
            }
        }
    }

    public void saveUpdate(View view) {
        NoteDto updateDto = new NoteDto();
        String title = binding.title.getText().toString();
        String content = binding.description.getText().toString();

        updateDto.setContent(content);
        updateDto.setTitle(title);
        if (noteId > -1) {
            updateDto.setId(noteId);
            mNoteDetailVM.updateNoteAfterNetworkCheck(this, updateDto, () -> finish());
        } else {
            mNoteDetailVM.addNoteAfterNetworkCheck(this, updateDto, () -> finish());
        }
    }
}