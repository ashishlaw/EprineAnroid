package com.eprine.propertyClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("jwt")
    @Expose
    private Jwt jwt;
    @SerializedName("chat_username")
    @Expose
    private String chatUsername;
    @SerializedName("chat_password")
    @Expose
    private String chatPassword;
    @SerializedName("therapist_custom_name")
    @Expose
    private String therapistCustomName;

    public Jwt getJwt() {
        return jwt;
    }

    public void setJwt(Jwt jwt) {
        this.jwt = jwt;
    }

    public String getChatUsername() {
        return chatUsername;
    }

    public void setChatUsername(String chatUsername) {
        this.chatUsername = chatUsername;
    }

    public String getChatPassword() {
        return chatPassword;
    }

    public void setChatPassword(String chatPassword) {
        this.chatPassword = chatPassword;
    }

    public String getTherapistCustomName() {
        return therapistCustomName;
    }

    public void setTherapistCustomName(String therapistCustomName) {
        this.therapistCustomName = therapistCustomName;
    }

}