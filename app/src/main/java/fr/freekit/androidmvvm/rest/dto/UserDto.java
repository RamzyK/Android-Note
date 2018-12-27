package fr.freekit.androidmvvm.rest.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserDto implements Serializable {

    @SerializedName("email")
    @Expose
    String email;

    @SerializedName("pwd")
    @Expose
    String pwd;

    public UserDto(){ }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getPwd() { return pwd; }

    public void setPwd(String pwd) { this.pwd = pwd; }
}
