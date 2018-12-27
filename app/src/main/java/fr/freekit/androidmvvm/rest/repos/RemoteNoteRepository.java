package fr.freekit.androidmvvm.rest.repos;

import fr.freekit.androidmvvm.rest.ApiWebService;
import fr.freekit.androidmvvm.rest.dto.NoteDto;
import fr.freekit.androidmvvm.rest.dto.UserDto;
import io.reactivex.Flowable;
import io.reactivex.Single;
import retrofit2.Response;

public class RemoteNoteRepository {

    public Single<Response<NoteDto>> createNote(NoteDto updateDto, String token) {
        return new ApiWebService().getService().createNote(updateDto);
    }

}
