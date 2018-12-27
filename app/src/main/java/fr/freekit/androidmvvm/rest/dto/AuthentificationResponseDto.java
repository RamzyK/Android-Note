package fr.freekit.androidmvvm.rest.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AuthentificationResponseDto implements Serializable {

    @SerializedName("code")
    @Expose
    private int code;

    @SerializedName("accessToken")
    @Expose
    private String accessToken;

    public AuthentificationResponseDto(){

    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
