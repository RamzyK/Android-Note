package fr.freekit.androidmvvm.rest.dto;

import android.arch.persistence.room.ColumnInfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NoteDto implements Serializable {

    @SerializedName("content")
    @Expose
    private String content;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("favourite")
    @Expose
    private boolean isFav = false;

    @SerializedName("id")
    @Expose
    private long id;

    public NoteDto() {

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isFav() {
        return isFav;
    }

    public void setFav(boolean fav) {
        isFav = fav;
    }
}
