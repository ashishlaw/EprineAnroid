package com.eprine.propertyClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LoginUsers {
    @SerializedName("user")
    @Expose
    private List<UserDetails> user;
    public List<UserDetails> getUser() {
        return user;
    }

    public void setUser(List<UserDetails> user) {
        this.user = user;
    }


}
