package fr.freekit.androidmvvm.rest.endpoint;

import java.util.List;

import fr.freekit.androidmvvm.rest.dto.AuthentificationResponseDto;
import fr.freekit.androidmvvm.rest.dto.NoteDto;
import fr.freekit.androidmvvm.rest.dto.UserDto;
import io.reactivex.Flowable;
import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface NoteInterface {

    @Headers({"Content-Type: application/json"})
    @POST("/note/add")
    Single<Response<NoteDto>> createNote(@Body NoteDto noteDto);

    @Headers({"Content-Type: application/json"})
    @GET("/note/list")
    Flowable<Response<List<NoteDto>>> getNotefromApi();

    @Headers({"Content-Type: application/json"})
    @POST("/note/update")
    Call<Response<NoteDto>> updateNote(@Body NoteDto noteDto);

    @Headers({"Content-Type: application/json"})
    @HTTP(method = "DELETE", path = "/note/delete", hasBody = true)
    Call<Response<NoteDto>> deleteNote(@Body NoteDto noteDto);

    @Headers({"Content-Type: application/json"})
    @POST("/auth/login")
    Flowable<Response<AuthentificationResponseDto>> getAccess(@Body UserDto user);

}
